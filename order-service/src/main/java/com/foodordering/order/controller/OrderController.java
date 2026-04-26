package com.foodordering.order.controller;

import com.foodordering.order.dto.CreateOrderRequest;
import com.foodordering.order.dto.ErrorResponse;
import com.foodordering.order.dto.OrderResponse;
import com.foodordering.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    /**
     * POST /api/v1/orders
     * Creates a new order after validating meal availability via gRPC.
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
        log.info("POST /api/v1/orders – customerId={}", request.getCustomerId());
        try {
            OrderResponse response = orderService.createOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException e) {
            // Meals unavailable
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ErrorResponse(422, "Unprocessable Entity",
                            e.getMessage(), Instant.now()));
        } catch (Exception e) {
            log.error("Error creating order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Internal Server Error",
                            "An unexpected error occurred", Instant.now()));
        }
    }

    /**
     * GET /api/v1/orders/{orderId}
     * Retrieves order status and details.
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable String orderId) {
        log.info("GET /api/v1/orders/{}", orderId);
        try {
            OrderResponse response = orderService.getOrder(orderId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Not Found",
                            e.getMessage(), Instant.now()));
        }
    }
}
