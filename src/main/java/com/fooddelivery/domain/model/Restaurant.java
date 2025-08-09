package com.fooddelivery.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "restaurants")
public class Restaurant {
    @Id
    private String id;
    private String name;
    private String ownerId;
    private Location location;
    private List<String> cuisine;
    private double rating;
    private int estimatedDeliveryTime;
    private boolean isActive;
    private ApprovalStatus approvalStatus;
    private String rejectionReason;
    private OperatingHours operatingHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum ApprovalStatus {
        PENDING_APPROVAL,    // Restaurant owner submitted, waiting for admin review
        APPROVED,           // Admin approved, restaurant can go live
        REJECTED,           // Admin rejected, restaurant owner can revise
        SUSPENDED           // Admin suspended due to violations
    }
}
