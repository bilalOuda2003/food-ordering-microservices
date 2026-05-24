package com.foodordering.payment.service;

import com.foodordering.payment.dto.OrderPlacedEvent;
import com.foodordering.payment.model.Payment;
import com.foodordering.payment.producer.PaymentEventProducerService;
import com.foodordering.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentEventProducerService producerService;

    public Payment processPayment(OrderPlacedEvent event) {
        OrderPlacedEvent.Payload payload = event.getPayload();

        // Check if payment already processed (idempotency)
        Optional<Payment> existing = paymentRepository.findByOrderId(payload.getOrderId());
        if (existing.isPresent()) {
            System.out.println("[PaymentService] Payment already processed for order: " + payload.getOrderId());
            return existing.get();
        }

        Payment payment = new Payment();
        payment.setOrderId(payload.getOrderId());
        payment.setCustomerId(payload.getCustomerId());
        payment.setAmount(payload.getTotalAmount());
        payment.setCurrency(payload.getCurrency() != null ? payload.getCurrency() : "ILS");
        payment.setPaymentMethod(payload.getPaymentMethod());
        payment.setStatus(Payment.PaymentStatus.PENDING);

        // Simulate payment processing
        boolean success = simulatePaymentGateway(payload.getTotalAmount(), payload.getPaymentMethod());

        if (success) {
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            payment.setTransactionRef("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            payment.setProcessedAt(LocalDateTime.now());
            System.out.println("[PaymentService] Payment SUCCESS for order: " + payload.getOrderId());
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            System.out.println("[PaymentService] Payment FAILED for order: " + payload.getOrderId());
        }

        Payment saved = paymentRepository.save(payment);

        // Publish payment result event to Kafka
        producerService.publishPaymentProcessed(saved);

        return saved;
    }

    public Payment refundPayment(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        payment.setProcessedAt(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);
        System.out.println("[PaymentService] Payment REFUNDED for order: " + orderId);
        return saved;
    }

    public Optional<Payment> getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    private boolean simulatePaymentGateway(Double amount, String method) {
        // Simulate: 90% success rate
        return Math.random() > 0.1;
    }
}
