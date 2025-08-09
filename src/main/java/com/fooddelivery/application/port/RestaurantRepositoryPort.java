package com.fooddelivery.application.port;

import com.fooddelivery.domain.model.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepositoryPort {
    Restaurant save(Restaurant restaurant);
    Optional<Restaurant> findById(String id);
    List<Restaurant> findByLocationCityAndIsActive(String city, boolean isActive);
    List<Restaurant> findByCuisineContainingAndIsActive(String cuisine, boolean isActive);
    List<Restaurant> findByLocationCityAndCuisineContainingAndIsActive(String city, String cuisine, boolean isActive);
    List<Restaurant> findByOwnerId(String ownerId);
    List<Restaurant> findByApprovalStatus(Restaurant.ApprovalStatus status);
    List<Restaurant> findByApprovalStatusAndIsActive(Restaurant.ApprovalStatus status, boolean isActive);
}
