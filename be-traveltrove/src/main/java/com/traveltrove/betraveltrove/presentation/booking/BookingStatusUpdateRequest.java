package com.traveltrove.betraveltrove.presentation.booking;

import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import lombok.Data;

@Data
public class BookingStatusUpdateRequest {
    private BookingStatus status;
}
