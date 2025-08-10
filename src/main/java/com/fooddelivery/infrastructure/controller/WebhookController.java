package com.fooddelivery.infrastructure.controller;

import com.fooddelivery.application.service.PaymentService;
import com.fooddelivery.domain.model.Payment;
import com.fooddelivery.infrastructure.config.StripeConfig;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final PaymentService paymentService;
    private final StripeConfig stripeConfig;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        
        Event event;
        
        try {
            // Verify webhook signature
            event = Webhook.constructEvent(payload, sigHeader, stripeConfig.getWebhookSecret());
        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe webhook signature", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        // Handle the event
        switch (event.getType()) {
            case "payment_intent.succeeded":
                handlePaymentIntentSucceeded(event);
                break;
            case "payment_intent.payment_failed":
                handlePaymentIntentFailed(event);
                break;
            case "payment_intent.canceled":
                handlePaymentIntentCanceled(event);
                break;
            case "payment_intent.processing":
                handlePaymentIntentProcessing(event);
                break;
            default:
                log.info("Unhandled event type: {}", event.getType());
        }

        return ResponseEntity.ok("Webhook handled successfully");
    }

    private void handlePaymentIntentSucceeded(Event event) {
        try {
            StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);
            if (stripeObject instanceof PaymentIntent paymentIntent) {
                log.info("Payment succeeded for payment intent: {}", paymentIntent.getId());
                
                paymentService.updatePaymentStatus(
                    paymentIntent.getId(),
                    Payment.PaymentStatus.COMPLETED,
                    null
                );
                
                // TODO: Update order status, send confirmation email, etc.
            }
        } catch (Exception e) {
            log.error("Error handling payment intent succeeded event", e);
        }
    }

    private void handlePaymentIntentFailed(Event event) {
        try {
            StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);
            if (stripeObject instanceof PaymentIntent paymentIntent) {
                log.info("Payment failed for payment intent: {}", paymentIntent.getId());
                
                String failureReason = paymentIntent.getLastPaymentError() != null ?
                    paymentIntent.getLastPaymentError().getMessage() : "Payment failed";
                
                paymentService.updatePaymentStatus(
                    paymentIntent.getId(),
                    Payment.PaymentStatus.FAILED,
                    failureReason
                );
                
                // TODO: Handle failed payment (cancel order, notify user, etc.)
            }
        } catch (Exception e) {
            log.error("Error handling payment intent failed event", e);
        }
    }

    private void handlePaymentIntentCanceled(Event event) {
        try {
            StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);
            if (stripeObject instanceof PaymentIntent paymentIntent) {
                log.info("Payment canceled for payment intent: {}", paymentIntent.getId());
                
                paymentService.updatePaymentStatus(
                    paymentIntent.getId(),
                    Payment.PaymentStatus.CANCELLED,
                    "Payment was canceled"
                );
                
                // TODO: Handle canceled payment (cancel order, etc.)
            }
        } catch (Exception e) {
            log.error("Error handling payment intent canceled event", e);
        }
    }

    private void handlePaymentIntentProcessing(Event event) {
        try {
            StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);
            if (stripeObject instanceof PaymentIntent paymentIntent) {
                log.info("Payment processing for payment intent: {}", paymentIntent.getId());
                
                paymentService.updatePaymentStatus(
                    paymentIntent.getId(),
                    Payment.PaymentStatus.PROCESSING,
                    null
                );
            }
        } catch (Exception e) {
            log.error("Error handling payment intent processing event", e);
        }
    }
}
