package com.traveltrove.betraveltrove.dataaccess.payment;

import com.traveltrove.betraveltrove.presentation.payment.PaymentResponseModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@ActiveProfiles("test")
public class PaymentRepositoryIntegrationTest {
    @Autowired
    private PaymentRepository paymentRepository;

    private final String paymentId = "33cff3cf-de6c-4f66-8f52-82f7ebf8f6d6";
    private final String bookingId = "booking-123";

    @BeforeEach
    void setup() {
        Payment testPayment = Payment.builder()
                .id("1")
                .paymentId(paymentId)
                .bookingId(bookingId)
                .amount(1000L)
                .currency("USD")
                .status("created")
                .build();

        StepVerifier.create(paymentRepository.save(testPayment))
                .expectNextMatches(savedPayment -> savedPayment.getPaymentId().equals(paymentId))
                .verifyComplete();
    }
    @AfterEach
    void cleanup() {
        StepVerifier.create(paymentRepository.deleteAll())
                .verifyComplete();
    }

    @Test
    void whenFindPaymentByPaymentId_withExistingId_thenReturnExistingPayment() {
        Mono<PaymentResponseModel> foundPayment = paymentRepository.findByPaymentId(paymentId);

        StepVerifier.create(foundPayment)
                .expectNextMatches(payment ->
                        payment.getPaymentId().equals(paymentId) &&
                                payment.getAmount() == 1000L &&
                                payment.getCurrency().equals("USD") &&
                                payment.getStatus().equals("created")
                )
                .verifyComplete();
    }

    @Test
    void whenFindPaymentByPaymentId_withNonExistingId_thenReturnEmptyMono() {
        Mono<PaymentResponseModel> foundPayment = paymentRepository.findByPaymentId("INVALID_ID");

        StepVerifier.create(foundPayment)
                .verifyComplete();
    }

    @Test
    void whenFindPaymentByBookingId_withExistingId_thenReturnExistingPayment() {
        Mono<PaymentResponseModel> foundPayment = paymentRepository.findByBookingId(bookingId);

        StepVerifier.create(foundPayment)
                .expectNextMatches(payment ->
                        payment.getBookingId().equals(bookingId) &&
                                payment.getAmount() == 1000L &&
                                payment.getCurrency().equals("USD") &&
                                payment.getStatus().equals("created")
                )
                .verifyComplete();
    }

    @Test
    void whenFindPaymentByBookingId_withNonExistingId_thenReturnEmptyMono() {
        Mono<PaymentResponseModel> foundPayment = paymentRepository.findByBookingId("INVALID_ID");

        StepVerifier.create(foundPayment)
                .verifyComplete();
    }

}
