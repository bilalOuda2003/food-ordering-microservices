package com.foodordering.restaurant.service;

import com.foodordering.restaurant.model.Meal;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MealService {

    // In-memory meal catalog (simulates a database)
    private static final Map<String, Meal> MEAL_CATALOG = new HashMap<>();

    static {
        MEAL_CATALOG.put("M-501", new Meal("M-501", "Chicken Shawarma", 15.00, true));
        MEAL_CATALOG.put("M-303", new Meal("M-303", "Falafel Wrap",      8.00, true));
        MEAL_CATALOG.put("M-102", new Meal("M-102", "Grilled Fish",      22.00, true));
        MEAL_CATALOG.put("M-404", new Meal("M-404", "Hummus Plate",       6.00, false)); // out of stock
        MEAL_CATALOG.put("M-205", new Meal("M-205", "Mixed Grill",       35.00, true));
    }

    public Optional<Meal> findById(String mealId) {
        return Optional.ofNullable(MEAL_CATALOG.get(mealId));
    }

    public String getRestaurantName(String restaurantId) {
        // In a real system this would query a restaurant DB
        return switch (restaurantId) {
            case "R-202" -> "Al-Sham Kitchen";
            case "R-101" -> "Gaza Grill House";
            default      -> "Unknown Restaurant";
        };
    }
}
