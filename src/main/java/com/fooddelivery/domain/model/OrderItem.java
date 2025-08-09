package com.fooddelivery.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private String menuItemId;
    private String name;
    private int quantity;
    private double price;
    private double total;
}
