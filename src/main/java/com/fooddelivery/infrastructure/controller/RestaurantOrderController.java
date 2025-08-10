package com.fooddelivery.infrastructure.controller;

import com.fooddelivery.application.service.OrderService;
import com.fooddelivery.domain.model.Order;
import com.fooddelivery.domain.model.OrderStatus;
import com.fooddelivery.infrastructure.controller.dto.*;
import com.fooddelivery.infrastructure.controller.mapper.RestaurantOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/restaurant")
@RequiredArgsConstructor
public class RestaurantOrderController {

    private final OrderService orderService;
    private final RestaurantOrderMapper restaurantOrderMapper;

    @GetMapping("/{restaurantId}/orders")
    public ResponseEntity<List<RestaurantOrderDto>> getIncomingOrders(
            @PathVariable String restaurantId,
            @RequestParam(required = false) String status) {
        try {
            OrderStatus orderStatus = null;
            if (status != null) {
                try {
                    orderStatus = OrderStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // Default to pending if invalid status
                    orderStatus = OrderStatus.ORDER_RECEIVED;
                }
            } else {
                // Default to pending orders
                orderStatus = OrderStatus.ORDER_RECEIVED;
            }

            List<Order> orders = orderService.getOrdersByRestaurant(restaurantId, orderStatus);
            
            List<RestaurantOrderDto> response = orders.stream()
                    .map(restaurantOrderMapper::toRestaurantOrderDto)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderStatusUpdateResponse> acceptRejectOrder(
            @PathVariable String orderId,
            @Valid @RequestBody AcceptOrderRequest request) {
        try {
            OrderStatus newStatus;
            String rejectionReason = null;
            Integer prepTime = null;

            if ("Accepted".equalsIgnoreCase(request.getStatus())) {
                newStatus = OrderStatus.ACCEPTED;
                prepTime = request.getPrepTime();
            } else if ("Rejected".equalsIgnoreCase(request.getStatus())) {
                newStatus = OrderStatus.REJECTED;
                rejectionReason = request.getReason();
            } else {
                return ResponseEntity.badRequest()
                        .body(new OrderStatusUpdateResponse("Invalid status. Use 'Accepted' or 'Rejected'", orderId, null));
            }

            Order updatedOrder = orderService.updateOrderStatus(orderId, newStatus, rejectionReason, prepTime);
            
            return ResponseEntity.ok(new OrderStatusUpdateResponse(
                    "Order updated successfully",
                    updatedOrder.getOrderId(),
                    updatedOrder.getStatus().toString()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new OrderStatusUpdateResponse(e.getMessage(), orderId, null));
        }
    }

    @PutMapping("/orders/{orderId}/progress")
    public ResponseEntity<OrderStatusUpdateResponse> updateOrderProgress(
            @PathVariable String orderId,
            @Valid @RequestBody UpdateOrderProgressRequest request) {
        try {
            OrderStatus newStatus;
            
            switch (request.getStatus().toUpperCase()) {
                case "PREPARING":
                    newStatus = OrderStatus.PREPARING;
                    break;
                case "OUT FOR DELIVERY":
                case "OUT_FOR_DELIVERY":
                    newStatus = OrderStatus.OUT_FOR_DELIVERY;
                    break;
                case "DELIVERED":
                    newStatus = OrderStatus.DELIVERED;
                    break;
                default:
                    return ResponseEntity.badRequest()
                            .body(new OrderStatusUpdateResponse(
                                    "Invalid status. Use 'Preparing', 'Out for Delivery', or 'Delivered'", 
                                    orderId, null));
            }

            Order updatedOrder = orderService.updateOrderStatus(orderId, newStatus, null, null);
            
            return ResponseEntity.ok(new OrderStatusUpdateResponse(
                    "Order status updated",
                    updatedOrder.getOrderId(),
                    updatedOrder.getStatus().toString()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new OrderStatusUpdateResponse(e.getMessage(), orderId, null));
        }
    }
}
