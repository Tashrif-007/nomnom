package com.fooddelivery.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcceptOrderRequest {
    @NotBlank(message = "Status is required")
    private String status; // "Accepted" or "Rejected"
    
    @Positive(message = "Prep time must be positive")
    private Integer prepTime; // Only for accepted orders
    
    private String reason; // Only for rejected orders
}
