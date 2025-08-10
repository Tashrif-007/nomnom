package com.fooddelivery.infrastructure.persistence.repository;

import com.fooddelivery.domain.model.Restaurant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantMongoRepository extends MongoRepository<Restaurant, String> {
    List<Restaurant> findByLocationCityAndIsActive(String city, boolean isActive);
    List<Restaurant> findByCuisineContainingAndIsActive(String cuisine, boolean isActive);
    List<Restaurant> findByLocationCityAndCuisineContainingAndIsActive(String city, String cuisine, boolean isActive);
    List<Restaurant> findByOwnerId(String ownerId);
    List<Restaurant> findByApprovalStatus(Restaurant.ApprovalStatus status);
    List<Restaurant> findByApprovalStatusAndIsActive(Restaurant.ApprovalStatus status, boolean isActive);
}
