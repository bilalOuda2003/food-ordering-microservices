package com.foodordering.order.model;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class Order {
    private String orderId;
    private String customerId;
    private String restaurantId;
    private String restaurantName;
    private OrderStatus status;
    private double totalAmount;
    private String currency;
    private String paymentMethod;
    private DeliveryAddress deliveryAddress;
    private List<OrderItem> items;
    private Instant createdAt;

    @Data
    @Builder
    public static class DeliveryAddress {
        private String street;
        private String city;
        private String postalCode;
    }

    @Data
    @Builder
    public static class OrderItem {
        private String mealId;
        private String name;
        private int quantity;
        private double unitPrice;
    }

    public enum OrderStatus {
        PENDING, CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
    }
}
