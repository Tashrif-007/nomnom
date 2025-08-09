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
@Document(collection = "carts")
public class Cart {
    @Id
    private String id;
    private String userId;
    private String guestSessionId;
    private String restaurantId;
    private List<CartItem> items;
    private double subtotal;
    private double tax;
    private double deliveryFee;
    private double finalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
