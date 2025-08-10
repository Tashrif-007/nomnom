package com.fooddelivery.infrastructure.persistence.repository;

import com.fooddelivery.domain.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartMongoRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUserId(String userId);
    Optional<Cart> findByGuestSessionId(String guestSessionId);
}
