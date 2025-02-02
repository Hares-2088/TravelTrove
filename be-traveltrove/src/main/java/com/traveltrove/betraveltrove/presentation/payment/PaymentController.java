package com.traveltrove.betraveltrove.presentation.payment;

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
import com.traveltrove.betraveltrove.externalservices.stripe.StripeService;
import com.traveltrove.betraveltrove.presentation.booking.BookingRequestModel;
import com.traveltrove.betraveltrove.utils.entitymodelyutils.PaymentModelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {


    private final PaymentService paymentService;
    private final StripeService stripeService;

    @PostMapping("/create-checkout-session")
    public Mono<PaymentResponseModel> createCheckoutSession(@RequestBody PaymentRequestModel paymentRequest) {
        return stripeService.createCheckoutSession(paymentRequest);
    }

    @PostMapping("/webhook")
    public Mono<ResponseEntity<String>> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        return stripeService.processWebhook(payload, sigHeader);
    }

    @GetMapping()
    public Flux<PaymentResponseModel> getAllPayments() {
        log.info("Getting all payments");
        return paymentService.getAllPayments();
    }
    @GetMapping("/{paymentId}")
    public Mono<ResponseEntity<PaymentResponseModel>> getPaymentByPaymentId(@PathVariable String paymentId) {
        log.info("Getting payment by paymentId: {}", paymentId);
        return paymentService.getPaymentByPaymentId(paymentId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

//    @DeleteMapping("/{paymentId}")
//    public Mono<ResponseEntity<Void>> deletePayment(@PathVariable String paymentId) {
//        log.info("Deleting payment by paymentId: {}", paymentId);
//        return paymentService.deletePayment(paymentId)
//                .map(ResponseEntity::ok)
//                .defaultIfEmpty(ResponseEntity.notFound().build());
//    }
}