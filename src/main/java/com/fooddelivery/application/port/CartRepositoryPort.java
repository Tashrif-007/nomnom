package com.fooddelivery.application.port;

import com.fooddelivery.domain.model.Cart;

import java.util.Optional;

public interface CartRepositoryPort {
    Cart save(Cart cart);
    Optional<Cart> findByUserId(String userId);
    Optional<Cart> findByGuestSessionId(String guestSessionId);
    void deleteById(String id);
}
