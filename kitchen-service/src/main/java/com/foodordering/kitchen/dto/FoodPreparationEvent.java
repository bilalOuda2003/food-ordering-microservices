package com.foodordering.kitchen.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FoodPreparationEvent {

    private String eventId;
    private String eventType;
    private String eventVersion;
    private String timestamp;
    private Payload payload;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payload {
        private String orderId;
        private String restaurantId;
        private String priority;
        private List<Item> items;
        private String specialInstructions;

        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }

        public String getRestaurantId() { return restaurantId; }
        public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }

        public List<Item> getItems() { return items; }
        public void setItems(List<Item> items) { this.items = items; }

        public String getSpecialInstructions() { return specialInstructions; }
        public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String mealId;
        private String name;
        private Integer quantity;

        public String getMealId() { return mealId; }
        public void setMealId(String mealId) { this.mealId = mealId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getEventVersion() { return eventVersion; }
    public void setEventVersion(String eventVersion) { this.eventVersion = eventVersion; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public Payload getPayload() { return payload; }
    public void setPayload(Payload payload) { this.payload = payload; }
}
