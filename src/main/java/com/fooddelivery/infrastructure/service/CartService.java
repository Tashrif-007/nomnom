package com.fooddelivery.application.service;

import com.fooddelivery.domain.model.Cart;
import com.fooddelivery.domain.model.CartItem;

import java.util.List;

public interface CartService {
    Cart addItemsToCart(String userId, String guestSessionId, String restaurantId, List<CartItem> items);
    Cart getCartByUser(String userId);
    Cart getCartByGuestSession(String guestSessionId);
    Cart updateCartItem(String cartId, String menuItemId, int quantity);
    Cart removeCartItem(String cartId, String menuItemId);
    void clearCart(String cartId);
    Cart calculateCartTotals(Cart cart);
}
