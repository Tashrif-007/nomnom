package com.fooddelivery.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartRequest {
    private String userId; // Optional - for registered users
    private String guestSessionId; // Optional - for guest users
    
    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;
    
    @NotEmpty(message = "Items list cannot be empty")
    private List<CartItemRequest> items;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemRequest {
        @NotBlank(message = "Item ID is required")
        private String itemId;
        
        @Positive(message = "Quantity must be positive")
        private int quantity;
    }
}
