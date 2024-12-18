package com.traveltrove.betraveltrove.presentation.booking;

import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseModel {

    private String bookingId; //public id
    private String userId;
    private String packageId;

    private Double totalPrice;
    private BookingStatus status;

    private LocalDate bookingDate;
}
