package com.traveltrove.betraveltrove.presentation.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseModel {
    private String paymentId;
    private String sessionId;

    private String bookingId;

    private Long amount; // Payment amount in cents
    private String currency; // Currency code ("usd")
    private String status; // Payment status ("created", "succeeded", "failed")
}
