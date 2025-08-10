package com.fooddelivery.application.service;

import com.fooddelivery.domain.model.Restaurant;

import java.util.List;

public interface RestaurantService {
    Restaurant createRestaurant(String name, String ownerId, String city, String area, 
                              List<String> cuisine, int estimatedDeliveryTime);
    Restaurant createRestaurant(Restaurant restaurant);
    Restaurant getRestaurantById(String restaurantId);
    List<Restaurant> getAllApprovedRestaurants();
    List<Restaurant> searchRestaurants(String location, String cuisine);
    Restaurant updateRestaurant(String restaurantId, Restaurant restaurant);
    Restaurant updateRestaurant(Restaurant restaurant);
    void deleteRestaurant(String restaurantId);
    List<Restaurant> getRestaurantsByOwner(String ownerId);
    List<Restaurant> getRestaurantsByApprovalStatus(Restaurant.ApprovalStatus status);
}
