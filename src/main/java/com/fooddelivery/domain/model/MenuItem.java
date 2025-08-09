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
@Document(collection = "menu_items")
public class MenuItem {
    @Id
    private String id;
    private String restaurantId;
    private String name;
    private String category;
    private double price;
    private String imageUrl;
    private String description;
    private boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
