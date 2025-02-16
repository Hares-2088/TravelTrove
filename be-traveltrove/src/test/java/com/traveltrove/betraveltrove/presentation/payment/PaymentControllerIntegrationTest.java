package com.traveltrove.betraveltrove.presentation.payment;

import com.traveltrove.betraveltrove.business.booking.BookingService;
import com.traveltrove.betraveltrove.business.payment.PaymentService;
import com.traveltrove.betraveltrove.business.tour.TourService;
import com.traveltrove.betraveltrove.business.tourpackage.PackageService;
import com.traveltrove.betraveltrove.dataaccess.payment.Payment;
import com.traveltrove.betraveltrove.dataaccess.payment.PaymentRepository;
import com.traveltrove.betraveltrove.presentation.booking.BookingResponseModel;
import com.traveltrove.betraveltrove.presentation.tour.TourResponseModel;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageResponseModel;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;



import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockitoBean
    private PackageService packageService;

    @MockitoBean
    private TourService tourService;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private PaymentService paymentService;

    private final String INVALID_PAYMENT_ID = "invalid-payment-id";

    private final Payment payment1 = Payment.builder()
            .id("1")
            .paymentId("1")
            .amount(100L)
            .currency("USD")
            .status("PAID")
            .build();

    private final Payment payment2 = Payment.builder()
            .id("2")
            .paymentId("2")
            .amount(200L)
            .currency("USD")
            .status("PAID")
            .build();

    @BeforeEach
    public void setupDB() {
        paymentRepository.deleteAll()
                .thenMany(Flux.just(payment1, payment2))
                .flatMap(paymentRepository::save)
                .doOnNext(payment -> {
                    System.out.println("Payment inserted from PaymentControllerIntegrationTest: " + payment);
                })
                .blockLast();

        StepVerifier.create(paymentRepository.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void whenCreateCheckoutSession_thenReturnCreatedPayment() {
        PaymentRequestModel newPayment = PaymentRequestModel.builder()
                .amount(5000L) // amount in cents
                .currency("usd")
                .packageId("package_123")
                .bookingId("booking_123")
                .successUrl("http://example.com/success")
                .cancelUrl("http://example.com/cancel")
                .build();

        // Call the create-checkout-session endpoint with security mocks.
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .mutateWith(SecurityMockServerConfigurers.mockUser("TestUser"))
                .post()
                .uri("/api/v1/payments/create-checkout-session")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newPayment)
                .exchange()
                .expectStatus().isOk()  // expecting 200 OK now
                .expectBody(PaymentResponseModel.class)
                .value(paymentResponse -> {
                    // Validate that a PaymentResponseModel is returned with proper fields.
                    assertNotNull(paymentResponse.getPaymentId());
                    assertNotNull(paymentResponse.getSessionId());
                    assertEquals("created", paymentResponse.getStatus());
                    assertEquals(newPayment.getCurrency(), paymentResponse.getCurrency());
                });

        // Validate that the Payment was saved in the repository.
        StepVerifier.create(paymentRepository.findAll())
                .expectNextCount(3)
                .verifyComplete();
    }



    @Test
    public void whenGetAllPayments_thenReturnAllPayments() {
        // Call the GET endpoint for all payments.
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .mutateWith(SecurityMockServerConfigurers.mockUser("TestUser"))
                .get()
                .uri("/api/v1/payments")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PaymentResponseModel.class)
                .hasSize(2);  // Expecting the two payments pre-inserted in setupDB.
    }

    @Test
    public void whenGetPaymentByPaymentId_thenReturnPayment() {
        // Call the GET endpoint for a specific payment using a valid paymentId.
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .mutateWith(SecurityMockServerConfigurers.mockUser("TestUser"))
                .get()
                .uri("/api/v1/payments/{paymentId}", payment1.getPaymentId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaymentResponseModel.class)
                .value(response -> {
                    assertEquals(payment1.getPaymentId(), response.getPaymentId());
                    assertEquals(payment1.getCurrency(), response.getCurrency());
                    assertEquals(payment1.getAmount(), response.getAmount());
                });
    }
    @Test
    void whenGetAllPayments_whenNoPayments_thenThrowNotFoundException() {
        // Delete all payments from the repository.
        paymentRepository.deleteAll().block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .mutateWith(SecurityMockServerConfigurers.mockUser("TestUser"))
                .get()
                .uri("/api/v1/payments")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("No payments found");
    }
    @Test
    public void whenGetPaymentByPaymentId_withInvalidId_thenReturnNotFound() {
        // Call the GET endpoint with an invalid paymentId.
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .mutateWith(SecurityMockServerConfigurers.mockUser("TestUser"))
                .get()
                .uri("/api/v1/payments/{paymentId}", INVALID_PAYMENT_ID)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void whenWebhookWithInvalidSignature_thenReturnBadRequest() {
        // Build a dummy payload to simulate a webhook call.
        String payload = "{\"id\":\"evt_test\",\"type\":\"checkout.session.completed\"}";

        webTestClient
                .post()
                .uri("/api/v1/payments/webhook")
                .header("Stripe-Signature", "invalid_signature")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getPaymentByBookingId_withExistingId_returnsPaymentSuccessfully() {
        PaymentResponseModel paymentResponseModel = PaymentResponseModel.builder()
                .paymentId("payment123")
                .bookingId("booking123")
                .amount(5000L)
                .currency("usd")
                .status("created")
                .build();

        when(paymentService.getPaymentByBookingId("booking123")).thenReturn(Mono.just(paymentResponseModel));

        webTestClient.get()
                .uri("/api/v1/payments/booking/booking123")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaymentResponseModel.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals("payment123", response.getPaymentId());
                    assertEquals("booking123", response.getBookingId());
                    assertEquals(5000L, response.getAmount());
                    assertEquals("usd", response.getCurrency());
                    assertEquals("created", response.getStatus());
                });
    }

    @Test
    void getPaymentByBookingId_withNonExistingId_returnsNotFoundError() {
        when(paymentService.getPaymentByBookingId(anyString())).thenReturn(Mono.error(new NotFoundException("Payment not found")));

        Mono<PaymentResponseModel> result = paymentService.getPaymentByBookingId("invalid");

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Payment not found"))
                .verify();
    }

