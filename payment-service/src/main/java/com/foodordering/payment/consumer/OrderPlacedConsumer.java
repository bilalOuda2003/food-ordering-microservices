package com.foodordering.payment.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodordering.payment.dto.OrderPlacedEvent;
import com.foodordering.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderPlacedConsumer {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "food-ordering.order-placed", groupId = "payment-service-group")
    public void consume(String message) {
        try {
            System.out.println("[PaymentConsumer] Received ORDER_PLACED event: " + message);
            OrderPlacedEvent event = objectMapper.readValue(message, OrderPlacedEvent.class);
            paymentService.processPayment(event);
        } catch (Exception e) {
            System.err.println("[PaymentConsumer] Error processing event: " + e.getMessage());
        }
    }
}
