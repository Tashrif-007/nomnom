package com.fooddelivery.infrastructure.controller.mapper;

import com.fooddelivery.application.port.UserRepositoryPort;
import com.fooddelivery.domain.model.Order;
import com.fooddelivery.domain.model.OrderItem;
import com.fooddelivery.domain.model.User;
import com.fooddelivery.infrastructure.controller.dto.RestaurantOrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RestaurantOrderMapper {

    private final UserRepositoryPort userRepository;

    public RestaurantOrderDto toRestaurantOrderDto(Order order) {
        // Get customer name
        String customerName = "Guest Customer";
        if (order.getUserId() != null) {
            customerName = userRepository.findById(order.getUserId())
                    .map(user -> user.getProfile() != null ? user.getProfile().getName() : "Customer")
                    .orElse("Customer");
        }

        // Map order items
        List<RestaurantOrderDto.OrderItemDto> orderItems = order.getItems().stream()
                .map(this::toOrderItemDto)
                .collect(Collectors.toList());

        // Format delivery address
        String deliveryAddressStr = formatDeliveryAddress(order);

        return new RestaurantOrderDto(
                order.getOrderId(),
                customerName,
                orderItems,
                order.getPricing().getFinalPrice(),
                deliveryAddressStr,
                order.getStatus().toString(),
                order.getPlacedAt()
        );
    }

    private RestaurantOrderDto.OrderItemDto toOrderItemDto(OrderItem orderItem) {
        return new RestaurantOrderDto.OrderItemDto(
                orderItem.getName(),
                orderItem.getQuantity(),
                orderItem.getPrice()
        );
    }

    private String formatDeliveryAddress(Order order) {
        if (order.getDeliveryAddress() == null) {
            return "No address provided";
        }
        
        StringBuilder address = new StringBuilder();
        if (order.getDeliveryAddress().getStreet() != null) {
            address.append(order.getDeliveryAddress().getStreet());
        }
        if (order.getDeliveryAddress().getCity() != null) {
            if (address.length() > 0) address.append(", ");
            address.append(order.getDeliveryAddress().getCity());
        }
        if (order.getDeliveryAddress().getArea() != null) {
            if (address.length() > 0) address.append(", ");
            address.append(order.getDeliveryAddress().getArea());
        }
        
        return address.toString();
    }
}
