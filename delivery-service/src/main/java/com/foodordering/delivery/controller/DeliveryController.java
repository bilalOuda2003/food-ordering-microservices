package com.foodordering.delivery.controller;

import com.foodordering.delivery.dto.DeliveryRequest;
import com.foodordering.delivery.model.Delivery;
import com.foodordering.delivery.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<Delivery> assignDelivery(@RequestBody DeliveryRequest request) {
        Delivery delivery = deliveryService.assignDelivery(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(delivery);
    }

    @GetMapping
    public ResponseEntity<List<Delivery>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Delivery> getDeliveryByOrderId(@PathVariable String orderId) {
        return deliveryService.getDeliveryByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Delivery>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByStatus(status));
    }

    @PutMapping("/order/{orderId}/status")
    public ResponseEntity<Delivery> updateStatus(@PathVariable String orderId,
                                                  @RequestParam String status) {
        try {
            Delivery updated = deliveryService.updateStatus(orderId, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Delivery Service is running on port 8085");
    }
}
