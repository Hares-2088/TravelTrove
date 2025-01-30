package com.traveltrove.betraveltrove.utils.entitymodelyutils;

import com.traveltrove.betraveltrove.dataaccess.payment.Payment;
import com.traveltrove.betraveltrove.presentation.payment.PaymentRequestModel;
import com.traveltrove.betraveltrove.presentation.payment.PaymentResponseModel;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

public class PaymentModelUtil {

    public static PaymentResponseModel toPaymentResponseModel(Payment payment, String clientSecret) {
        PaymentResponseModel paymentResponseModel = new PaymentResponseModel();
        BeanUtils.copyProperties(payment, paymentResponseModel);
        paymentResponseModel.setStripePaymentId(payment.getStripePaymentId());
        paymentResponseModel.setPaymentId(payment.getPaymentId());
        // Set clientSecret from Stripe API response (not stored in entity)
        paymentResponseModel.setClientSecret(clientSecret);
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
