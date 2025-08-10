package com.fooddelivery.application.service.impl;

import com.fooddelivery.application.port.CartRepositoryPort;
import com.fooddelivery.application.port.MenuItemRepositoryPort;
import com.fooddelivery.application.port.OrderRepositoryPort;
import com.fooddelivery.application.service.OrderService;
import com.fooddelivery.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepositoryPort orderRepository;
    private final CartRepositoryPort cartRepository;
    private final MenuItemRepositoryPort menuItemRepository;
    
    private static final AtomicLong orderCounter = new AtomicLong(1000);

    @Override
    public Order createOrder(String userId, String guestSessionId, String cartId, 
                           String deliveryAddressStr, Payment payment) {
        // Get cart
        Cart cart = cartRepository.findByUserId(userId != null ? userId : guestSessionId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Create order
        Order order = new Order();
        order.setOrderId(generateOrderId());
        order.setUserId(userId);
        order.setGuestSessionId(guestSessionId);
        order.setRestaurantId(cart.getRestaurantId());

        // Convert cart items to order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(cartItem.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItemId(cartItem.getMenuItemId());
            orderItem.setName(menuItem.getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setTotal(cartItem.getPrice() * cartItem.getQuantity());
            
            orderItems.add(orderItem);
        }
        order.setItems(orderItems);

        // Set pricing
        OrderPricing pricing = new OrderPricing();
        pricing.setSubtotal(cart.getSubtotal());
        pricing.setTax(cart.getTax());
        pricing.setDeliveryFee(cart.getDeliveryFee());
        pricing.setFinalPrice(cart.getFinalPrice());
        order.setPricing(pricing);

        // Set delivery address
        DeliveryAddress deliveryAddress = parseDeliveryAddress(deliveryAddressStr);
        order.setDeliveryAddress(deliveryAddress);

        // Set payment
        order.setPayment(payment);

        // Set order status and timeline
        order.setStatus(OrderStatus.ORDER_RECEIVED);
        order.setEstimatedDeliveryTime(45); // Default 45 minutes

        List<OrderTimeline> timeline = new ArrayList<>();
        timeline.add(new OrderTimeline(OrderStatus.ORDER_RECEIVED, LocalDateTime.now()));
        order.setTimeline(timeline);

        order.setPlacedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Clear cart after successful order
        cartRepository.deleteById(cart.getId());

        return savedOrder;
    }

    @Override
    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public Order getOrderByOrderId(String humanReadableOrderId) {
        return orderRepository.findByOrderId(humanReadableOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public Order updateOrderStatus(String orderId, OrderStatus status, String rejectionReason, Integer prepTime) {
        Order order = getOrderById(orderId);
        
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        if (rejectionReason != null) {
            order.setRejectionReason(rejectionReason);
        }
        
        if (prepTime != null) {
            order.setPrepTime(prepTime);
        }

        // Add to timeline
        order.getTimeline().add(new OrderTimeline(status, LocalDateTime.now()));

        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByRestaurant(String restaurantId, OrderStatus status) {
        if (status != null) {
            return orderRepository.findByRestaurantIdAndStatus(restaurantId, status);
        } else {
            // Return all orders for restaurant - would need additional repository method
            throw new RuntimeException("Method not fully implemented - need repository method for all restaurant orders");
        }
    }

    @Override
    public List<Order> getOrdersByUser(String userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> getOrdersByGuestSession(String guestSessionId) {
        return orderRepository.findByGuestSessionId(guestSessionId);
    }

    @Override
    public List<Order> getOrdersByRider(String riderId) {
        return orderRepository.findByRiderId(riderId);
    }

    @Override
    public Order assignRiderToOrder(String orderId, String riderId) {
        Order order = getOrderById(orderId);
        order.setRiderId(riderId);
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderRepository.save(order);
    }

    @Override
    public String generateOrderId() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long counter = orderCounter.incrementAndGet();
        return "ORD-" + datePart + "-" + counter;
    }

    private DeliveryAddress parseDeliveryAddress(String addressStr) {
        // Simple parsing - in real app, this would be more sophisticated
        String[] parts = addressStr.split(",");
        DeliveryAddress address = new DeliveryAddress();
        address.setStreet(parts.length > 0 ? parts[0].trim() : addressStr);
        address.setCity(parts.length > 1 ? parts[1].trim() : "Dhaka");
        address.setArea(parts.length > 2 ? parts[2].trim() : null);
        return address;
    }
}
