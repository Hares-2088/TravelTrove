package com.traveltrove.betraveltrove.presentation.payment;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.traveltrove.betraveltrove.business.booking.BookingService;
import com.traveltrove.betraveltrove.business.payment.PaymentService;
import com.traveltrove.betraveltrove.presentation.booking.BookingRequestModel;
import com.traveltrove.betraveltrove.utils.entitymodelyutils.PaymentModelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    @Value("${STRIPE_SECRET_KEY}")
    private String stripeSecretKey;

    @Value("${STRIPE_WEBHOOK_SECRET}")
    private String endpointSecret;

    private final PaymentService paymentService;
    private final BookingService bookingService;

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
        if (paymentRequest.getBookingId() == null || paymentRequest.getBookingId().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Booking ID is required."));
        }
        try {
            // Create Stripe Checkout Session parameters
            SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName("Package: " + paymentRequest.getPackageId())
                    .build();

            SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency(paymentRequest.getCurrency())
                    .setUnitAmount(paymentRequest.getAmount()) // Amount in cents (already calculated in frontend)
                    .setProductData(productData)
                    .build();

            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(1L) // Quantity is 1 because the total price is already calculated
                    .setPriceData(priceData)
                    .build();

            SessionCreateParams params = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(paymentRequest.getSuccessUrl() + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(paymentRequest.getCancelUrl())
                    .addLineItem(lineItem)
                    .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder().build())
                    .putMetadata("packageId", paymentRequest.getPackageId())
//                    .putMetadata("userId", "123") // Example: Add user ID
                    .putMetadata("bookingId", paymentRequest.getBookingId()) // Add booking ID
                    .build();

            // Create the Stripe session
            Session session = Session.create(params);

            // Call the service layer to save the payment details
            return paymentService.createPayment(paymentRequest, session.getId())
                    .map(payment -> PaymentModelUtil.toPaymentResponseModel(payment, session.getId()));

        } catch (StripeException e) {
            log.error("Stripe API error while creating a session: {}", e.getMessage());
            return Mono.error(new RuntimeException("Failed to create Stripe session: " + e.getMessage(), e));
        } catch (Exception e) {
            log.error("Unexpected error while creating Stripe checkout session: {}", e.getMessage());
            return Mono.error(new RuntimeException("An unexpected error occurred", e));
        }
    }

    @PostMapping("/webhook")
    public Mono<ResponseEntity<String>> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return Mono.just(ResponseEntity.badRequest().body("Invalid signature"));
        }
        log.info("Handling Stripe event: {}", event.getType());

        switch (event.getType()) {
            case "checkout.session.completed":
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                if (session != null) {
                    String bookingId = session.getMetadata().get("bookingId");
                    return bookingService.confirmBookingPayment(bookingId)
                            .thenReturn(ResponseEntity.ok("Booking confirmed!"));
                }
                break;
            case "payment_intent.succeeded":
                // Handle successful payment
                break;
            case "payment_intent.payment_failed":
                // Handle failed payment
                break;
            default:
                // Log unhandled event types
                log.info("Unhandled event type: {}", event.getType());
        }
        return Mono.just(ResponseEntity.ok("Event received but not handled"));
    }
}