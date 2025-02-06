package com.traveltrove.betraveltrove.dataaccess.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;

    private String paymentId;
    private String bookingId;
    private Long amount;
    private String currency;
    private LocalDateTime paymentDate;
    private String status;
    private String stripePaymentId;



}
