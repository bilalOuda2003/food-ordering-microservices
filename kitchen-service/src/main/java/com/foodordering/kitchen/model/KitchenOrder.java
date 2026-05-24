package com.foodordering.kitchen.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "kitchen_orders")
public class KitchenOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String kitchenOrderId;

    private String orderId;
    private String restaurantId;
    private String priority;
    private String specialInstructions;

    @Enumerated(EnumType.STRING)
    private KitchenStatus status;

    private LocalDateTime receivedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public enum KitchenStatus {
        RECEIVED, PREPARING, READY, FAILED
    }

    @PrePersist
    protected void onCreate() {
        receivedAt = LocalDateTime.now();
        if (kitchenOrderId == null) {
            kitchenOrderId = "KIT-" + System.currentTimeMillis();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getKitchenOrderId() { return kitchenOrderId; }
    public void setKitchenOrderId(String kitchenOrderId) { this.kitchenOrderId = kitchenOrderId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public KitchenStatus getStatus() { return status; }
    public void setStatus(KitchenStatus status) { this.status = status; }

    public LocalDateTime getReceivedAt() { return receivedAt; }
    public void setReceivedAt(LocalDateTime receivedAt) { this.receivedAt = receivedAt; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
