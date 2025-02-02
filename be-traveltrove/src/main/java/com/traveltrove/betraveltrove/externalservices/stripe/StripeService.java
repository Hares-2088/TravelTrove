package com.traveltrove.betraveltrove.externalservices.stripe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.traveltrove.betraveltrove.business.booking.BookingService;
import com.traveltrove.betraveltrove.business.payment.PaymentService;
import com.traveltrove.betraveltrove.presentation.payment.PaymentRequestModel;
import com.traveltrove.betraveltrove.presentation.payment.PaymentResponseModel;
import com.traveltrove.betraveltrove.utils.entitymodelyutils.PaymentModelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Component
public class StripeService {
    @Value("${STRIPE_SECRET_KEY}")
    private String stripeSecretKey;

    @Value("${STRIPE_WEBHOOK_SECRET}")
    private String endpointSecret;

    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final ObjectMapper objectMapper;

    public StripeService(PaymentService paymentService, BookingService bookingService, ObjectMapper objectMapper) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
        this.objectMapper = objectMapper;
    }

    public Mono<PaymentResponseModel> createCheckoutSession(PaymentRequestModel paymentRequest) {
        Stripe.apiKey = stripeSecretKey;
        try {
            // Build product data
            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName("Package: " + paymentRequest.getPackageId())
                            .build();

            // Build price data (amount is in cents)
            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(paymentRequest.getCurrency())
                            .setUnitAmount(paymentRequest.getAmount())
                            .setProductData(productData)
                            .build();

            // Build line item
            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(priceData)
                            .build();

            // Build the checkout session parameters
            SessionCreateParams params = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(paymentRequest.getSuccessUrl() + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(paymentRequest.getCancelUrl())
                    .addLineItem(lineItem)
                    .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder().build())
                    .putMetadata("packageId", paymentRequest.getPackageId())
                    .putMetadata("bookingId", paymentRequest.getBookingId())
                    .build();

            // Create the Stripe session
            Session session = Session.create(params);

            // Save the payment details to the database
            return paymentService.createPayment(paymentRequest, session.getId())
                    .map(payment -> PaymentModelUtil.toPaymentResponseModel(payment, session.getId()));

        } catch (StripeException e) {
            log.error("Stripe API error: {}", e.getMessage());
            return Mono.error(new RuntimeException("Failed to create Stripe session: " + e.getMessage(), e));
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return Mono.error(new RuntimeException("An unexpected error occurred", e));
        }
    }

    //Looks weird, but it's a Stripe webhook and it works, don't touch it :))
    public Mono<ResponseEntity<String>> processWebhook(String payload, String sigHeader) {
        Event event;
        try {
            // Verify webhook signature
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            log.info("Verified webhook event: {} of type: {}", event.getId(), event.getType());
        } catch (SignatureVerificationException e) {
            log.error("Invalid signature: {}", e.getMessage());
            return Mono.just(ResponseEntity.badRequest().body("Invalid signature"));
        }

        switch (event.getType()) {
            case "checkout.session.completed":
                Optional<StripeObject> sessionOpt = event.getDataObjectDeserializer().getObject();
                if (sessionOpt.isPresent() && sessionOpt.get() instanceof Session) {
                    Session session = (Session) sessionOpt.get();
                    String bookingId = session.getMetadata().get("bookingId");
                    log.info("Checkout session completed for bookingId: {}", bookingId);
                    return bookingService.confirmBookingPayment(bookingId)
                            .doOnSuccess(result -> log.info("Booking confirmed for bookingId: {}", bookingId))
                            .doOnError(error -> log.error("Error confirming booking for bookingId: {}. Error: {}",
                                    bookingId, error.getMessage()))
                            .thenReturn(ResponseEntity.ok("Booking confirmed!"));
                } else {
                    log.warn("Could not deserialize session object, attempting manual parsing.");
                    try {
                        JsonNode rootNode = objectMapper.readTree(payload);
                        JsonNode bookingIdNode = rootNode.path("data").path("object").path("metadata").path("bookingId");
                        String bookingId = bookingIdNode.asText(null);
                        if (bookingId == null || bookingId.isEmpty()) {
                            log.error("Missing booking ID in webhook payload");
                            return Mono.just(ResponseEntity.badRequest().body("Missing booking ID"));
                        }
                        log.info("Extracted bookingId: {}", bookingId);
                        return bookingService.confirmBookingPayment(bookingId)
                                .doOnSuccess(result -> log.info("Booking confirmed for bookingId: {}", bookingId))
                                .doOnError(error -> log.error("Error confirming booking: {}", error.getMessage()))
                                .thenReturn(ResponseEntity.ok("Booking confirmed!"));
                    } catch (Exception ex) {
                        log.error("Error parsing webhook payload: {}", ex.getMessage(), ex);
                        return Mono.just(ResponseEntity.internalServerError().body("Error processing webhook payload"));
                    }
                }
            case "payment_intent.succeeded":
                log.info("Payment succeeded event received: {}", event.getId());

                break;
            case "payment_intent.payment_failed":
                log.info("Payment failed event received: {}", event.getId());
                break;
            default:
                log.info("Unhandled event type: {}", event.getType());
        }
        return Mono.just(ResponseEntity.ok("Event received but not handled"));
    }
}

