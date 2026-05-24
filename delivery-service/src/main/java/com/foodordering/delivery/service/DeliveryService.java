package com.foodordering.delivery.service;

import com.foodordering.delivery.dto.DeliveryRequest;
import com.foodordering.delivery.model.Delivery;
import com.foodordering.delivery.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    // Simulated driver pool
    private static final String[][] DRIVERS = {
        {"DRV-01", "Ahmad Nasser", "+970-59-1111111"},
        {"DRV-02", "Mohammed Ali", "+970-59-2222222"},
        {"DRV-03", "Yusuf Hassan", "+970-59-3333333"},
        {"DRV-04", "Omar Khalil", "+970-59-4444444"}
    };

    public Delivery assignDelivery(DeliveryRequest request) {
        Optional<Delivery> existing = deliveryRepository.findByOrderId(request.getOrderId());
        if (existing.isPresent()) {
            System.out.println("[DeliveryService] Delivery already assigned for order: " + request.getOrderId());
            return existing.get();
        }

        Delivery delivery = new Delivery();
        delivery.setOrderId(request.getOrderId());
        delivery.setCustomerId(request.getCustomerId());

        if (request.getDeliveryAddress() != null) {
            delivery.setStreet(request.getDeliveryAddress().getStreet());
            delivery.setCity(request.getDeliveryAddress().getCity());
            delivery.setPostalCode(request.getDeliveryAddress().getPostalCode());
        }

        // Assign a random available driver
        String[] driver = DRIVERS[(int) (Math.random() * DRIVERS.length)];
        delivery.setDriverId(driver[0]);
        delivery.setDriverName(driver[1]);
        delivery.setDriverPhone(driver[2]);
        delivery.setStatus(Delivery.DeliveryStatus.ASSIGNED);
        delivery.setEstimatedETA("25 min");
        delivery.setAssignedAt(LocalDateTime.now());

        Delivery saved = deliveryRepository.save(delivery);
        System.out.println("[DeliveryService] Driver " + driver[1] + " assigned for order: " + request.getOrderId());
        return saved;
    }

    public Delivery updateStatus(String orderId, String newStatus) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for order: " + orderId));

        Delivery.DeliveryStatus status = Delivery.DeliveryStatus.valueOf(newStatus.toUpperCase());
        delivery.setStatus(status);

        if (status == Delivery.DeliveryStatus.PICKED_UP) {
            delivery.setPickedUpAt(LocalDateTime.now());
        } else if (status == Delivery.DeliveryStatus.DELIVERED) {
            delivery.setDeliveredAt(LocalDateTime.now());
        }

        Delivery saved = deliveryRepository.save(delivery);
        System.out.println("[DeliveryService] Delivery status updated to " + status + " for order: " + orderId);
        return saved;
    }

    public Optional<Delivery> getDeliveryByOrderId(String orderId) {
        return deliveryRepository.findByOrderId(orderId);
    }

    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    public List<Delivery> getDeliveriesByStatus(String status) {
        return deliveryRepository.findByStatus(Delivery.DeliveryStatus.valueOf(status.toUpperCase()));
    }

    public void handleFoodReady(String rawEvent) {
        // When food is ready, automatically create a pending delivery
        System.out.println("[DeliveryService] Food is ready - preparing delivery assignment...");
    }
}
