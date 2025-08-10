package com.fooddelivery.application.service.impl;

import com.fooddelivery.application.port.RestaurantRepositoryPort;
import com.fooddelivery.application.service.RestaurantService;
import com.fooddelivery.domain.model.Location;
import com.fooddelivery.domain.model.OperatingHours;
import com.fooddelivery.domain.model.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepositoryPort restaurantRepository;

    @Override
    public Restaurant createRestaurant(String name, String ownerId, String city, String area, 
                                     List<String> cuisine, int estimatedDeliveryTime) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setOwnerId(ownerId);
        
        Location location = new Location();
        location.setCity(city);
        location.setArea(area);
        restaurant.setLocation(location);
        
        restaurant.setCuisine(cuisine);
        restaurant.setRating(0.0);
        restaurant.setEstimatedDeliveryTime(estimatedDeliveryTime);
        restaurant.setActive(true);
        
        // Default operating hours
        OperatingHours hours = new OperatingHours("10:00", "23:00");
        restaurant.setOperatingHours(hours);
        
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant.setUpdatedAt(LocalDateTime.now());

        return restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant getRestaurantById(String restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }

    @Override
    public List<Restaurant> getAllApprovedRestaurants() {
        return restaurantRepository.findByApprovalStatusAndIsActive(Restaurant.ApprovalStatus.APPROVED, true);
    }

    @Override
    public List<Restaurant> searchRestaurants(String location, String cuisine) {
        if (location != null && cuisine != null) {
            return restaurantRepository.findByLocationCityAndCuisineContainingAndIsActive(location, cuisine, true);
        } else if (location != null) {
            return restaurantRepository.findByLocationCityAndIsActive(location, true);
        } else if (cuisine != null) {
            return restaurantRepository.findByCuisineContainingAndIsActive(cuisine, true);
        } else {
            throw new RuntimeException("Location or cuisine parameter is required");
        }
    }

    @Override
    public Restaurant updateRestaurant(String restaurantId, Restaurant updatedRestaurant) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        
        restaurant.setName(updatedRestaurant.getName());
        restaurant.setLocation(updatedRestaurant.getLocation());
        restaurant.setCuisine(updatedRestaurant.getCuisine());
        restaurant.setEstimatedDeliveryTime(updatedRestaurant.getEstimatedDeliveryTime());
        restaurant.setActive(updatedRestaurant.isActive());
        restaurant.setOperatingHours(updatedRestaurant.getOperatingHours());
        restaurant.setUpdatedAt(LocalDateTime.now());

        return restaurantRepository.save(restaurant);
    }

    @Override
    public void deleteRestaurant(String restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurant.setActive(false);
        restaurant.setUpdatedAt(LocalDateTime.now());
        restaurantRepository.save(restaurant);
    }

    @Override
    public List<Restaurant> getRestaurantsByOwner(String ownerId) {
        return restaurantRepository.findByOwnerId(ownerId);
    }

    @Override
    public Restaurant updateRestaurant(Restaurant restaurant) {
        restaurant.setUpdatedAt(LocalDateTime.now());
        return restaurantRepository.save(restaurant);
    }

    @Override
    public List<Restaurant> getRestaurantsByApprovalStatus(Restaurant.ApprovalStatus status) {
        return restaurantRepository.findByApprovalStatus(status);
    }
}
