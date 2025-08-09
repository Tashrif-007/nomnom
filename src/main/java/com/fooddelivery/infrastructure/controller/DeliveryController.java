package com.fooddelivery.infrastructure.controller;

import com.fooddelivery.application.port.UserRepositoryPort;
import com.fooddelivery.application.service.DeliveryService;
import com.fooddelivery.domain.model.DeliveryRider;
import com.fooddelivery.domain.model.Order;
import com.fooddelivery.domain.model.User;
import com.fooddelivery.infrastructure.controller.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final UserRepositoryPort userRepository;

    @PostMapping("/rider/create-profile")
    public ResponseEntity<CreateDeliveryRiderResponse> createDeliveryRiderProfile(@Valid @RequestBody CreateDeliveryRiderRequest request) {
        try {
            DeliveryRider rider = deliveryService.createDeliveryRider(
                    request.getUserId(),
                    request.getVehicleType(),
                    request.getLicenseNumber()
            );

            return ResponseEntity.ok(new CreateDeliveryRiderResponse(
                    "Delivery rider profile created successfully",
                    rider.getId(),
                    rider.getVehicleType().toString(),
                    rider.getStatus().toString()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new CreateDeliveryRiderResponse(e.getMessage(), null, null, null));
        }
    }

    @PostMapping("/assign")
    public ResponseEntity<AssignRiderResponse> assignRiderToOrder(@Valid @RequestBody AssignRiderRequest request) {
        try {
            Order order = deliveryService.assignRiderToOrder(
                    request.getOrderId(),
                    request.getRiderId(),
                    request.getAssignedBy()
            );
            return ResponseEntity.ok(new AssignRiderResponse(
                    "Rider assigned successfully",
                    order.getOrderId(),
                    request.getRiderId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AssignRiderResponse(e.getMessage(), request.getOrderId(), null));
        }
    }

    @GetMapping("/riders")
    public ResponseEntity<List<AvailableRiderDto>> getAvailableRiders(
            @RequestParam(required = false) String status) {
        try {
            List<DeliveryRider> riders = deliveryService.getAvailableRiders();
            
            List<AvailableRiderDto> response = riders.stream()
                    .map(this::toAvailableRiderDto)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/rider/{riderId}/orders")
    public ResponseEntity<List<RiderOrderDto>> getOrdersForRider(@PathVariable String riderId) {
        try {
            List<Order> orders = deliveryService.getOrdersForRider(riderId);
            
            List<RiderOrderDto> response = orders.stream()
                    .map(this::toRiderOrderDto)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/order/{orderId}/status")
    public ResponseEntity<OrderStatusUpdateResponse> updateOrderStatusByRider(
            @PathVariable String orderId,
            @Valid @RequestBody UpdateDeliveryStatusRequest request) {
        try {
            Order order = deliveryService.updateOrderStatusByRider(orderId, request.getStatus());
            
            return ResponseEntity.ok(new OrderStatusUpdateResponse(
                    "Order status updated",
                    order.getOrderId(),
                    order.getStatus().toString()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new OrderStatusUpdateResponse(e.getMessage(), orderId, null));
        }
    }

    private AvailableRiderDto toAvailableRiderDto(DeliveryRider rider) {
        String riderName = userRepository.findById(rider.getUserId())
                .map(user -> user.getProfile() != null ? user.getProfile().getName() : "Rider")
                .orElse("Rider");

        return new AvailableRiderDto(
                rider.getId(),
                riderName,
                rider.getStatus().toString(),
                rider.getVehicleType().toString(),
                rider.getRating()
        );
    }

    private RiderOrderDto toRiderOrderDto(Order order) {
        String customerName = "Guest Customer";
        if (order.getUserId() != null) {
            customerName = userRepository.findById(order.getUserId())
                    .map(user -> user.getProfile() != null ? user.getProfile().getName() : "Customer")
                    .orElse("Customer");
        }

        String deliveryAddress = formatDeliveryAddress(order);

        return new RiderOrderDto(
                order.getOrderId(),
                customerName,
                deliveryAddress,
                order.getStatus().toString(),
                order.getPricing().getFinalPrice()
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
        
        return address.toString();
    }
}
