package com.fooddelivery.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private PaymentMethod method;
    private PaymentStatus status;
    private String stripePaymentIntentId;
    private String stripeChargeId;
    private Double amount;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String failureReason;
    
    public enum PaymentMethod {
        COD, CARD, DIGITAL_WALLET
    }
    
    public enum PaymentStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, REFUNDED
    }
}
