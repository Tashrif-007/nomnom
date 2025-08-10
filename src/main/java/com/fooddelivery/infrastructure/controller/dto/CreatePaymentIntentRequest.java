package com.fooddelivery.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentIntentRequest {
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;
    
    private String currency = "usd";
    private String customerId;
    private Map<String, String> metadata;
}
