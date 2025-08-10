package com.fooddelivery.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignRiderRequest {
    @NotBlank(message = "Order ID is required")
    private String orderId;
    
    @NotBlank(message = "Rider ID is required")
    private String riderId;
    
    @NotBlank(message = "Assigned by is required")
    private String assignedBy; // "admin" or "restaurant"
}
