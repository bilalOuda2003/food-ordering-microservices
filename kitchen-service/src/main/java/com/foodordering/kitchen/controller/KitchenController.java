package com.foodordering.kitchen.controller;

import com.foodordering.kitchen.model.KitchenOrder;
import com.foodordering.kitchen.service.KitchenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kitchen")
public class KitchenController {

    @Autowired
    private KitchenService kitchenService;

    @GetMapping
    public ResponseEntity<List<KitchenOrder>> getAllOrders() {
        return ResponseEntity.ok(kitchenService.getAllOrders());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<KitchenOrder> getOrderByOrderId(@PathVariable String orderId) {
        return kitchenService.getOrderByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<KitchenOrder>> getOrdersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(kitchenService.getOrdersByStatus(status.toUpperCase()));
    }

    @PostMapping("/ready/{orderId}")
    public ResponseEntity<KitchenOrder> markAsReady(@PathVariable String orderId) {
        try {
            KitchenOrder ready = kitchenService.markAsReady(orderId);
            return ResponseEntity.ok(ready);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Kitchen Service is running on port 8083");
    }
}
