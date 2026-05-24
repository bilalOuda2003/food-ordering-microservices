package com.foodordering.kitchen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodordering.kitchen.model.KitchenOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class KitchenEventProducerService {

    private static final String TOPIC = "food-ordering.food-ready";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void publishFoodReady(KitchenOrder kitchenOrder) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventId", "EVT-KIT-" + System.currentTimeMillis());
            event.put("eventType", "FOOD_READY");
            event.put("eventVersion", "1.0");
            event.put("timestamp", LocalDateTime.now().toString());

            Map<String, Object> payload = new HashMap<>();
            payload.put("kitchenOrderId", kitchenOrder.getKitchenOrderId());
            payload.put("orderId", kitchenOrder.getOrderId());
            payload.put("restaurantId", kitchenOrder.getRestaurantId());
            payload.put("status", kitchenOrder.getStatus().name());
            payload.put("completedAt", kitchenOrder.getCompletedAt() != null ? kitchenOrder.getCompletedAt().toString() : null);

            event.put("payload", payload);

            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, kitchenOrder.getOrderId(), message);
            System.out.println("[KitchenProducer] Published FOOD_READY for order: " + kitchenOrder.getOrderId());

        } catch (Exception e) {
            System.err.println("[KitchenProducer] Failed to publish event: " + e.getMessage());
        }
    }
}
