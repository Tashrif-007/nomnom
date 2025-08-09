package com.fooddelivery.infrastructure.controller;

import com.fooddelivery.application.service.MenuService;
import com.fooddelivery.domain.model.MenuItem;
import com.fooddelivery.infrastructure.controller.dto.*;
import com.fooddelivery.infrastructure.controller.mapper.RestaurantMapper;
import com.fooddelivery.infrastructure.security.AuthorizationAspect;
import com.fooddelivery.infrastructure.security.RequireRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/restaurant")
@RequiredArgsConstructor
public class MenuManagementController {

    private final MenuService menuService;
    private final RestaurantMapper restaurantMapper;
    private final AuthorizationAspect authorizationAspect;

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<MenuItemDto>> getMenuItems(@PathVariable String restaurantId) {
        try {
            List<MenuItem> menuItems = menuService.getMenuItemsByRestaurant(restaurantId);
            
            List<MenuItemDto> response = menuItems.stream()
                    .map(restaurantMapper::toMenuItemDto)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{restaurantId}/menu")
    @RequireRole({"ROLE_ADMIN", "ROLE_RESTAURANT_OWNER"})
    public ResponseEntity<MenuItemResponse> addMenuItem(
            @PathVariable String restaurantId,
            @Valid @RequestBody AddMenuItemRequest request) {
        try {
            // Check restaurant ownership
            authorizationAspect.checkRestaurantOwnership(restaurantId);
            
            MenuItem menuItem = menuService.addMenuItem(
                    restaurantId,
                    request.getName(),
                    "Main", // Default category, could be made configurable
                    request.getPrice(),
                    request.getDescription(),
                    request.getImageUrl()
            );
            
            return ResponseEntity.ok(new MenuItemResponse("Item added", menuItem.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MenuItemResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/menu/{itemId}")
    @RequireRole({"ROLE_ADMIN", "ROLE_RESTAURANT_OWNER"})
    public ResponseEntity<ApiResponse> updateMenuItem(
            @PathVariable String itemId,
            @Valid @RequestBody AddMenuItemRequest request) {
        try {
            // Get menu item to check restaurant ownership
            MenuItem existingItem = menuService.getMenuItemById(itemId);
            authorizationAspect.checkRestaurantOwnership(existingItem.getRestaurantId());
            
            MenuItem updatedItem = new MenuItem();
            updatedItem.setName(request.getName());
            updatedItem.setPrice(request.getPrice());
            updatedItem.setDescription(request.getDescription());
            updatedItem.setImageUrl(request.getImageUrl());
            updatedItem.setAvailable(request.isAvailable());
            
            menuService.updateMenuItem(itemId, updatedItem);
            
            return ResponseEntity.ok(new ApiResponse("Item updated", itemId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/menu/{itemId}")
    @RequireRole({"ROLE_ADMIN", "ROLE_RESTAURANT_OWNER"})
    public ResponseEntity<ApiResponse> deleteMenuItem(@PathVariable String itemId) {
        try {
            // Get menu item to check restaurant ownership
            MenuItem existingItem = menuService.getMenuItemById(itemId);
            authorizationAspect.checkRestaurantOwnership(existingItem.getRestaurantId());
            
            menuService.deleteMenuItem(itemId);
            
            return ResponseEntity.ok(new ApiResponse("Item deleted", itemId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage()));
        }
    }

    @PatchMapping("/menu/{itemId}/availability")
    @RequireRole({"ROLE_ADMIN", "ROLE_RESTAURANT_OWNER"})
    public ResponseEntity<ApiResponse> toggleItemAvailability(
            @PathVariable String itemId,
            @Valid @RequestBody ToggleAvailabilityRequest request) {
        try {
            // Get menu item to check restaurant ownership
            MenuItem existingItem = menuService.getMenuItemById(itemId);
            authorizationAspect.checkRestaurantOwnership(existingItem.getRestaurantId());
            
            MenuItem menuItem = menuService.toggleItemAvailability(itemId, request.isAvailable());
            
            return ResponseEntity.ok(new ApiResponse(
                    "Availability updated",
                    new MenuItemDto(menuItem.getId(), menuItem.getName(), menuItem.getPrice(), 
                                  menuItem.getImageUrl(), menuItem.getDescription(), menuItem.isAvailable())
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage()));
        }
    }
}
