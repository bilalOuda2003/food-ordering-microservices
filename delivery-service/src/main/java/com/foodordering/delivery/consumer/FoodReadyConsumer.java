package com.foodordering.delivery.consumer;

import com.foodordering.delivery.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FoodReadyConsumer {

    @Autowired
    private DeliveryService deliveryService;

    @KafkaListener(topics = "food-ordering.food-ready", groupId = "delivery-service-group")
    public void consume(String message) {
        System.out.println("[DeliveryConsumer] Received FOOD_READY event: " + message);
        deliveryService.handleFoodReady(message);
    }
}
