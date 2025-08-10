package com.fooddelivery.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRestaurantRequest {
    
    @NotBlank(message = "Restaurant name is required")
    private String name;
    
    @NotBlank(message = "Owner ID is required")
    private String ownerId;
    
    @NotBlank(message = "City is required")
    private String city;
    
    private String area;
    
    @NotNull(message = "Cuisine list is required")
    private List<String> cuisine;
    
    @Positive(message = "Estimated delivery time must be positive")
    private int estimatedDeliveryTime;
    
    private String description;
    private String imageUrl;
    private String phoneNumber;
    private OperatingHoursDto operatingHours;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperatingHoursDto {
        @NotBlank(message = "Opening time is required")
        private String openTime;
        
        @NotBlank(message = "Closing time is required")
        private String closeTime;
    }
}
