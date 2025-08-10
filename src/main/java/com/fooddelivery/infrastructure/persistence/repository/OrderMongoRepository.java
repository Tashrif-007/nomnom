package com.fooddelivery.infrastructure.persistence.repository;

import com.fooddelivery.domain.model.Order;
import com.fooddelivery.domain.model.OrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderMongoRepository extends MongoRepository<Order, String> {
    Optional<Order> findByOrderId(String orderId);
    List<Order> findByRestaurantIdAndStatus(String restaurantId, OrderStatus status);
    List<Order> findByRiderId(String riderId);
    List<Order> findByRestaurantIdAndPlacedAtBetween(String restaurantId, LocalDateTime from, LocalDateTime to);
    List<Order> findByUserId(String userId);
    List<Order> findByGuestSessionId(String guestSessionId);
}
