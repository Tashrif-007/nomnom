package com.fooddelivery.application.port;

import com.fooddelivery.domain.model.MenuItem;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepositoryPort {
    MenuItem save(MenuItem menuItem);
    Optional<MenuItem> findById(String id);
    List<MenuItem> findByRestaurantId(String restaurantId);
    List<MenuItem> findByRestaurantIdAndAvailable(String restaurantId, boolean available);
    void deleteById(String id);
}
