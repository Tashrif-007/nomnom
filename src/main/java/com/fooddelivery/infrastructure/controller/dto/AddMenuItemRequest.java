package com.fooddelivery.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMenuItemRequest {
    @NotBlank(message = "Menu item name is required")
    private String name;
    
    @Positive(message = "Price must be positive")
    private double price;
    
    private boolean available = true;
    private String imageUrl;
    private String description;
}
