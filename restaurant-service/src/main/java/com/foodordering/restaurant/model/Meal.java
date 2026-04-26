package com.foodordering.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Meal {
    private String mealId;
    private String name;
    private double unitPrice;
    private boolean inStock;
}
