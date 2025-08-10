package com.fooddelivery.application.service.impl;

import com.fooddelivery.application.port.OrderRepositoryPort;
import com.fooddelivery.application.service.AnalyticsService;
import com.fooddelivery.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final OrderRepositoryPort orderRepository;

    @Override
    public AnalyticsData getRestaurantAnalytics(String restaurantId, LocalDateTime from, LocalDateTime to) {
        // If no date range provided, use last 30 days
        if (from == null || to == null) {
            to = LocalDateTime.now();
            from = to.minusDays(30);
        }

        List<Order> orders = orderRepository.findByRestaurantIdAndPlacedAtBetween(restaurantId, from, to);
        
        int totalOrders = orders.size();
        double totalRevenue = orders.stream()
                .filter(order -> order.getPricing() != null)
                .mapToDouble(order -> order.getPricing().getFinalPrice())
                .sum();

        return new AnalyticsData(totalOrders, totalRevenue, from, to);
    }
}
