package com.fooddelivery.application.service;

import com.fooddelivery.domain.model.Order;
import com.fooddelivery.domain.model.OrderStatus;
import com.fooddelivery.domain.model.Payment;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    Order createOrder(String userId, String guestSessionId, String cartId, 
                     String deliveryAddress, Payment payment);
    Order getOrderById(String orderId);
    Order getOrderByOrderId(String humanReadableOrderId);
    Order updateOrderStatus(String orderId, OrderStatus status, String rejectionReason, Integer prepTime);
    List<Order> getOrdersByRestaurant(String restaurantId, OrderStatus status);
    List<Order> getOrdersByUser(String userId);
    List<Order> getOrdersByGuestSession(String guestSessionId);
    List<Order> getOrdersByRider(String riderId);
    Order assignRiderToOrder(String orderId, String riderId);
    String generateOrderId();
}
