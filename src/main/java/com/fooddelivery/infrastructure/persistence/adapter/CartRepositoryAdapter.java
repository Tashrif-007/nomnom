package com.fooddelivery.infrastructure.persistence.adapter;

import com.fooddelivery.application.port.CartRepositoryPort;
import com.fooddelivery.domain.model.Cart;
import com.fooddelivery.infrastructure.persistence.repository.CartMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CartRepositoryAdapter implements CartRepositoryPort {

    private final CartMongoRepository cartMongoRepository;

    @Override
    public Cart save(Cart cart) {
        return cartMongoRepository.save(cart);
    }

    @Override
    public Optional<Cart> findByUserId(String userId) {
        return cartMongoRepository.findByUserId(userId);
    }

    @Override
    public Optional<Cart> findByGuestSessionId(String guestSessionId) {
        return cartMongoRepository.findByGuestSessionId(guestSessionId);
    }

    @Override
    public void deleteById(String id) {
        cartMongoRepository.deleteById(id);
    }
}
