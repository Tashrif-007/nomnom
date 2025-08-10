package com.fooddelivery.infrastructure.persistence.adapter;

import com.fooddelivery.application.port.MenuItemRepositoryPort;
import com.fooddelivery.domain.model.MenuItem;
import com.fooddelivery.infrastructure.persistence.repository.MenuItemMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MenuItemRepositoryAdapter implements MenuItemRepositoryPort {

    private final MenuItemMongoRepository menuItemMongoRepository;

    @Override
    public MenuItem save(MenuItem menuItem) {
        return menuItemMongoRepository.save(menuItem);
    }

    @Override
    public Optional<MenuItem> findById(String id) {
        return menuItemMongoRepository.findById(id);
    }

    @Override
    public List<MenuItem> findByRestaurantId(String restaurantId) {
        return menuItemMongoRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public List<MenuItem> findByRestaurantIdAndAvailable(String restaurantId, boolean available) {
        return menuItemMongoRepository.findByRestaurantIdAndAvailable(restaurantId, available);
    }

    @Override
    public void deleteById(String id) {
        menuItemMongoRepository.deleteById(id);
    }
}
