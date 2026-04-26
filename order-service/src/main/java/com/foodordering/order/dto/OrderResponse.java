package com.foodordering.order.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private String orderId;
    private String status;
    private String customerId;
    private String restaurantId;
    private String restaurantName;
    private double totalAmount;
    private String currency;
    private String estimatedDelivery;
    private Instant createdAt;
    private List<OrderItemResponse> items;

    @Data
    @Builder
    public static class OrderItemResponse {
        private String mealId;
        private String name;
        private int quantity;
        private double unitPrice;
        private double subtotal;
    }
}
