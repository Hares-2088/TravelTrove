package com.traveltrove.betraveltrove.externalservices.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class StripeService {
    @Value("${STRIPE_SECRET_KEY}")
    private String secretKey;

    public StripeService(@Value("${STRIPE_SECRET_KEY}") String secretKey) {
        Stripe.apiKey = secretKey;
    }
    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("Stripe secret key is not configured.");
        }
        Stripe.apiKey = secretKey;
        log.info("Stripe SDK initialized with secret key.");
    }
    public Mono<String> createPaymentIntent(Long amount, String currency) {
        return Mono.fromCallable(() -> {
            try {
                log.info("Creating PaymentIntent for amount: {} {}", amount, currency);
                PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                        .setAmount(amount * 100) // Convert dollars to cents
                        .setCurrency(currency)
                        .build();

                PaymentIntent intent = PaymentIntent.create(params);
                log.info("PaymentIntent created successfully: {}", intent.getId());
                return intent.getClientSecret(); // Return client secret for frontend
            } catch (StripeException e) {
                log.error("Failed to create PaymentIntent", e);
                throw new RuntimeException("Stripe Payment Failed", e);
            }
        }).onErrorResume(e -> {
            log.error("Error in StripeService.createPaymentIntent", e);
            return Mono.error(new RuntimeException("Failed to process payment", e));
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
