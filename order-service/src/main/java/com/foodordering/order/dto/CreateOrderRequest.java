package com.foodordering.order.dto;

import lombok.Data;
import java.util.List;

// ── Incoming Request from Customer App ──────────────────────────
@Data
public class CreateOrderRequest {
    private String customerId;
    private String restaurantId;
    private Address deliveryAddress;
    private List<OrderItemRequest> items;
    private String paymentMethod;

    @Data
    public static class Address {
        private String street;
        private String city;
        private String postalCode;
    }

    @Data
    public static class OrderItemRequest {
        private String mealId;
        private String name;
        private int quantity;
    }
}
