package com.fooddelivery.infrastructure.controller;

import com.fooddelivery.application.service.PaymentService;
import com.fooddelivery.domain.model.Payment;
import com.fooddelivery.infrastructure.config.StripeConfig;
import com.fooddelivery.infrastructure.controller.dto.*;
import com.fooddelivery.infrastructure.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class PaymentController {

    private final StripeService stripeService;
    private final PaymentService paymentService;
    private final StripeConfig stripeConfig;

    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> getStripeConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("publicKey", stripeConfig.getPublicKey());
        config.put("currency", stripeConfig.getCurrency());
        return ResponseEntity.ok(config);
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<CreatePaymentIntentResponse> createPaymentIntent(
            @Valid @RequestBody CreatePaymentIntentRequest request) {
        try {
            long amountInCents = stripeService.convertToCents(request.getAmount());
            
            // Add order metadata if provided
            Map<String, String> metadata = request.getMetadata();
            if (metadata == null) {
                metadata = new HashMap<>();
            }
            metadata.put("source", "food-delivery-app");
            
            PaymentIntent paymentIntent = stripeService.createPaymentIntent(
                    amountInCents,
                    request.getCurrency(),
                    request.getCustomerId(),
                    metadata
            );

            // Create payment record in our system
            paymentService.createPayment(paymentIntent.getId(), request.getAmount(), request.getCurrency());

            CreatePaymentIntentResponse response = new CreatePaymentIntentResponse(
                    paymentIntent.getClientSecret(),
                    paymentIntent.getId(),
                    paymentIntent.getStatus(),
                    "Payment intent created successfully"
            );

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            log.error("Failed to create payment intent", e);
            CreatePaymentIntentResponse errorResponse = new CreatePaymentIntentResponse(
                    null,
                    null,
                    "failed",
                    "Failed to create payment intent: " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error creating payment intent", e);
            CreatePaymentIntentResponse errorResponse = new CreatePaymentIntentResponse(
                    null,
                    null,
                    "failed",
                    "An unexpected error occurred"
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<PaymentResponse> confirmPayment(
            @Valid @RequestBody ConfirmPaymentRequest request) {
        try {
            PaymentIntent paymentIntent = stripeService.confirmPaymentIntent(
                    request.getPaymentIntentId(),
                    request.getPaymentMethodId()
            );

            // Update payment status in our system
            Payment.PaymentStatus status = mapStripeStatusToPaymentStatus(paymentIntent.getStatus());
            paymentService.updatePaymentStatus(request.getPaymentIntentId(), status, null);

            PaymentResponse response = new PaymentResponse(
                    paymentIntent.getId(),
                    paymentIntent.getStatus(),
                    "Payment confirmed successfully",
                    paymentIntent.getClientSecret()
            );

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            log.error("Failed to confirm payment", e);
            // Update payment status to failed
            paymentService.updatePaymentStatus(request.getPaymentIntentId(), 
                Payment.PaymentStatus.FAILED, e.getMessage());
            
            PaymentResponse errorResponse = new PaymentResponse(
                    request.getPaymentIntentId(),
                    "failed",
                    "Failed to confirm payment: " + e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error confirming payment", e);
            PaymentResponse errorResponse = new PaymentResponse(
                    request.getPaymentIntentId(),
                    "failed",
                    "An unexpected error occurred",
                    null
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/payment-intent/{paymentIntentId}")
    public ResponseEntity<PaymentResponse> getPaymentIntent(@PathVariable String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = stripeService.retrievePaymentIntent(paymentIntentId);

            PaymentResponse response = new PaymentResponse(
                    paymentIntent.getId(),
                    paymentIntent.getStatus(),
                    "Payment intent retrieved successfully",
                    paymentIntent.getClientSecret()
            );

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            log.error("Failed to retrieve payment intent", e);
            PaymentResponse errorResponse = new PaymentResponse(
                    paymentIntentId,
                    "failed",
                    "Failed to retrieve payment intent: " + e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/cancel-payment/{paymentIntentId}")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = stripeService.cancelPaymentIntent(paymentIntentId);

            // Update payment status in our system
            paymentService.updatePaymentStatus(paymentIntentId, Payment.PaymentStatus.CANCELLED, null);

            PaymentResponse response = new PaymentResponse(
                    paymentIntent.getId(),
                    paymentIntent.getStatus(),
                    "Payment cancelled successfully",
                    null
            );

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            log.error("Failed to cancel payment", e);
            PaymentResponse errorResponse = new PaymentResponse(
                    paymentIntentId,
                    "failed",
                    "Failed to cancel payment: " + e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/process-order-payment")
    public ResponseEntity<PaymentResponse> processOrderPayment(
            @Valid @RequestBody ProcessOrderPaymentRequest request) {
        try {
            Payment payment = paymentService.processPayment(
                request.getOrderId(),
                request.getAmount(),
                request.getPaymentIntentId()
            );

            PaymentResponse response = new PaymentResponse(
                    payment.getStripePaymentIntentId(),
                    payment.getStatus().toString().toLowerCase(),
                    "Order payment processed successfully",
                    null
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to process order payment", e);
            PaymentResponse errorResponse = new PaymentResponse(
                    request.getPaymentIntentId(),
                    "failed",
                    "Failed to process order payment: " + e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/refund/{paymentIntentId}")
    public ResponseEntity<PaymentResponse> refundPayment(
            @PathVariable String paymentIntentId,
            @RequestParam(required = false) Double amount) {
        try {
            Payment payment = paymentService.refundPayment(paymentIntentId, amount);
            
            if (payment == null) {
                return ResponseEntity.notFound().build();
            }

            PaymentResponse response = new PaymentResponse(
                    payment.getStripePaymentIntentId(),
                    payment.getStatus().toString().toLowerCase(),
                    "Refund processed successfully",
                    null
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to process refund", e);
            PaymentResponse errorResponse = new PaymentResponse(
                    paymentIntentId,
                    "failed",
                    "Failed to process refund: " + e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(errorResponse);
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