//    @Test
//    void calculateRevenueByTourId_withExistingTourId_returnsTotalRevenue() {
//        TourResponseModel tourResponseModel = new TourResponseModel();
//        tourResponseModel.setTourId("tour123");
//
//        PackageResponseModel packageResponseModel = new PackageResponseModel();
//        packageResponseModel.setPackageId("package123");
//
//        BookingResponseModel bookingResponseModel = new BookingResponseModel();
//        bookingResponseModel.setBookingId("booking123");
//
//        PaymentResponseModel paymentResponseModel = PaymentResponseModel.builder()
//                .paymentId("payment123")
//                .bookingId("booking123")
//                .amount(5000L)
//                .currency("usd")
//                .status("created")
//                .build();
//
//        when(tourService.getTourByTourId("tour123")).thenReturn(Mono.just(tourResponseModel));
//        when(packageService.getAllPackages("tour123")).thenReturn(Flux.just(packageResponseModel));
//        when(bookingService.getBookingsByPackageId("package123")).thenReturn(Flux.just(bookingResponseModel));
//        when(paymentRepository.findByBookingId("booking123")).thenReturn(Mono.just(paymentResponseModel));
//
//        Mono<Long> result = paymentService.calculateRevenueByTourId("tour123");
//
//        StepVerifier.create(result)
//                .expectNext(5000L)
//                .verifyComplete();
//    }

//    @Test
//    void calculateRevenueByTourId_withNonExistingTourId_returnsZero() {
//        when(tourService.getTourByTourId("invalid")).thenReturn(Mono.empty());
//
//        Mono<Long> result = paymentService.calculateRevenueByTourId("invalid");
//
//        StepVerifier.create(result)
//                .expectNext(0L)
//                .verifyComplete();
//    }
}


