package com.foodordering.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodordering.notification.model.Notification;
import com.foodordering.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public void handleOrderPlaced(String rawEvent) {
        try {
            Map<String, Object> event = objectMapper.readValue(rawEvent, Map.class);
            Map<String, Object> payload = (Map<String, Object>) event.get("payload");

            String orderId = (String) payload.get("orderId");
            String customerId = (String) payload.get("customerId");
            String customerEmail = (String) payload.get("customerEmail");
            String customerPhone = (String) payload.get("customerPhone");
            Object totalAmount = payload.get("totalAmount");

            sendNotification(orderId, customerId, customerEmail, customerPhone,
                    "ORDER_PLACED",
                    "Your order " + orderId + " has been placed successfully! Total: " + totalAmount + " ILS",
                    "EMAIL");

            sendNotification(orderId, customerId, customerEmail, customerPhone,
                    "ORDER_PLACED",
                    "Order " + orderId + " confirmed. We will notify you when ready.",
                    "SMS");

        } catch (Exception e) {
            System.err.println("[NotificationService] Error handling ORDER_PLACED: " + e.getMessage());
        }
    }

    public void handlePaymentProcessed(String rawEvent) {
        try {
            Map<String, Object> event = objectMapper.readValue(rawEvent, Map.class);
            Map<String, Object> payload = (Map<String, Object>) event.get("payload");

            String orderId = (String) payload.get("orderId");
            String customerId = (String) payload.get("customerId");
            String status = (String) payload.get("status");

            String message = "SUCCESS".equals(status)
                    ? "Payment for order " + orderId + " was successful!"
                    : "Payment for order " + orderId + " FAILED. Please retry.";

            sendNotification(orderId, customerId, null, null,
                    "PAYMENT_" + status, message, "EMAIL");

        } catch (Exception e) {
            System.err.println("[NotificationService] Error handling PAYMENT_PROCESSED: " + e.getMessage());
        }
    }

    public void handleFoodReady(String rawEvent) {
        try {
            Map<String, Object> event = objectMapper.readValue(rawEvent, Map.class);
            Map<String, Object> payload = (Map<String, Object>) event.get("payload");

            String orderId = (String) payload.get("orderId");

            sendNotification(orderId, null, null, null,
                    "FOOD_READY",
                    "Your food for order " + orderId + " is ready and being picked up for delivery!",
                    "PUSH");

        } catch (Exception e) {
            System.err.println("[NotificationService] Error handling FOOD_READY: " + e.getMessage());
        }
    }

    private void sendNotification(String orderId, String customerId, String email,
                                   String phone, String type, String message, String channel) {
        Notification notification = new Notification();
        notification.setOrderId(orderId);
        notification.setCustomerId(customerId);
        notification.setCustomerEmail(email);
        notification.setCustomerPhone(phone);
        notification.setNotificationType(type);
        notification.setMessage(message);
        notification.setChannel(channel);
        notification.setStatus(Notification.NotificationStatus.PENDING);

        // Simulate sending
        System.out.println("[NotificationService] Sending " + channel + " to customer: " + message);
        notification.setStatus(Notification.NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());

        notificationRepository.save(notification);
        System.out.println("[NotificationService] Notification SENT via " + channel + " for order: " + orderId);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getNotificationsByOrderId(String orderId) {
        return notificationRepository.findByOrderId(orderId);
    }
}
