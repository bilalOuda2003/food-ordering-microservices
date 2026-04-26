package com.foodordering.order.service;

import com.foodordering.order.client.RestaurantGrpcClient;
import com.foodordering.order.dto.CreateOrderRequest;
import com.foodordering.order.dto.OrderResponse;
import com.foodordering.order.model.Order;
import com.foodordering.restaurant.grpc.AvailabilityResponse;
import com.foodordering.restaurant.grpc.MealDetail;
import com.foodordering.restaurant.grpc.MealItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final RestaurantGrpcClient restaurantGrpcClient;

    // In-memory order store (replace with DB in production)
    private final Map<String, Order> orderStore = new ConcurrentHashMap<>();

    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for customer={}, restaurant={}",
                request.getCustomerId(), request.getRestaurantId());

        // ── Step 1: Build gRPC meal items ──────────────────────────
        List<MealItem> grpcItems = request.getItems().stream()
                .map(item -> MealItem.newBuilder()
                        .setMealId(item.getMealId())
                        .setQuantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        // ── Step 2: Call Restaurant Service via gRPC ───────────────
        AvailabilityResponse availability =
                restaurantGrpcClient.checkAvailability(request.getRestaurantId(), grpcItems);

        if (!availability.getAvailable()) {
            throw new IllegalStateException(
                    "Order cannot be placed: " + availability.getMessage());
        }

        // ── Step 3: Map gRPC response to order items ───────────────
        Map<String, MealDetail> mealDetailMap = availability.getMealsList().stream()
                .collect(Collectors.toMap(MealDetail::getMealId, md -> md));

        List<Order.OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;

        for (CreateOrderRequest.OrderItemRequest reqItem : request.getItems()) {
            MealDetail detail = mealDetailMap.get(reqItem.getMealId());
            double unitPrice = (detail != null) ? detail.getUnitPrice() : 0.0;
            String mealName  = (detail != null) ? detail.getName() : reqItem.getName();

            orderItems.add(Order.OrderItem.builder()
                    .mealId(reqItem.getMealId())
                    .name(mealName)
                    .quantity(reqItem.getQuantity())
                    .unitPrice(unitPrice)
                    .build());

            totalAmount += unitPrice * reqItem.getQuantity();
        }

        // ── Step 4: Persist order ──────────────────────────────────
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Order order = Order.builder()
                .orderId(orderId)
                .customerId(request.getCustomerId())
                .restaurantId(request.getRestaurantId())
                .restaurantName(availability.getRestaurantName())
                .status(Order.OrderStatus.CONFIRMED)
                .totalAmount(totalAmount)
                .currency("ILS")
                .paymentMethod(request.getPaymentMethod())
                .deliveryAddress(Order.DeliveryAddress.builder()
                        .street(request.getDeliveryAddress().getStreet())
                        .city(request.getDeliveryAddress().getCity())
                        .postalCode(request.getDeliveryAddress().getPostalCode())
                        .build())
                .items(orderItems)
                .createdAt(Instant.now())
                .build();

        orderStore.put(orderId, order);
        log.info("Order created successfully: orderId={}, total={}", orderId, totalAmount);

        // ── Step 5: Build response ─────────────────────────────────
        return toResponse(order);
    }

    public OrderResponse getOrder(String orderId) {
        Order order = orderStore.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }
        return toResponse(order);
    }

    // ── Mapper ─────────────────────────────────────────────────────
    private OrderResponse toResponse(Order order) {
        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderResponse.OrderItemResponse.builder()
                        .mealId(item.getMealId())
                        .name(item.getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getUnitPrice() * item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus().name())
                .customerId(order.getCustomerId())
                .restaurantId(order.getRestaurantId())
                .restaurantName(order.getRestaurantName())
                .totalAmount(order.getTotalAmount())
                .currency(order.getCurrency())
                .estimatedDelivery("30 min")
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }
}
