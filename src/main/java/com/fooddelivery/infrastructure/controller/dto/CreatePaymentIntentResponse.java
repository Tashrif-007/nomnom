package com.fooddelivery.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentIntentResponse {
    private String clientSecret;
    private String paymentIntentId;
    private String status;
    private String message;
}
