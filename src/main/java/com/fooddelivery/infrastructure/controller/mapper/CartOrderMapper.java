package com.fooddelivery.infrastructure.controller.mapper;

import com.fooddelivery.application.port.MenuItemRepositoryPort;
import com.fooddelivery.domain.model.Cart;
import com.fooddelivery.domain.model.CartItem;
import com.fooddelivery.domain.model.MenuItem;
import com.fooddelivery.domain.model.Order;
import com.fooddelivery.infrastructure.controller.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CartOrderMapper {

    private final MenuItemRepositoryPort menuItemRepository;

    public List<CartItem> toCartItems(List<AddToCartRequest.CartItemRequest> cartItemRequests) {
        return cartItemRequests.stream()
                .map(request -> {
                    CartItem cartItem = new CartItem();
                    cartItem.setMenuItemId(request.getItemId());
                    cartItem.setQuantity(request.getQuantity());
                    // Price will be set by service after fetching menu item
                    return cartItem;
                })
                .collect(Collectors.toList());
    }

    public CartResponse toCartResponse(Cart cart) {
        List<CartItemDto> cartItemDtos = cart.getItems().stream()
                .map(this::toCartItemDto)
                .collect(Collectors.toList());

        return new CartResponse(
                cart.getRestaurantId(),
                cartItemDtos,
                cart.getTax(),
                cart.getDeliveryFee(),
                cart.getFinalPrice()
        );
    }

    private CartItemDto toCartItemDto(CartItem cartItem) {
        // Get menu item name
        String itemName = menuItemRepository.findById(cartItem.getMenuItemId())
                .map(MenuItem::getName)
                .orElse("Unknown Item");

        double total = cartItem.getPrice() * cartItem.getQuantity();

        return new CartItemDto(
                cartItem.getMenuItemId(),
                itemName,
                cartItem.getQuantity(),
                cartItem.getPrice(),
                total
        );
    }

    public CreateOrderResponse toCreateOrderResponse(Order order) {
        return new CreateOrderResponse(
                "Order placed successfully",
                order.getOrderId(),
                order.getEstimatedDeliveryTime(),
                order.getStatus().toString()
        );
    }

    public OrderStatusResponse toOrderStatusResponse(Order order) {
        return new OrderStatusResponse(
                order.getOrderId(),
                order.getStatus().toString(),
                order.getEstimatedDeliveryTime()
        );
    }

    public OrderHistoryDto toOrderHistoryDto(Order order) {
        List<OrderItemDto> orderItems = order.getItems().stream()
                .map(orderItem -> new OrderItemDto(
                        orderItem.getMenuItemId(),
                        orderItem.getName(),
                        orderItem.getPrice(),
                        orderItem.getQuantity(),
                        orderItem.getTotal()
                ))
                .collect(Collectors.toList());

        return new OrderHistoryDto(
                order.getOrderId(),
                order.getRestaurantId(),
                "Restaurant", // restaurantName - not available in order, could fetch from restaurant service
                order.getStatus().toString(),
                order.getPricing().getFinalPrice(),
                order.getPlacedAt(),
                order.getEstimatedDeliveryTime(),
                orderItems,
                order.getDeliveryAddress().getStreet() + ", " + 
                order.getDeliveryAddress().getArea() + ", " + 
                order.getDeliveryAddress().getCity()
        );
    }
}
