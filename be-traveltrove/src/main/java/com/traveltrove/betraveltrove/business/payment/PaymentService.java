package com.traveltrove.betraveltrove.business.payment;

import com.traveltrove.betraveltrove.dataaccess.payment.Payment;
import com.traveltrove.betraveltrove.presentation.payment.PaymentRequestModel;
import com.traveltrove.betraveltrove.presentation.payment.PaymentResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PaymentService {
    Mono<Payment> createPayment(PaymentRequestModel paymentRequestModel, String stripePayementId);
    Mono<PaymentResponseModel> updatePayment(String paymentId, String status);
//    Mono<Void> deletePayment(String paymentId);
    Mono<PaymentResponseModel> getPaymentByPaymentId(String paymentId);
    Flux<PaymentResponseModel> getAllPayments();
    Flux<Payment> getPaymentsByPeriod(int year, Integer month);
}
