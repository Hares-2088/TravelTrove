package com.traveltrove.betraveltrove.presentation.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseModel {
    private String paymentId;
    private String clientSecret;
    private String stripePaymentId;
}
