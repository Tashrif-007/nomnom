package com.fooddelivery.infrastructure.persistence.adapter;

import com.fooddelivery.application.port.OrderRepositoryPort;
import com.fooddelivery.domain.model.Order;
import com.fooddelivery.domain.model.OrderStatus;
import com.fooddelivery.infrastructure.persistence.repository.OrderMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderMongoRepository orderMongoRepository;

    @Override
    public Order save(Order order) {
        return orderMongoRepository.save(order);
    }

    @Override
    public Optional<Order> findById(String id) {
        return orderMongoRepository.findById(id);
    }

    @Override
    public Optional<Order> findByOrderId(String orderId) {
        return orderMongoRepository.findByOrderId(orderId);
    }

    @Override
    public List<Order> findByRestaurantIdAndStatus(String restaurantId, OrderStatus status) {
        return orderMongoRepository.findByRestaurantIdAndStatus(restaurantId, status);
    }

    @Override
    public List<Order> findByRiderId(String riderId) {
        return orderMongoRepository.findByRiderId(riderId);
    }

    @Override
    public List<Order> findByRestaurantIdAndPlacedAtBetween(String restaurantId, LocalDateTime from, LocalDateTime to) {
        return orderMongoRepository.findByRestaurantIdAndPlacedAtBetween(restaurantId, from, to);
    }

    @Override
    public List<Order> findByUserId(String userId) {
        return orderMongoRepository.findByUserId(userId);
    }

    @Override
    public List<Order> findByGuestSessionId(String guestSessionId) {
        return orderMongoRepository.findByGuestSessionId(guestSessionId);
    }
}
