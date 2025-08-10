package com.fooddelivery.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryDto {
    private String orderId;
    private String restaurantId;
    private String restaurantName;
    private String status;
    private double totalAmount;
    private LocalDateTime createdAt;
    private int estimatedDeliveryTime; // in minutes
    private List<OrderItemDto> items;
    private String deliveryAddress;
}
