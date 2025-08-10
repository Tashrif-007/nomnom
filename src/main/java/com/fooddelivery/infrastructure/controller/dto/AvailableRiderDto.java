package com.fooddelivery.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableRiderDto {
    private String riderId;
    private String name;
    private String status;
    private String vehicleType;
    private double rating;
}
