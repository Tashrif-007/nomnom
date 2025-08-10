package com.fooddelivery.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantSearchResponse {
    private String id;
    private String name;
    private double rating;
    private int estimatedDeliveryTime;
    private String location;
}
