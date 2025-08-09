package com.fooddelivery.infrastructure.controller;

import com.fooddelivery.application.service.UserService;
import com.fooddelivery.domain.model.Address;
import com.fooddelivery.domain.model.User;
import com.fooddelivery.infrastructure.controller.dto.*;
import com.fooddelivery.infrastructure.controller.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(@RequestParam String userId) {
        try {
            User user = userService.getUserById(userId);
            UserProfileResponse response = userMapper.toUserProfileResponse(user);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateUserProfile(
            @RequestParam String userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        try {
            userService.updateUserProfile(userId, request.getName(), request.getPhone());
            return ResponseEntity.ok(new ApiResponse("Profile updated successfully", userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage()));
        }
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressResponse> addAddress(
            @RequestParam String userId,
            @Valid @RequestBody AddAddressRequest request) {
        try {
            Address address = new Address();
            address.setType(request.getType());
            address.setStreet(request.getStreet());
            address.setCity(request.getCity());
            address.setArea(request.getArea());
            address.setDefault(request.isDefault());

            User user = userService.addAddress(userId, address);
            
            // Find the newly added address ID
            String addressId = user.getProfile().getAddresses().stream()
                    .filter(addr -> addr.getStreet().equals(request.getStreet()))
                    .findFirst()
                    .map(Address::getId)
                    .orElse(null);

            return ResponseEntity.ok(new AddressResponse("Address added successfully", addressId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AddressResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<ApiResponse> updateAddress(
            @RequestParam String userId,
            @PathVariable String addressId,
            @Valid @RequestBody AddAddressRequest request) {
        try {
            Address updatedAddress = new Address();
            updatedAddress.setType(request.getType());
            updatedAddress.setStreet(request.getStreet());
            updatedAddress.setCity(request.getCity());
            updatedAddress.setArea(request.getArea());
            updatedAddress.setDefault(request.isDefault());

            userService.updateAddress(userId, addressId, updatedAddress);
            return ResponseEntity.ok(new ApiResponse("Address updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<ApiResponse> deleteAddress(
            @RequestParam String userId,
            @PathVariable String addressId) {
        try {
            userService.deleteAddress(userId, addressId);
            return ResponseEntity.ok(new ApiResponse("Address deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage()));
        }
    }

    @PatchMapping("/addresses/{addressId}/default")
    public ResponseEntity<ApiResponse> setDefaultAddress(
            @RequestParam String userId,
            @PathVariable String addressId) {
        try {
            userService.setDefaultAddress(userId, addressId);
            return ResponseEntity.ok(new ApiResponse("Default address updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage()));
        }
    }
}
