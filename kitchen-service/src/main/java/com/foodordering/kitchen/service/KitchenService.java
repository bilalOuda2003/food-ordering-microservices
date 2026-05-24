package com.foodordering.kitchen.service;

import com.foodordering.kitchen.dto.FoodPreparationEvent;
import com.foodordering.kitchen.model.KitchenOrder;
import com.foodordering.kitchen.repository.KitchenOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class KitchenService {

    @Autowired
    private KitchenOrderRepository kitchenOrderRepository;

    @Autowired
    private KitchenEventProducerService producerService;

    public KitchenOrder startPreparation(FoodPreparationEvent event) {
        FoodPreparationEvent.Payload payload = event.getPayload();

        Optional<KitchenOrder> existing = kitchenOrderRepository.findByOrderId(payload.getOrderId());
        if (existing.isPresent()) {
            System.out.println("[KitchenService] Order already received: " + payload.getOrderId());
            return existing.get();
        }

        KitchenOrder kitchenOrder = new KitchenOrder();
        kitchenOrder.setOrderId(payload.getOrderId());
        kitchenOrder.setRestaurantId(payload.getRestaurantId());
        kitchenOrder.setPriority(payload.getPriority() != null ? payload.getPriority() : "NORMAL");
        kitchenOrder.setSpecialInstructions(payload.getSpecialInstructions());
        kitchenOrder.setStatus(KitchenOrder.KitchenStatus.RECEIVED);

        KitchenOrder saved = kitchenOrderRepository.save(kitchenOrder);
        System.out.println("[KitchenService] Order RECEIVED in kitchen: " + payload.getOrderId());

        // Simulate async preparation (in real system this would be async)
        simulatePreparation(saved);

        return saved;
    }

    public KitchenOrder markAsReady(String orderId) {
        KitchenOrder kitchenOrder = kitchenOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Kitchen order not found for: " + orderId));

        kitchenOrder.setStatus(KitchenOrder.KitchenStatus.READY);
        kitchenOrder.setCompletedAt(LocalDateTime.now());
        KitchenOrder saved = kitchenOrderRepository.save(kitchenOrder);

        // Publish FOOD_READY event
        producerService.publishFoodReady(saved);

        System.out.println("[KitchenService] Food READY for order: " + orderId);
        return saved;
    }

    public List<KitchenOrder> getAllOrders() {
        return kitchenOrderRepository.findAll();
    }

    public List<KitchenOrder> getOrdersByStatus(String status) {
        return kitchenOrderRepository.findByStatus(KitchenOrder.KitchenStatus.valueOf(status));
    }

    public Optional<KitchenOrder> getOrderByOrderId(String orderId) {
        return kitchenOrderRepository.findByOrderId(orderId);
    }

    private void simulatePreparation(KitchenOrder order) {
        // Update to PREPARING immediately
        order.setStatus(KitchenOrder.KitchenStatus.PREPARING);
        order.setStartedAt(LocalDateTime.now());
        kitchenOrderRepository.save(order);
        System.out.println("[KitchenService] Preparation STARTED for order: " + order.getOrderId());
        // In a real system, a scheduler would mark it READY after some time
    }
}
