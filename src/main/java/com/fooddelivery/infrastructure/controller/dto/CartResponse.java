package com.fooddelivery.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private String restaurantId;
    private List<CartItemDto> items;
    private double tax;
    private double deliveryFee;
    private double finalPrice;
}
