package com.fooddelivery.application.service.impl;

import com.fooddelivery.application.port.UserRepositoryPort;
import com.fooddelivery.application.service.UserService;
import com.fooddelivery.domain.model.Address;
import com.fooddelivery.domain.model.User;
import com.fooddelivery.domain.model.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(String email, String password, User.UserRole role) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User with email already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setProfile(new UserProfile(null, null, false, new ArrayList<>()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return user;
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User updateUserProfile(String userId, String name, String phone) {
        User user = getUserById(userId);
        
        if (user.getProfile() == null) {
            user.setProfile(new UserProfile());
        }
        
        user.getProfile().setName(name);
        user.getProfile().setPhone(phone);
        user.getProfile().setProfileCompleted(true);
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    public User addAddress(String userId, Address address) {
        User user = getUserById(userId);
        
        if (user.getProfile() == null) {
            user.setProfile(new UserProfile());
        }
        
        if (user.getProfile().getAddresses() == null) {
            user.getProfile().setAddresses(new ArrayList<>());
        }

        address.setId(UUID.randomUUID().toString());
        
        // If this is the first address, make it default
        if (user.getProfile().getAddresses().isEmpty()) {
            address.setDefault(true);
        }
        
        // If setting as default, unset others
        if (address.isDefault()) {
            user.getProfile().getAddresses().forEach(addr -> addr.setDefault(false));
        }

        user.getProfile().getAddresses().add(address);
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    public User updateAddress(String userId, String addressId, Address updatedAddress) {
        User user = getUserById(userId);
        
        List<Address> addresses = user.getProfile().getAddresses();
        Address existingAddress = addresses.stream()
                .filter(addr -> addr.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Address not found"));

        existingAddress.setType(updatedAddress.getType());
        existingAddress.setStreet(updatedAddress.getStreet());
        existingAddress.setCity(updatedAddress.getCity());
        existingAddress.setArea(updatedAddress.getArea());
        
        // If setting as default, unset others
        if (updatedAddress.isDefault()) {
            addresses.forEach(addr -> addr.setDefault(false));
            existingAddress.setDefault(true);
        }

        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public User deleteAddress(String userId, String addressId) {
        User user = getUserById(userId);
        
        List<Address> addresses = user.getProfile().getAddresses();
        Address addressToRemove = addresses.stream()
                .filter(addr -> addr.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Address not found"));

        addresses.remove(addressToRemove);
        
        // If removed address was default, set first remaining as default
        if (addressToRemove.isDefault() && !addresses.isEmpty()) {
            addresses.get(0).setDefault(true);
        }

        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public User setDefaultAddress(String userId, String addressId) {
        User user = getUserById(userId);
        
        List<Address> addresses = user.getProfile().getAddresses();
        
        // Unset all as default
        addresses.forEach(addr -> addr.setDefault(false));
        
        // Set specified address as default
        Address defaultAddress = addresses.stream()
                .filter(addr -> addr.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Address not found"));
        
        defaultAddress.setDefault(true);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
