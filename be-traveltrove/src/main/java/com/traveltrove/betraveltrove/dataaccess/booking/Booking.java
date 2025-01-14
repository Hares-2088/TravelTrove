package com.traveltrove.betraveltrove.dataaccess.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "booking")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    private String id; //private id

    private String bookingId; //public id
    private String userId;
    private String packageId;

    private Double totalPrice;
    private BookingStatus status;

    private LocalDate bookingDate;
}
