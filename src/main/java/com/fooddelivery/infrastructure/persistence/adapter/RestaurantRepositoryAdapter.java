package com.fooddelivery.infrastructure.persistence.adapter;

import com.fooddelivery.application.port.RestaurantRepositoryPort;
import com.fooddelivery.domain.model.Restaurant;
import com.fooddelivery.infrastructure.persistence.repository.RestaurantMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RestaurantRepositoryAdapter implements RestaurantRepositoryPort {

    private final RestaurantMongoRepository restaurantMongoRepository;

    @Override
    public Restaurant save(Restaurant restaurant) {
        return restaurantMongoRepository.save(restaurant);
    }

    @Override
    public Optional<Restaurant> findById(String id) {
        return restaurantMongoRepository.findById(id);
    }

    @Override
    public List<Restaurant> findByLocationCityAndIsActive(String city, boolean isActive) {
        return restaurantMongoRepository.findByLocationCityAndIsActive(city, isActive);
    }

    @Override
    public List<Restaurant> findByCuisineContainingAndIsActive(String cuisine, boolean isActive) {
        return restaurantMongoRepository.findByCuisineContainingAndIsActive(cuisine, isActive);
    }

    @Override
    public List<Restaurant> findByLocationCityAndCuisineContainingAndIsActive(String city, String cuisine, boolean isActive) {
        return restaurantMongoRepository.findByLocationCityAndCuisineContainingAndIsActive(city, cuisine, isActive);
    }

    @Override
    public List<Restaurant> findByOwnerId(String ownerId) {
        return restaurantMongoRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Restaurant> findByApprovalStatus(Restaurant.ApprovalStatus status) {
        return restaurantMongoRepository.findByApprovalStatus(status);
    }

    @Override
    public List<Restaurant> findByApprovalStatusAndIsActive(Restaurant.ApprovalStatus status, boolean isActive) {
        return restaurantMongoRepository.findByApprovalStatusAndIsActive(status, isActive);
    }
}
