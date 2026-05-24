package com.foodordering.kitchen.repository;

import com.foodordering.kitchen.model.KitchenOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KitchenOrderRepository extends JpaRepository<KitchenOrder, Long> {
    Optional<KitchenOrder> findByOrderId(String orderId);
    List<KitchenOrder> findByStatus(KitchenOrder.KitchenStatus status);
}
