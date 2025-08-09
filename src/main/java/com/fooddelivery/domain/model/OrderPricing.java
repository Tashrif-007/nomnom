package com.fooddelivery.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPricing {
    private double subtotal;
    private double tax;
    private double deliveryFee;
    private double finalPrice;
}
