package com.foodordering.kitchen.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodordering.kitchen.dto.FoodPreparationEvent;
import com.foodordering.kitchen.service.KitchenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KitchenQueueConsumer {

    @Autowired
    private KitchenService kitchenService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "food-ordering.kitchen-queue", groupId = "kitchen-service-group")
    public void consume(String message) {
        try {
            System.out.println("[KitchenConsumer] Received FOOD_PREPARATION_REQUESTED: " + message);
            FoodPreparationEvent event = objectMapper.readValue(message, FoodPreparationEvent.class);
            kitchenService.startPreparation(event);
        } catch (Exception e) {
            System.err.println("[KitchenConsumer] Error processing event: " + e.getMessage());
        }
    }
}
