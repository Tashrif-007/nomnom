package com.fooddelivery.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "delivery_riders")
public class DeliveryRider {
    @Id
    private String id;
    private String userId;
    private VehicleType vehicleType;
    private String licenseNumber;
    private RiderStatus status;
    private Location.Coordinates currentLocation;
    private double rating;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum VehicleType {
        BIKE, CAR, SCOOTER
    }
    
    public enum RiderStatus {
        AVAILABLE, BUSY, OFFLINE
    }
}
