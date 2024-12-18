package com.traveltrove.betraveltrove.dataaccess.booking;

public enum BookingStatus {
    PAYMENT_PENDING,
    PAYMENT_TENTATIVE2_PENDING,
    PAYMENT_TENTATIVE3_PENDING,
    BOOKING_FAILED,
    PAYMENT_SUCCESS,
    BOOKING_CONFIRMED,
    BOOKING_FINALIZED,
    EXPIRED
}
//For the related diagram go to State transition diagrams -> Booking Status Diagram