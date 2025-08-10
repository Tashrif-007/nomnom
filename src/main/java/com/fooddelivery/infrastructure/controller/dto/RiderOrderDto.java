package com.fooddelivery.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiderOrderDto {
    private String orderId;
    private String customerName;
    private String deliveryAddress;
    private String status;
    private double totalAmount;
}
