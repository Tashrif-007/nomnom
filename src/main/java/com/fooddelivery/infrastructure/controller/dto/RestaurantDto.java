package com.fooddelivery.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {
    private String id;
    private String name;
    private String description;
    private String cuisine;
    private String location;
    private double rating;
    private String deliveryTime;
    private String imageUrl;
    private String ownerId;
    private String approvalStatus;
    private String rejectionReason;
    private boolean active;
}
