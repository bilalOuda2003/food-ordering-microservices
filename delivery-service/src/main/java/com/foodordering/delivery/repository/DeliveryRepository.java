package com.foodordering.delivery.repository;

import com.foodordering.delivery.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByOrderId(String orderId);
    List<Delivery> findByDriverId(String driverId);
    List<Delivery> findByStatus(Delivery.DeliveryStatus status);
}
