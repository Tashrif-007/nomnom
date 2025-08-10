package com.fooddelivery.infrastructure.persistence.adapter;

import com.fooddelivery.application.port.UserRepositoryPort;
import com.fooddelivery.domain.model.User;
import com.fooddelivery.infrastructure.persistence.repository.UserMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserMongoRepository userMongoRepository;

    @Override
    public User save(User user) {
        return userMongoRepository.save(user);
    }

    @Override
    public Optional<User> findById(String id) {
        return userMongoRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userMongoRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMongoRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(String id) {
        userMongoRepository.deleteById(id);
    }

    @Override
    public List<User> findByRole(User.UserRole role) {
        return userMongoRepository.findByRole(role);
    }
}
