package com.fooddelivery.application.port;

import com.fooddelivery.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteById(String id);
    List<User> findByRole(User.UserRole role);
}
