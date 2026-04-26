package com.foodordering.restaurant.grpc;

import com.foodordering.restaurant.model.Meal;
import com.foodordering.restaurant.service.MealService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class RestaurantGrpcService extends RestaurantServiceGrpc.RestaurantServiceImplBase {

    private final MealService mealService;

    @Override
    public void checkAvailability(AvailabilityRequest request,
                                  StreamObserver<AvailabilityResponse> responseObserver) {

        log.info("gRPC CheckAvailability called for restaurantId={}, items={}",
                request.getRestaurantId(), request.getItemsCount());

        String restaurantName = mealService.getRestaurantName(request.getRestaurantId());
        List<MealDetail> mealDetails = new ArrayList<>();
        boolean allAvailable = true;

        for (MealItem item : request.getItemsList()) {
            Optional<Meal> mealOpt = mealService.findById(item.getMealId());

            if (mealOpt.isPresent()) {
                Meal meal = mealOpt.get();
                if (!meal.isInStock()) {
                    allAvailable = false;
                    log.warn("Meal {} is out of stock", meal.getMealId());
                }
                mealDetails.add(MealDetail.newBuilder()
                        .setMealId(meal.getMealId())
                        .setName(meal.getName())
                        .setUnitPrice(meal.getUnitPrice())
                        .setInStock(meal.isInStock())
                        .build());
            } else {
                // Meal not found → not available
                allAvailable = false;
                log.warn("Meal {} not found in catalog", item.getMealId());
                mealDetails.add(MealDetail.newBuilder()
                        .setMealId(item.getMealId())
                        .setName("Unknown Meal")
                        .setUnitPrice(0.0)
                        .setInStock(false)
                        .build());
            }
        }

        String message = allAvailable
                ? "All items are available"
                : "Some items are unavailable or out of stock";

        AvailabilityResponse response = AvailabilityResponse.newBuilder()
                .setAvailable(allAvailable)
                .setRestaurantName(restaurantName)
                .setMessage(message)
                .addAllMeals(mealDetails)
                .build();

        log.info("gRPC Response: available={}, restaurant={}", allAvailable, restaurantName);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
