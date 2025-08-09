package com.fooddelivery.infrastructure.controller;

import com.fooddelivery.application.service.CartService;
import com.fooddelivery.application.service.OrderService;
import com.fooddelivery.domain.model.*;
import com.fooddelivery.infrastructure.controller.dto.*;
import com.fooddelivery.infrastructure.controller.mapper.CartOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CartOrderController {

    private final CartService cartService;
    private final OrderService orderService;
    private final CartOrderMapper cartOrderMapper;

    @PostMapping("/cart")
    public ResponseEntity<AddToCartResponse> addItemsToCart(@Valid @RequestBody AddToCartRequest request) {
        try {
            List<CartItem> cartItems = cartOrderMapper.toCartItems(request.getItems());
            
            Cart cart = cartService.addItemsToCart(
                    request.getUserId(),
                    request.getGuestSessionId(),
                    request.getRestaurantId(),
                    cartItems
            );
            
            return ResponseEntity.ok(new AddToCartResponse("Items added to cart", cart.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AddToCartResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/cart/{userId}")
    public ResponseEntity<CartResponse> viewCart(@PathVariable String userId) {
        try {
            Cart cart = cartService.getCartByUser(userId);
            CartResponse response = cartOrderMapper.toCartResponse(cart);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/orders")
    public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            // Create payment object
            Payment payment = new Payment();
            payment.setMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()));
            payment.setStatus(Payment.PaymentStatus.PENDING);
            
            if (request.getPaymentInfo() != null) {
                if (request.getPaymentInfo().getStripePaymentIntentId() != null) {
                    payment.setStripePaymentIntentId(request.getPaymentInfo().getStripePaymentIntentId());
                }
                if (request.getPaymentInfo().getStripeChargeId() != null) {
                    payment.setStripeChargeId(request.getPaymentInfo().getStripeChargeId());
                }
            }

            Order order = orderService.createOrder(
                    request.getUserId(),
                    request.getGuestSessionId(),
                    request.getCartId(),
                    request.getDeliveryAddress(),
                    payment
            );
            
            CreateOrderResponse response = cartOrderMapper.toCreateOrderResponse(order);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new CreateOrderResponse(e.getMessage(), null, 0, null));
        }
    }

    @GetMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderStatusResponse> getOrderStatus(@PathVariable String orderId) {
        try {
            Order order = orderService.getOrderByOrderId(orderId);
            OrderStatusResponse response = cartOrderMapper.toOrderStatusResponse(order);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<List<OrderHistoryDto>> getUserOrders(@PathVariable String userId) {
        try {
            List<Order> orders = orderService.getOrdersByUser(userId);
            List<OrderHistoryDto> response = orders.stream()
                    .map(cartOrderMapper::toOrderHistoryDto)
                    .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/cart/{cartId}/items/{itemId}")
    public ResponseEntity<ApiResponse> updateCartItem(
            @PathVariable String cartId,
            @PathVariable String itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        try {
            cartService.updateCartItem(cartId, itemId, request.getQuantity());
            return ResponseEntity.ok(new ApiResponse("Cart item updated successfully", cartId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/cart/{cartId}/items/{itemId}")
    public ResponseEntity<ApiResponse> removeCartItem(
            @PathVariable String cartId,
            @PathVariable String itemId) {
        try {
            cartService.removeCartItem(cartId, itemId);
            return ResponseEntity.ok(new ApiResponse("Item removed from cart", cartId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/cart/{cartId}")
    public ResponseEntity<ApiResponse> clearCart(@PathVariable String cartId) {
        try {
            cartService.clearCart(cartId);
            return ResponseEntity.ok(new ApiResponse("Cart cleared successfully", cartId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage()));
        }
    }
}
