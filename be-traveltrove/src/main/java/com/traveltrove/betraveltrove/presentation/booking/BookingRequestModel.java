package com.traveltrove.betraveltrove.presentation.booking;

import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import com.traveltrove.betraveltrove.presentation.traveler.TravelerRequestModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestModel {

    private String userId;
    private String packageId;

    private Double totalPrice;
    private BookingStatus status;

    private LocalDate bookingDate;

    private List<TravelerRequestModel> travelers;
}
