package com.fooddelivery.application.service.impl;

import com.fooddelivery.application.port.DeliveryRiderRepositoryPort;
import com.fooddelivery.application.service.DeliveryService;
import com.fooddelivery.application.service.OrderService;
import com.fooddelivery.domain.model.DeliveryRider;
import com.fooddelivery.domain.model.Order;
import com.fooddelivery.domain.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRiderRepositoryPort deliveryRiderRepository;
    private final OrderService orderService;

    @Override
    public Order assignRiderToOrder(String orderId, String riderId, String assignedBy) {
        // Validate rider exists and is available
        DeliveryRider rider = deliveryRiderRepository.findById(riderId)
                .orElseThrow(() -> new RuntimeException("Delivery rider not found"));

        if (rider.getStatus() != DeliveryRider.RiderStatus.AVAILABLE) {
            throw new RuntimeException("Rider is not available");
        }

        // Assign rider to order
        Order order = orderService.assignRiderToOrder(orderId, riderId);
        
        // Update rider status to busy
        rider.setStatus(DeliveryRider.RiderStatus.BUSY);
        rider.setUpdatedAt(LocalDateTime.now());
        deliveryRiderRepository.save(rider);

        return order;
    }

    @Override
    public List<DeliveryRider> getAvailableRiders() {
        return deliveryRiderRepository.findByStatusAndIsActive(
                DeliveryRider.RiderStatus.AVAILABLE, true);
    }

    @Override
    public List<Order> getOrdersForRider(String riderId) {
        return orderService.getOrdersByRider(riderId);
    }

    @Override
    public Order updateOrderStatusByRider(String orderId, String status) {
        OrderStatus orderStatus;
        
        switch (status.toUpperCase()) {
            case "PICKED UP":
            case "PICKED_UP":
                orderStatus = OrderStatus.OUT_FOR_DELIVERY;
                break;
            case "DELIVERED":
                orderStatus = OrderStatus.DELIVERED;
                break;
            default:
                throw new RuntimeException("Invalid status for rider update: " + status);
        }

        Order order = orderService.updateOrderStatus(orderId, orderStatus, null, null);
        
        // If order is delivered, make rider available again
        if (orderStatus == OrderStatus.DELIVERED) {
            if (order.getRiderId() != null) {
                deliveryRiderRepository.findById(order.getRiderId())
                        .ifPresent(rider -> {
                            rider.setStatus(DeliveryRider.RiderStatus.AVAILABLE);
                            rider.setUpdatedAt(LocalDateTime.now());
                            deliveryRiderRepository.save(rider);
                        });
            }
        }

        return order;
    }

    @Override
    public DeliveryRider createDeliveryRider(String userId, String vehicleType, String licenseNumber) {
        DeliveryRider rider = new DeliveryRider();
        rider.setUserId(userId);
        rider.setVehicleType(DeliveryRider.VehicleType.valueOf(vehicleType.toUpperCase()));
        rider.setLicenseNumber(licenseNumber);
        rider.setStatus(DeliveryRider.RiderStatus.AVAILABLE);
        rider.setRating(0.0);
        rider.setActive(true);
        rider.setCreatedAt(LocalDateTime.now());
        rider.setUpdatedAt(LocalDateTime.now());

        return deliveryRiderRepository.save(rider);
    }

    @Override
    public DeliveryRider updateRiderStatus(String riderId, DeliveryRider.RiderStatus status) {
        DeliveryRider rider = deliveryRiderRepository.findById(riderId)
                .orElseThrow(() -> new RuntimeException("Delivery rider not found"));

        rider.setStatus(status);
        rider.setUpdatedAt(LocalDateTime.now());

        return deliveryRiderRepository.save(rider);
    }
}
