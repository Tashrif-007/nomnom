package com.fooddelivery.application.service;

import com.fooddelivery.domain.model.User;
import com.fooddelivery.domain.model.Address;

public interface UserService {
    User createUser(String email, String password, User.UserRole role);
    User authenticateUser(String email, String password);
    User getUserById(String userId);
    User getUserByEmail(String email);
    User updateUserProfile(String userId, String name, String phone);
    User addAddress(String userId, Address address);
    User updateAddress(String userId, String addressId, Address address);
    User deleteAddress(String userId, String addressId);
    User setDefaultAddress(String userId, String addressId);
    boolean existsByEmail(String email);
}
