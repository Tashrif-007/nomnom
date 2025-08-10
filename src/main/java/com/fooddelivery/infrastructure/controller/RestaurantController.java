package com.fooddelivery.infrastructure.controller;

import com.fooddelivery.application.service.MenuService;
import com.fooddelivery.application.service.RestaurantService;
import com.fooddelivery.domain.model.MenuItem;
import com.fooddelivery.domain.model.Restaurant;
import com.fooddelivery.infrastructure.controller.dto.*;
import com.fooddelivery.infrastructure.controller.mapper.RestaurantMapper;
import com.fooddelivery.infrastructure.security.AuthorizationAspect;
import com.fooddelivery.infrastructure.security.RequireRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final MenuService menuService;
    private final RestaurantMapper restaurantMapper;
    private final AuthorizationAspect authorizationAspect;

    @PostMapping
    @RequireRole("ROLE_RESTAURANT_OWNER")
    public ResponseEntity<CreateRestaurantResponse> createRestaurant(@Valid @RequestBody CreateRestaurantRequest request) {
        try {
            // Restaurant owner creates their own restaurant
            var currentUser = authorizationAspect.getCurrentUser();
            
            Restaurant restaurant = restaurantMapper.fromCreateRestaurantRequest(request);
            restaurant.setOwnerId(currentUser.getId()); // Set current user as owner
            restaurant.setApprovalStatus(Restaurant.ApprovalStatus.PENDING_APPROVAL); // Needs admin approval
            restaurant.setActive(false); // Not active until approved
            
            Restaurant savedRestaurant = restaurantService.createRestaurant(restaurant);
            
            CreateRestaurantResponse response = restaurantMapper.toCreateRestaurantResponse(savedRestaurant);
            response.setMessage("Restaurant submitted for approval. Admin will review shortly.");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new CreateRestaurantResponse(e.getMessage(), null, null, false));
        }
    }

    @GetMapping
    public ResponseEntity<List<RestaurantSearchResponse>> getAllRestaurants() {
        try {
            List<Restaurant> restaurants = restaurantService.getAllApprovedRestaurants();
            
            List<RestaurantSearchResponse> response = restaurants.stream()
                    .map(restaurantMapper::toRestaurantSearchResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<RestaurantSearchResponse>> searchRestaurants(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String cuisine) {
        try {
            List<Restaurant> restaurants = restaurantService.searchRestaurants(location, cuisine);
            
            List<RestaurantSearchResponse> response = restaurants.stream()
                    .map(restaurantMapper::toRestaurantSearchResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantDetails(@PathVariable String restaurantId) {
        try {
            Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
            List<MenuItem> menuItems = menuService.getMenuItemsByRestaurant(restaurantId);
            
            RestaurantDetailsResponse response = restaurantMapper.toRestaurantDetailsResponse(restaurant, menuItems);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{restaurantId}")
    @RequireRole({"ROLE_ADMIN", "ROLE_RESTAURANT_OWNER"})
    public ResponseEntity<ApiResponse> updateRestaurant(@PathVariable String restaurantId, 
                                                       @Valid @RequestBody UpdateRestaurantRequest request) {
        try {
            Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
            restaurantMapper.updateRestaurantFromRequest(restaurant, request);
            Restaurant updatedRestaurant = restaurantService.updateRestaurant(restaurantId, restaurant);
            
            return ResponseEntity.ok(new ApiResponse("Restaurant updated successfully", updatedRestaurant.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{restaurantId}")
    @RequireRole("ROLE_ADMIN")
    public ResponseEntity<ApiResponse> deleteRestaurant(@PathVariable String restaurantId) {
        try {
            restaurantService.deleteRestaurant(restaurantId);
            
            return ResponseEntity.ok(new ApiResponse("Restaurant deactivated successfully", restaurantId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<RestaurantSearchResponse>> getRestaurantsByOwner(@PathVariable String ownerId) {
        try {
            List<Restaurant> restaurants = restaurantService.getRestaurantsByOwner(ownerId);
            
            List<RestaurantSearchResponse> response = restaurants.stream()
                    .map(restaurantMapper::toRestaurantSearchResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/approve")
    @RequireRole("ROLE_ADMIN")
    public ResponseEntity<GenericResponse> approveRestaurant(@PathVariable String id) {
        try {
            Restaurant restaurant = restaurantService.getRestaurantById(id);
            if (restaurant == null) {
                return ResponseEntity.notFound().build();
            }
            
            if (restaurant.getApprovalStatus() != Restaurant.ApprovalStatus.PENDING_APPROVAL) {
                return ResponseEntity.badRequest()
                        .body(new GenericResponse("Restaurant is not pending approval"));
            }
            
            restaurant.setApprovalStatus(Restaurant.ApprovalStatus.APPROVED);
            restaurant.setActive(true); // Make restaurant active when approved
            restaurant.setRejectionReason(null); // Clear any previous rejection reason
            
            restaurantService.updateRestaurant(restaurant);
            
            return ResponseEntity.ok(new GenericResponse("Restaurant approved successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new GenericResponse("Error approving restaurant: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/reject")
    @RequireRole("ROLE_ADMIN")
    public ResponseEntity<GenericResponse> rejectRestaurant(@PathVariable String id, 
                                                           @RequestBody Map<String, String> payload) {
        try {
            Restaurant restaurant = restaurantService.getRestaurantById(id);
            if (restaurant == null) {
                return ResponseEntity.notFound().build();
            }
            
            if (restaurant.getApprovalStatus() != Restaurant.ApprovalStatus.PENDING_APPROVAL) {
                return ResponseEntity.badRequest()
                        .body(new GenericResponse("Restaurant is not pending approval"));
            }
            
            String reason = payload.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new GenericResponse("Rejection reason is required"));
            }
            
            restaurant.setApprovalStatus(Restaurant.ApprovalStatus.REJECTED);
            restaurant.setRejectionReason(reason);
            restaurant.setActive(false);
            
            restaurantService.updateRestaurant(restaurant);
            
            return ResponseEntity.ok(new GenericResponse("Restaurant rejected successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new GenericResponse("Error rejecting restaurant: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/suspend")
    @RequireRole("ROLE_ADMIN")
    public ResponseEntity<GenericResponse> suspendRestaurant(@PathVariable String id,
                                                            @RequestBody Map<String, String> payload) {
        try {
            Restaurant restaurant = restaurantService.getRestaurantById(id);
            if (restaurant == null) {
                return ResponseEntity.notFound().build();
            }
            
            if (restaurant.getApprovalStatus() != Restaurant.ApprovalStatus.APPROVED) {
                return ResponseEntity.badRequest()
                        .body(new GenericResponse("Only approved restaurants can be suspended"));
            }
            
            String reason = payload.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new GenericResponse("Suspension reason is required"));
            }
            
            restaurant.setApprovalStatus(Restaurant.ApprovalStatus.SUSPENDED);
            restaurant.setRejectionReason(reason); // Using same field for suspension reason
            restaurant.setActive(false);
            
            restaurantService.updateRestaurant(restaurant);
            
            return ResponseEntity.ok(new GenericResponse("Restaurant suspended successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new GenericResponse("Error suspending restaurant: " + e.getMessage()));
        }
    }

    @GetMapping("/pending-approval")
    @RequireRole("ROLE_ADMIN")
    public ResponseEntity<List<RestaurantSearchResponse>> getPendingRestaurants() {
        try {
            List<Restaurant> pendingRestaurants = restaurantService.getRestaurantsByApprovalStatus(
                    Restaurant.ApprovalStatus.PENDING_APPROVAL);
            
            List<RestaurantSearchResponse> response = pendingRestaurants.stream()
                    .map(restaurantMapper::toRestaurantSearchResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
