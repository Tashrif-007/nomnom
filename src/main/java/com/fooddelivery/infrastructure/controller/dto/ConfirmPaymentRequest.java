package com.fooddelivery.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmPaymentRequest {
    @NotBlank(message = "Payment Intent ID is required")
    private String paymentIntentId;
    
    @NotBlank(message = "Payment Method ID is required")
    private String paymentMethodId;
}
