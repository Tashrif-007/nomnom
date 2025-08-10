package com.fooddelivery.infrastructure.controller;

import com.fooddelivery.application.service.AuthService;
import com.fooddelivery.application.service.GuestSessionService;
import com.fooddelivery.application.service.UserService;
import com.fooddelivery.domain.model.GuestSession;
import com.fooddelivery.domain.model.User;
import com.fooddelivery.infrastructure.controller.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final GuestSessionService guestSessionService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        try {
            User user = userService.createUser(request.getEmail(), request.getPassword(), User.UserRole.USER);
            String token = authService.generateToken(user.getId(), user.getEmail());

            AuthResponse response = new AuthResponse(
                    "User registered successfully",
                    user.getId(),
                    token
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(e.getMessage(), null, null));
        }
    }

    @PostMapping("/admin/signup")
    public ResponseEntity<AuthResponse> createAdmin(@Valid @RequestBody SignUpRequest request) {
        try {
            User user = userService.createUser(request.getEmail(), request.getPassword(), User.UserRole.ADMIN);
            String token = authService.generateToken(user.getId(), user.getEmail());

            AuthResponse response = new AuthResponse(
                    "Admin created successfully",
                    user.getId(),
                    token
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(e.getMessage(), null, null));
        }
    }

    @PostMapping("/restaurant-owner/signup")
    public ResponseEntity<AuthResponse> createRestaurantOwner(@Valid @RequestBody SignUpRequest request) {
        try {
            User user = userService.createUser(request.getEmail(), request.getPassword(), User.UserRole.RESTAURANT_OWNER);
            String token = authService.generateToken(user.getId(), user.getEmail());

            AuthResponse response = new AuthResponse(
                    "Restaurant owner created successfully",
                    user.getId(),
                    token
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(e.getMessage(), null, null));
        }
    }

    @PostMapping("/delivery-rider/signup")
    public ResponseEntity<AuthResponse> createDeliveryRider(@Valid @RequestBody SignUpRequest request) {
        try {
            User user = userService.createUser(request.getEmail(), request.getPassword(), User.UserRole.DELIVERY_RIDER);
            String token = authService.generateToken(user.getId(), user.getEmail());

            AuthResponse response = new AuthResponse(
                    "Delivery rider created successfully",
                    user.getId(),
                    token
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(e.getMessage(), null, null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.authenticateUser(request.getEmail(), request.getPassword());
            String token = authService.generateToken(user.getId(), user.getEmail());

            AuthResponse response = new AuthResponse(
                    "Login successful",
                    user.getId(),
                    token
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(e.getMessage(), null, null));
        }
    }

    @PostMapping("/guest-session")
    public ResponseEntity<GuestSessionResponse> createGuestSession() {
        try {
            GuestSession guestSession = guestSessionService.createGuestSession();
            
            long expiresIn = Duration.between(LocalDateTime.now(), guestSession.getExpiresAt()).toSeconds();
            
            GuestSessionResponse response = new GuestSessionResponse(
                    guestSession.getGuestSessionId(),
                    expiresIn
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
