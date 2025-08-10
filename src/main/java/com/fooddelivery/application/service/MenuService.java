package com.fooddelivery.application.service;

import com.fooddelivery.domain.model.MenuItem;

import java.util.List;

public interface MenuService {
    MenuItem addMenuItem(String restaurantId, String name, String category, 
                        double price, String description, String imageUrl);
    MenuItem getMenuItemById(String itemId);
    List<MenuItem> getMenuItemsByRestaurant(String restaurantId);
    MenuItem updateMenuItem(String itemId, MenuItem menuItem);
    void deleteMenuItem(String itemId);
    MenuItem toggleItemAvailability(String itemId, boolean available);
}
