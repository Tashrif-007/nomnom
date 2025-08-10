package com.fooddelivery.application.service.impl;

import com.fooddelivery.application.port.MenuItemRepositoryPort;
import com.fooddelivery.application.service.MenuService;
import com.fooddelivery.domain.model.MenuItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuItemRepositoryPort menuItemRepository;

    @Override
    public MenuItem addMenuItem(String restaurantId, String name, String category, 
                               double price, String description, String imageUrl) {
        MenuItem menuItem = new MenuItem();
        menuItem.setRestaurantId(restaurantId);
        menuItem.setName(name);
        menuItem.setCategory(category);
        menuItem.setPrice(price);
        menuItem.setDescription(description);
        menuItem.setImageUrl(imageUrl);
        menuItem.setAvailable(true);
        menuItem.setCreatedAt(LocalDateTime.now());
        menuItem.setUpdatedAt(LocalDateTime.now());

        return menuItemRepository.save(menuItem);
    }

    @Override
    public MenuItem getMenuItemById(String itemId) {
        return menuItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
    }

    @Override
    public List<MenuItem> getMenuItemsByRestaurant(String restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public MenuItem updateMenuItem(String itemId, MenuItem updatedMenuItem) {
        MenuItem menuItem = getMenuItemById(itemId);
        
        menuItem.setName(updatedMenuItem.getName());
        menuItem.setCategory(updatedMenuItem.getCategory());
        menuItem.setPrice(updatedMenuItem.getPrice());
        menuItem.setDescription(updatedMenuItem.getDescription());
        menuItem.setImageUrl(updatedMenuItem.getImageUrl());
        menuItem.setAvailable(updatedMenuItem.isAvailable());
        menuItem.setUpdatedAt(LocalDateTime.now());

        return menuItemRepository.save(menuItem);
    }

    @Override
    public void deleteMenuItem(String itemId) {
        menuItemRepository.deleteById(itemId);
    }

    @Override
    public MenuItem toggleItemAvailability(String itemId, boolean available) {
        MenuItem menuItem = getMenuItemById(itemId);
        menuItem.setAvailable(available);
        menuItem.setUpdatedAt(LocalDateTime.now());
        
        return menuItemRepository.save(menuItem);
    }
}
