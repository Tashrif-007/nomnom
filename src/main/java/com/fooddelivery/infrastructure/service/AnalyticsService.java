package com.fooddelivery.application.service;

import java.time.LocalDateTime;

public interface AnalyticsService {
    AnalyticsData getRestaurantAnalytics(String restaurantId, LocalDateTime from, LocalDateTime to);
    
    class AnalyticsData {
        private final int totalOrders;
        private final double totalRevenue;
        private final LocalDateTime from;
        private final LocalDateTime to;
        
        public AnalyticsData(int totalOrders, double totalRevenue, LocalDateTime from, LocalDateTime to) {
            this.totalOrders = totalOrders;
            this.totalRevenue = totalRevenue;
            this.from = from;
            this.to = to;
        }
        
        public int getTotalOrders() { return totalOrders; }
        public double getTotalRevenue() { return totalRevenue; }
        public LocalDateTime getFrom() { return from; }
        public LocalDateTime getTo() { return to; }
    }
}
