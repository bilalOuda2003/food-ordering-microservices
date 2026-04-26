package com.foodordering.order.client;

import com.foodordering.restaurant.grpc.AvailabilityRequest;
import com.foodordering.restaurant.grpc.AvailabilityResponse;
import com.foodordering.restaurant.grpc.MealItem;
import com.foodordering.restaurant.grpc.RestaurantServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class RestaurantGrpcClient {

    // "restaurant-service" matches the name in application.properties
    @GrpcClient("restaurant-service")
    private RestaurantServiceGrpc.RestaurantServiceBlockingStub restaurantStub;

    /**
     * Calls the Restaurant Service via gRPC to check if meals are available.
     *
     * @param restaurantId the restaurant ID
     * @param items        list of (mealId, quantity) pairs
     * @return AvailabilityResponse with prices and stock status
     */
    public AvailabilityResponse checkAvailability(String restaurantId,
                                                   List<MealItem> items) {
        log.info("Calling Restaurant Service via gRPC – restaurantId={}, items={}",
                restaurantId, items.size());

        AvailabilityRequest request = AvailabilityRequest.newBuilder()
                .setRestaurantId(restaurantId)
                .addAllItems(items)
                .build();

        AvailabilityResponse response = restaurantStub.checkAvailability(request);

        log.info("gRPC Response received – available={}, restaurant={}",
                response.getAvailable(), response.getRestaurantName());

        return response;
    }
}
