package com.traveltrove.betraveltrove.dataaccess.booking;

public enum BookingStatus {
    PAYMENT_PENDING,
    PAYMENT_ATTEMPT2_PENDING,
    BOOKING_FAILED,
    BOOKING_CONFIRMED,
    BOOKING_FINALIZED,
    REFUNDED
}
//For the related diagram go to State transition diagrams -> Booking Status Diagram