package com.fooddelivery.application.service;

import com.fooddelivery.domain.model.DeliveryRider;
import com.fooddelivery.domain.model.Order;

import java.util.List;

public interface DeliveryService {
    Order assignRiderToOrder(String orderId, String riderId, String assignedBy);
    List<DeliveryRider> getAvailableRiders();
    List<Order> getOrdersForRider(String riderId);
    Order updateOrderStatusByRider(String orderId, String status);
    DeliveryRider createDeliveryRider(String userId, String vehicleType, String licenseNumber);
    DeliveryRider updateRiderStatus(String riderId, DeliveryRider.RiderStatus status);
}
