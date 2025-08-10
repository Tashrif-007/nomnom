package com.fooddelivery.application.service.impl;

import com.fooddelivery.application.port.CartRepositoryPort;
import com.fooddelivery.application.port.MenuItemRepositoryPort;
import com.fooddelivery.application.service.CartService;
import com.fooddelivery.domain.model.Cart;
import com.fooddelivery.domain.model.CartItem;
import com.fooddelivery.domain.model.MenuItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepositoryPort cartRepository;
    private final MenuItemRepositoryPort menuItemRepository;

    // Constants for pricing calculations
    private static final double TAX_RATE = 0.10; // 10% tax
    private static final double DELIVERY_FEE = 50.0; // Fixed delivery fee

    @Override
    public Cart addItemsToCart(String userId, String guestSessionId, String restaurantId, List<CartItem> items) {
        // Find existing cart or create new one
        Optional<Cart> existingCart = userId != null ? 
                cartRepository.findByUserId(userId) : 
                cartRepository.findByGuestSessionId(guestSessionId);

        Cart cart;
        if (existingCart.isPresent()) {
            cart = existingCart.get();
            
            // Check if trying to add items from different restaurant
            if (!cart.getRestaurantId().equals(restaurantId)) {
                throw new RuntimeException("Cannot add items from different restaurant. Please clear cart first.");
            }
        } else {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setGuestSessionId(guestSessionId);
            cart.setRestaurantId(restaurantId);
            cart.setItems(new ArrayList<>());
            cart.setCreatedAt(LocalDateTime.now());
        }

        // Validate menu items and add to cart
        for (CartItem newItem : items) {
            MenuItem menuItem = menuItemRepository.findById(newItem.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found: " + newItem.getMenuItemId()));

            if (!menuItem.isAvailable()) {
                throw new RuntimeException("Menu item is not available: " + menuItem.getName());
            }

            // Check if item already exists in cart
            Optional<CartItem> existingItem = cart.getItems().stream()
                    .filter(item -> item.getMenuItemId().equals(newItem.getMenuItemId()))
                    .findFirst();

            if (existingItem.isPresent()) {
                // Update quantity
                existingItem.get().setQuantity(existingItem.get().getQuantity() + newItem.getQuantity());
            } else {
                // Add new item
                newItem.setPrice(menuItem.getPrice());
                cart.getItems().add(newItem);
            }
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cart = calculateCartTotals(cart);
        
        return cartRepository.save(cart);
    }

    @Override
    public Cart getCartByUser(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));
    }

    @Override
    public Cart getCartByGuestSession(String guestSessionId) {
        return cartRepository.findByGuestSessionId(guestSessionId)
                .orElseThrow(() -> new RuntimeException("Cart not found for guest session"));
    }

    @Override
    public Cart updateCartItem(String cartId, String menuItemId, int quantity) {
        // This would require additional repository method - simplified for now
        throw new RuntimeException("Method not implemented yet");
    }

    @Override
    public Cart removeCartItem(String cartId, String menuItemId) {
        // This would require additional repository method - simplified for now
        throw new RuntimeException("Method not implemented yet");
    }

    @Override
    public void clearCart(String cartId) {
        cartRepository.deleteById(cartId);
    }

    @Override
    public Cart calculateCartTotals(Cart cart) {
        double subtotal = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        double tax = subtotal * TAX_RATE;
        double finalPrice = subtotal + tax + DELIVERY_FEE;

        cart.setSubtotal(subtotal);
        cart.setTax(tax);
        cart.setDeliveryFee(DELIVERY_FEE);
        cart.setFinalPrice(finalPrice);

        return cart;
    }
}
