package com.fooddelivery.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliveryRiderRequest {
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Vehicle type is required")
    private String vehicleType; // BIKE, CAR, SCOOTER
    
    @NotBlank(message = "License number is required")
    private String licenseNumber;
}
