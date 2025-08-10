package com.fooddelivery.infrastructure.controller.dto;

import com.fooddelivery.domain.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private String userId; // Optional - for registered users
    private String guestSessionId; // Optional - for guest users
    
    @NotBlank(message = "Cart ID is required")
    private String cartId;
    
    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // "COD" or "CARD"
    
    private PaymentInfo paymentInfo;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentInfo {
        private String stripePaymentIntentId; // For card payments
        private String stripeChargeId; // Optional, for completed payments
    }
}
