package com.fooddelivery.application.service;

import com.fooddelivery.domain.model.Payment;

public interface PaymentService {
    
    /**
     * Process payment for an order
     * @param orderId The order ID
     * @param amount Payment amount
     * @param paymentIntentId Stripe payment intent ID
     * @return Processed payment
     */
    Payment processPayment(String orderId, Double amount, String paymentIntentId);
    
    /**
     * Create a payment record
     * @param paymentIntentId Stripe payment intent ID
     * @param amount Payment amount
     * @param currency Currency code
     * @return Created payment
     */
    Payment createPayment(String paymentIntentId, Double amount, String currency);
    
    /**
     * Update payment status
     * @param paymentIntentId Stripe payment intent ID
     * @param status New payment status
     * @param failureReason Failure reason if payment failed
     * @return Updated payment
     */
    Payment updatePaymentStatus(String paymentIntentId, Payment.PaymentStatus status, String failureReason);
    
    /**
     * Get payment by Stripe payment intent ID
     * @param paymentIntentId Stripe payment intent ID
     * @return Payment if found
     */
    Payment getPaymentByIntentId(String paymentIntentId);
    
    /**
     * Refund a payment
     * @param paymentIntentId Stripe payment intent ID
     * @param amount Refund amount (null for full refund)
     * @return Updated payment
     */
    Payment refundPayment(String paymentIntentId, Double amount);
}
