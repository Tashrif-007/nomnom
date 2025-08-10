package com.fooddelivery.application.service.impl;

import com.fooddelivery.application.service.PaymentService;
import com.fooddelivery.domain.model.Payment;
import com.fooddelivery.infrastructure.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final StripeService stripeService;
    
    // In-memory storage for demo purposes
    // In a real application, this would be a database repository
    private final Map<String, Payment> paymentStore = new ConcurrentHashMap<>();

    @Override
    public Payment processPayment(String orderId, Double amount, String paymentIntentId) {
        log.info("Processing payment for order: {} with payment intent: {}", orderId, paymentIntentId);
        
        try {
            // Retrieve payment intent from Stripe to verify status
            PaymentIntent paymentIntent = stripeService.retrievePaymentIntent(paymentIntentId);
            
            Payment payment = paymentStore.get(paymentIntentId);
            if (payment == null) {
                payment = createPayment(paymentIntentId, amount, "usd");
            }
            
            // Update payment status based on Stripe status
            Payment.PaymentStatus status = mapStripeStatusToPaymentStatus(paymentIntent.getStatus());
            payment.setStatus(status);
            payment.setUpdatedAt(LocalDateTime.now());
            
            if ("succeeded".equals(paymentIntent.getStatus())) {
                payment.setStripeChargeId(paymentIntent.getLatestChargeObject() != null ? 
                    paymentIntent.getLatestChargeObject().getId() : null);
            }
            
            paymentStore.put(paymentIntentId, payment);
            
            log.info("Payment processed successfully for order: {} with status: {}", orderId, status);
            return payment;
            
        } catch (StripeException e) {
            log.error("Failed to process payment for order: {}", orderId, e);
            Payment payment = paymentStore.getOrDefault(paymentIntentId, 
                createPayment(paymentIntentId, amount, "usd"));
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            payment.setUpdatedAt(LocalDateTime.now());
            paymentStore.put(paymentIntentId, payment);
            return payment;
        }
    }

    @Override
    public Payment createPayment(String paymentIntentId, Double amount, String currency) {
        Payment payment = new Payment();
        payment.setStripePaymentIntentId(paymentIntentId);
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setMethod(Payment.PaymentMethod.CARD);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        
        paymentStore.put(paymentIntentId, payment);
        
        log.info("Created payment record for payment intent: {}", paymentIntentId);
        return payment;
    }

    @Override
    public Payment updatePaymentStatus(String paymentIntentId, Payment.PaymentStatus status, String failureReason) {
        Payment payment = paymentStore.get(paymentIntentId);
        if (payment == null) {
            log.warn("Payment not found for payment intent: {}", paymentIntentId);
            return null;
        }
        
        payment.setStatus(status);
        payment.setFailureReason(failureReason);
        payment.setUpdatedAt(LocalDateTime.now());
        
        paymentStore.put(paymentIntentId, payment);
        
        log.info("Updated payment status for payment intent: {} to status: {}", paymentIntentId, status);
        return payment;
    }

    @Override
    public Payment getPaymentByIntentId(String paymentIntentId) {
        return paymentStore.get(paymentIntentId);
    }

    @Override
    public Payment refundPayment(String paymentIntentId, Double amount) {
        Payment payment = paymentStore.get(paymentIntentId);
        if (payment == null) {
            log.warn("Payment not found for refund: {}", paymentIntentId);
            return null;
        }
        
        try {
            // Process refund through Stripe
            Map<String, Object> refundParams = new HashMap<>();
            refundParams.put("payment_intent", paymentIntentId);
            if (amount != null) {
                refundParams.put("amount", stripeService.convertToCents(amount));
            }
            
            Refund refund = Refund.create(refundParams);
            
            // Update payment status
            payment.setStatus(Payment.PaymentStatus.REFUNDED);
            payment.setUpdatedAt(LocalDateTime.now());
            paymentStore.put(paymentIntentId, payment);
            
            log.info("Refund processed successfully for payment intent: {}", paymentIntentId);
            return payment;
            
        } catch (StripeException e) {
            log.error("Failed to process refund for payment intent: {}", paymentIntentId, e);
            payment.setFailureReason("Refund failed: " + e.getMessage());
            payment.setUpdatedAt(LocalDateTime.now());
            paymentStore.put(paymentIntentId, payment);
            return payment;
        }
    }
    
    private Payment.PaymentStatus mapStripeStatusToPaymentStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "requires_payment_method", "requires_confirmation", "requires_action" -> 
                Payment.PaymentStatus.PENDING;
            case "processing" -> Payment.PaymentStatus.PROCESSING;
            case "succeeded" -> Payment.PaymentStatus.COMPLETED;
            case "canceled" -> Payment.PaymentStatus.CANCELLED;
            default -> Payment.PaymentStatus.FAILED;
        };
    }
}
