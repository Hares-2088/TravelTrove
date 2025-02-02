package com.traveltrove.betraveltrove.business.payment;

import com.traveltrove.betraveltrove.business.notification.NotificationService;
import com.traveltrove.betraveltrove.business.user.UserService;
import com.traveltrove.betraveltrove.dataaccess.notification.NotificationRepository;
import com.traveltrove.betraveltrove.dataaccess.payment.Payment;
import com.traveltrove.betraveltrove.dataaccess.payment.PaymentRepository;
import com.traveltrove.betraveltrove.dataaccess.user.User;
import com.traveltrove.betraveltrove.presentation.payment.PaymentRequestModel;
import com.traveltrove.betraveltrove.presentation.payment.PaymentResponseModel;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceUnitTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;



    @Test
    void createPayment_createsPaymentSuccessfully() {
        PaymentRequestModel requestModel = new PaymentRequestModel();
        Payment payment = new Payment();
        when(paymentRepository.save(any(Payment.class))).thenReturn(Mono.just(payment));

        Mono<Payment> result = paymentService.createPayment(requestModel, "stripePaymentId");

        StepVerifier.create(result)
                .expectNext(payment)
                .verifyComplete();
    }
    @Test
    void getPaymentByPaymentId_returnsPaymentSuccessfully() {
        Payment payment = Payment.builder()
                .paymentId("payment123")
                .stripePaymentId("stripePaymentId")
                .amount(5000L)
                .currency("usd")
                .status("created")
                .build();
        PaymentResponseModel paymentResponseModel = PaymentResponseModel.builder()
                .paymentId("payment123")
                .amount(5000L)
                .currency("usd")
                .status("created")
                .build();

        when(paymentRepository.findByPaymentId("payment123")).thenReturn(Mono.just(paymentResponseModel));

        Mono<PaymentResponseModel> result = paymentService.getPaymentByPaymentId("payment123");

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getPaymentId().equals("payment123") &&
                                response.getAmount() == 5000L &&
                                response.getCurrency().equals("usd") &&
                                response.getStatus().equals("created")
                )
                .verifyComplete();
    }
    @Test
    void getPaymentByPaymentId_withInvalidId_returnsNotFoundError() {
        when(paymentRepository.findByPaymentId(anyString())).thenReturn(Mono.empty());

        Mono<PaymentResponseModel> result = paymentService.getPaymentByPaymentId("invalid");

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Payment not found"))
                .verify();
    }

    @Test
    void getAllPayments_returnsAllPaymentsSuccessfully() {
        Payment payment1 = Payment.builder()
                .paymentId("payment1")
                .stripePaymentId("stripe1")
                .amount(5000L)
                .currency("usd")
                .status("created")
                .build();

        Payment payment2 = Payment.builder()
                .paymentId("payment2")
                .stripePaymentId("stripe2")
                .amount(10000L)
                .currency("usd")
                .status("created")
                .build();

        when(paymentRepository.findAll()).thenReturn(Flux.just(payment1, payment2));

        Flux<PaymentResponseModel> result = paymentService.getAllPayments();

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getPaymentId().equals("payment1"))
                .expectNextMatches(response -> response.getPaymentId().equals("payment2"))
                .verifyComplete();
    }

    @Test
    void updatePayment_returnsUpdatedPaymentSuccessfully() {
        Payment existingPayment = Payment.builder()
                .paymentId("payment123")
                .stripePaymentId("stripePaymentId")
                .amount(5000L)
                .currency("usd")
                .status("created")
                .build();

        when(paymentRepository.findById("payment123")).thenReturn(Mono.just(existingPayment));

        existingPayment.setStatus("succeeded");
        when(paymentRepository.save(existingPayment)).thenReturn(Mono.just(existingPayment));

        Mono<PaymentResponseModel> result = paymentService.updatePayment("payment123", "succeeded");

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getPaymentId().equals("payment123") &&
                                response.getStatus().equals("succeeded")
                )
                .verifyComplete();
    }

    @Test
    void updatePayment_withInvalidPaymentId_returnsEmpty() {
        when(paymentRepository.findById("invalid")).thenReturn(Mono.empty());

        Mono<PaymentResponseModel> result = paymentService.updatePayment("invalid", "succeeded");

        StepVerifier.create(result)
                .verifyComplete();
    }




}
