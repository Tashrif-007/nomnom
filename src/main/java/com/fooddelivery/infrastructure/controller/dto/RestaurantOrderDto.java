package com.fooddelivery.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantOrderDto {
    private String orderId;
    private String customerName;
    private List<OrderItemDto> items;
    private double totalPrice;
    private String deliveryAddress;
    private String status;
    private LocalDateTime placedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        private String itemName;
        private int quantity;
        private double price;
    }
}
