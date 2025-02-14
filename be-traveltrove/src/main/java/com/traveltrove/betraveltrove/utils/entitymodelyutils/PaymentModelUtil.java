package com.traveltrove.betraveltrove.utils.entitymodelyutils;

import com.traveltrove.betraveltrove.dataaccess.payment.Payment;
import com.traveltrove.betraveltrove.presentation.payment.PaymentRequestModel;
import com.traveltrove.betraveltrove.presentation.payment.PaymentResponseModel;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

public class PaymentModelUtil {

    public static PaymentResponseModel toPaymentResponseModel(Payment payment, String sessionId) {
        PaymentResponseModel paymentResponseModel = new PaymentResponseModel();
        BeanUtils.copyProperties(payment, paymentResponseModel);
        paymentResponseModel.setPaymentId(payment.getPaymentId());
        paymentResponseModel.setSessionId(sessionId);
        return paymentResponseModel;
    }

    public static Payment toPaymentEntity(String stripePaymentId, PaymentRequestModel request) {
        return Payment.builder()
                .paymentId(generateUUIDString())
                .stripePaymentId(stripePaymentId)
                .amount(request.getAmount() / 100)
                .currency(request.getCurrency())
                .status("created")
                .paymentDate(java.time.LocalDateTime.now())
                .build();
    }

    public static PaymentResponseModel toPaymentResponseModelWithoutSession(Payment payment) {
        PaymentResponseModel paymentResponseModel = new PaymentResponseModel();
        BeanUtils.copyProperties(payment, paymentResponseModel);
        paymentResponseModel.setPaymentId(payment.getPaymentId());
        paymentResponseModel.setAmount(payment.getAmount() / 100); // Scale amount back

        return paymentResponseModel;


    }

    private static String generateUUIDString() {
        return java.util.UUID.randomUUID().toString();
    }
}
