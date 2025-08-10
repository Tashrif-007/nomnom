package com.fooddelivery.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderProgressRequest {
    @NotBlank(message = "Status is required")
    private String status; // "Preparing", "Out for Delivery", "Delivered"
}
