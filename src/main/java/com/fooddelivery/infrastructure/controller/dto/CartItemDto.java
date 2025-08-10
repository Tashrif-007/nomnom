package com.fooddelivery.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private String itemId;
    private String name;
    private int quantity;
    private double price;
    private double total;
}
