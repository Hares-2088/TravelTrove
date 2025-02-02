package com.traveltrove.betraveltrove.presentation.payment;

import com.traveltrove.betraveltrove.presentation.traveler.TravelerRequestModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestModel {
    private Long amount; // Amount in cents
    private String currency; // Currency code (e.g., "usd")
    private String packageId; // ID of the package being purchased
    private String successUrl; // Frontend success URL
    private String cancelUrl; // Frontend cancel URL
    private String bookingId;
//private String bookingRequest; // A JSON string containing the booking details

    private List<TravelerRequestModel> travelers; // List of travelers for the booking

}
