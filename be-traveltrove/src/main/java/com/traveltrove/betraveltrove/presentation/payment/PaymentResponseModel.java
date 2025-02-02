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
    private String sessionId;

    private Long amount; // Payment amount in cents
    private String currency; // Currency code (e.g., "usd")
    private String status; // Payment status (e.g., "created", "succeeded", "failed")
}
