package com.fooddelivery.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "stripe")
@Data
public class StripeConfig {
    private String publicKey = "pk_test_PLACEHOLDER_PUBLIC_KEY";
    private String secretKey = "sk_test_PLACEHOLDER_SECRET_KEY";
    private String webhookSecret = "whsec_PLACEHOLDER_WEBHOOK_SECRET";
    private String currency = "usd";
}
