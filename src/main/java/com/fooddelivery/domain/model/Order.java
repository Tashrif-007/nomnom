package com.fooddelivery.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String orderId; // Human readable ID like ORD-789
    private String userId;
    private String guestSessionId;
    private String restaurantId;
    private List<OrderItem> items;
    private OrderPricing pricing;
    private DeliveryAddress deliveryAddress;
    private Payment payment;
    private OrderStatus status;
    private int estimatedDeliveryTime;
    private String rejectionReason;
    private int prepTime;
    private String riderId;
    private List<OrderTimeline> timeline;
    private LocalDateTime placedAt;
    private LocalDateTime updatedAt;
}
