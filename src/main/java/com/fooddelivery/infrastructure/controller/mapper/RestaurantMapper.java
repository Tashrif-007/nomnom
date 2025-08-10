package com.fooddelivery.infrastructure.controller.mapper;

import com.fooddelivery.domain.model.Location;
import com.fooddelivery.domain.model.MenuItem;
import com.fooddelivery.domain.model.OperatingHours;
import com.fooddelivery.domain.model.Restaurant;
import com.fooddelivery.infrastructure.controller.dto.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RestaurantMapper {

    public RestaurantSearchResponse toRestaurantSearchResponse(Restaurant restaurant) {
        String location = restaurant.getLocation().getCity();
        if (restaurant.getLocation().getArea() != null) {
            location += ", " + restaurant.getLocation().getArea();
        }
        
        return new RestaurantSearchResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getRating(),
                restaurant.getEstimatedDeliveryTime(),
                location
        );
    }

    public RestaurantDetailsResponse toRestaurantDetailsResponse(Restaurant restaurant, List<MenuItem> menuItems) {
        RestaurantDto restaurantDto = new RestaurantDto(
                restaurant.getId(),
                restaurant.getName(),
                null, // description - not available in Restaurant entity
                String.join(", ", restaurant.getCuisine()),
                restaurant.getLocation().getCity() + ", " + restaurant.getLocation().getArea(),
                restaurant.getRating(),
                restaurant.getEstimatedDeliveryTime() + " mins",
                null, // imageUrl - skipping images for now
                restaurant.getOwnerId(),
                restaurant.getApprovalStatus().toString(),
                restaurant.getRejectionReason(),
                restaurant.isActive()
        );

        List<MenuItemDto> menuItemDtos = menuItems.stream()
                .map(this::toMenuItemDto)
                .collect(Collectors.toList());

        return new RestaurantDetailsResponse(restaurantDto, menuItemDtos);
    }

    public MenuItemDto toMenuItemDto(MenuItem menuItem) {
        return new MenuItemDto(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getPrice(),
                null, // imageUrl - skipping images for now
                menuItem.getDescription(),
                menuItem.isAvailable()
        );
    }

    public CreateRestaurantResponse toCreateRestaurantResponse(Restaurant restaurant) {
        return new CreateRestaurantResponse(
                "Restaurant created successfully",
                restaurant.getId(),
                restaurant.getName(),
                restaurant.isActive()
        );
    }

    public Restaurant fromCreateRestaurantRequest(CreateRestaurantRequest request) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setOwnerId(request.getOwnerId());
        
        Location location = new Location();
        location.setCity(request.getCity());
        location.setArea(request.getArea());
        restaurant.setLocation(location);
        
        restaurant.setCuisine(request.getCuisine());
        restaurant.setEstimatedDeliveryTime(request.getEstimatedDeliveryTime());
        restaurant.setRating(0.0); // Default rating
        restaurant.setActive(true); // Default to active
        
        if (request.getOperatingHours() != null) {
            OperatingHours hours = new OperatingHours(
                request.getOperatingHours().getOpenTime(),
                request.getOperatingHours().getCloseTime()
            );
            restaurant.setOperatingHours(hours);
        } else {
            // Default operating hours
            restaurant.setOperatingHours(new OperatingHours("10:00", "23:00"));
        }
        
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant.setUpdatedAt(LocalDateTime.now());
        
        return restaurant;
    }

    public void updateRestaurantFromRequest(Restaurant restaurant, UpdateRestaurantRequest request) {
        restaurant.setName(request.getName());
        
        Location location = restaurant.getLocation();
        if (location == null) {
            location = new Location();
        }
        location.setCity(request.getCity());
        location.setArea(request.getArea());
        restaurant.setLocation(location);
        
        restaurant.setCuisine(request.getCuisine());
        restaurant.setEstimatedDeliveryTime(request.getEstimatedDeliveryTime());
        restaurant.setActive(request.isActive());
        
        if (request.getOperatingHours() != null) {
            OperatingHours hours = new OperatingHours(
                request.getOperatingHours().getOpenTime(),
                request.getOperatingHours().getCloseTime()
            );
            restaurant.setOperatingHours(hours);
        }
        
        restaurant.setUpdatedAt(LocalDateTime.now());
    }
}
