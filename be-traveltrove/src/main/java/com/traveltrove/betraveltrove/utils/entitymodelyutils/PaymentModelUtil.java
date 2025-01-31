package com.traveltrove.betraveltrove.utils.entitymodelyutils;

import com.traveltrove.betraveltrove.dataaccess.payment.Payment;
import com.traveltrove.betraveltrove.presentation.payment.PaymentRequestModel;
import com.traveltrove.betraveltrove.presentation.payment.PaymentResponseModel;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

public class PaymentModelUtil {

    public static PaymentResponseModel toPaymentResponseModel(Payment payment, String sessionId) {
        PaymentResponseModel paymentResponseModel = new PaymentResponseModel();
        BeanUtils.copyProperties(payment, paymentResponseModel); // Copy common properties
//        paymentResponseModel.setStripePaymentId(payment.getStripePaymentId()); // Set Stripe Payment ID
        paymentResponseModel.setPaymentId(payment.getPaymentId()); // Set internal Payment ID
        paymentResponseModel.setSessionId(sessionId); // Set Stripe Checkout Session ID
        return paymentResponseModel;
    }

    public static Payment toPaymentEntity(String stripePaymentId, PaymentRequestModel request) {
        return Payment.builder()
                .paymentId(generateUUIDString())
                .stripePaymentId(stripePaymentId)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status("created")  // Initial status
                .paymentDate(LocalDateTime.now())
                .build();
    }

    private static String generateUUIDString() {
        return java.util.UUID.randomUUID().toString();
    }
}
