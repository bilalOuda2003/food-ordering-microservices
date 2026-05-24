package com.foodordering.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodordering.payment.model.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentEventProducerService {

    private static final String TOPIC = "food-ordering.payment-processed";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void publishPaymentProcessed(Payment payment) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventId", "EVT-PAY-" + System.currentTimeMillis());
            event.put("eventType", "PAYMENT_PROCESSED");
            event.put("eventVersion", "1.0");
            event.put("timestamp", LocalDateTime.now().toString());

            Map<String, Object> payload = new HashMap<>();
            payload.put("paymentId", payment.getPaymentId());
            payload.put("orderId", payment.getOrderId());
            payload.put("customerId", payment.getCustomerId());
            payload.put("amount", payment.getAmount());
            payload.put("currency", payment.getCurrency());
            payload.put("status", payment.getStatus().name());
            payload.put("transactionRef", payment.getTransactionRef());
            payload.put("processedAt", payment.getProcessedAt() != null ? payment.getProcessedAt().toString() : null);

            event.put("payload", payload);

            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, payment.getOrderId(), message);
            System.out.println("[PaymentProducer] Published PAYMENT_PROCESSED for order: " + payment.getOrderId());

        } catch (Exception e) {
            System.err.println("[PaymentProducer] Failed to publish event: " + e.getMessage());
        }
    }
}
