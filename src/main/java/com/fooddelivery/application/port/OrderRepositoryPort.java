package com.fooddelivery.application.port;

import com.fooddelivery.domain.model.Order;
import com.fooddelivery.domain.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(String id);
    Optional<Order> findByOrderId(String orderId);
    List<Order> findByRestaurantIdAndStatus(String restaurantId, OrderStatus status);
    List<Order> findByRiderId(String riderId);
    List<Order> findByRestaurantIdAndPlacedAtBetween(String restaurantId, LocalDateTime from, LocalDateTime to);
    List<Order> findByUserId(String userId);
    List<Order> findByGuestSessionId(String guestSessionId);
}
