package com.foodordering.notification.consumer;

import com.foodordering.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "food-ordering.order-placed", groupId = "notification-service-group")
    public void consumeOrderPlaced(String message) {
        System.out.println("[NotificationConsumer] Received ORDER_PLACED event");
        notificationService.handleOrderPlaced(message);
    }

    @KafkaListener(topics = "food-ordering.payment-processed", groupId = "notification-service-group")
    public void consumePaymentProcessed(String message) {
        System.out.println("[NotificationConsumer] Received PAYMENT_PROCESSED event");
        notificationService.handlePaymentProcessed(message);
    }

    @KafkaListener(topics = "food-ordering.food-ready", groupId = "notification-service-group")
    public void consumeFoodReady(String message) {
        System.out.println("[NotificationConsumer] Received FOOD_READY event");
        notificationService.handleFoodReady(message);
    }
}
