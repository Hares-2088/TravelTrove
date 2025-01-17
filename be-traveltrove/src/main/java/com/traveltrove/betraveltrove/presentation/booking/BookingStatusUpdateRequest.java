package com.traveltrove.betraveltrove.presentation.booking;

import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BookingStatusUpdateRequest {
    private BookingStatus status;
}
