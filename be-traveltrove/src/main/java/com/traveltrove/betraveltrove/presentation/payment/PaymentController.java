package com.traveltrove.betraveltrove.presentation.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.traveltrove.betraveltrove.business.payment.PaymentService;
import com.traveltrove.betraveltrove.utils.entitymodelyutils.PaymentModelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    @Value("${STRIPE_SECRET_KEY}")
    private String stripeSecretKey;

    private final PaymentService paymentService;

    @PostMapping("/create-checkout-session")
    public Mono<PaymentResponseModel> createCheckoutSession(@RequestBody PaymentRequestModel paymentRequest) {
        Stripe.apiKey = stripeSecretKey;

        // Validate the request
        if (paymentRequest.getAmount() == null || paymentRequest.getAmount() <= 0) {
            return Mono.error(new IllegalArgumentException("Amount must be a positive value."));
        }
        if (paymentRequest.getCurrency() == null || paymentRequest.getCurrency().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Currency is required."));
        }
        if (paymentRequest.getPackageId() == null || paymentRequest.getPackageId().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Package ID is required."));
        }
        if (paymentRequest.getSuccessUrl() == null || paymentRequest.getSuccessUrl().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Success URL is required."));
        }
        if (paymentRequest.getCancelUrl() == null || paymentRequest.getCancelUrl().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Cancel URL is required."));
        }

        try {
            // Log the incoming request
            log.info("Creating Stripe Checkout Session for package: {}", paymentRequest.getPackageId());
            log.info("Amount: {} {}", paymentRequest.getAmount(), paymentRequest.getCurrency());
            log.info("Success URL: {}", paymentRequest.getSuccessUrl());
            log.info("Cancel URL: {}", paymentRequest.getCancelUrl());

            // Create Stripe Checkout Session parameters
            SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName("Package: " + paymentRequest.getPackageId())
                    .build();

            SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency(paymentRequest.getCurrency())
                    .setUnitAmount(paymentRequest.getAmount()) // Amount in cents
                    .setProductData(productData)
                    .build();

            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(priceData)
                    .build();

            SessionCreateParams params = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(paymentRequest.getSuccessUrl() + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(paymentRequest.getCancelUrl())
                    .addLineItem(lineItem)
                    .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder()
                            .build())
                    .putMetadata("packageId", paymentRequest.getPackageId()) // Add metadata
                    .putMetadata("userId", "123") // Example: Add user ID
                    .build();

            // Create the Stripe session
            Session session = Session.create(params);

            // Log the entire session for debugging
            log.info("Stripe Session created successfully: {}", session);

            // Call the service layer to save the payment details
            return paymentService.createPayment(paymentRequest, session.getId()) // Pass sessionId instead of stripePaymentId
                    .map(payment -> {
                        log.info("Payment saved successfully: {}", payment);
                        return PaymentModelUtil.toPaymentResponseModel(payment, session.getId()); // Pass sessionId
                    });

        } catch (StripeException e) {
            log.error("Stripe API error while creating a session. Status: {}, Code: {}, Message: {}",
                    e.getStatusCode(), e.getCode(), e.getMessage());
            return Mono.error(new RuntimeException("Failed to create Stripe session: " + e.getMessage(), e));
        } catch (Exception e) {
            log.error("Unexpected error while creating Stripe checkout session: {}", e.getMessage(), e);
            return Mono.error(new RuntimeException("An unexpected error occurred", e));
        }
    }

}