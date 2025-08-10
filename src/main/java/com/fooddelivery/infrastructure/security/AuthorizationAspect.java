package com.fooddelivery.infrastructure.security;

import com.fooddelivery.application.service.AuthService;
import com.fooddelivery.application.service.RestaurantService;
import com.fooddelivery.application.service.UserService;
import com.fooddelivery.domain.model.Restaurant;
import com.fooddelivery.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationAspect {

    private final UserService userService;
    private final AuthService authService;
    private final RestaurantService restaurantService;

    @Before("@annotation(requireRole)")
    public void checkRole(JoinPoint joinPoint, RequireRole requireRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }

        String userEmail = authentication.getName();
        User user = userService.getUserByEmail(userEmail);
        
        String userRole = "ROLE_" + user.getRole().name();
        String[] requiredRoles = requireRole.value();
        
        boolean hasRequiredRole = false;
        for (String role : requiredRoles) {
            if (userRole.equals(role)) {
                hasRequiredRole = true;
                break;
            }
        }
        
        if (!hasRequiredRole) {
            throw new SecurityException("Insufficient permissions. Required roles: " + String.join(", ", requiredRoles));
        }
    }

    public void checkRestaurantOwnership(String restaurantId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }

        String userEmail = authentication.getName();
        User user = userService.getUserByEmail(userEmail);
        
        // Admin can access any restaurant
        if (user.getRole() == User.UserRole.ADMIN) {
            return;
        }
        
        // Restaurant owner can only access their own restaurant
        if (user.getRole() == User.UserRole.RESTAURANT_OWNER) {
            Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
            if (!restaurant.getOwnerId().equals(user.getId())) {
                throw new SecurityException("You can only manage your own restaurant");
            }
            return;
        }
        
        throw new SecurityException("Only restaurant owners and admins can manage restaurants");
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }

        String userEmail = authentication.getName();
        return userService.getUserByEmail(userEmail);
    }
}
