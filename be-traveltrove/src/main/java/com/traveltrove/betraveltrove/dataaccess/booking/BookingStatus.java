package com.traveltrove.betraveltrove.dataaccess.booking;

public enum BookingStatus {
    PAYMENT_PENDING,
    PAYMENT_TENTATIVE2_PENDING,
    PAYMENT_TENTATIVE3_PENDING,
    BOOKING_FAILED,
    PAYMENT_SUCCESS,
    BOOKING_CONFIRMED,
    BOOKING_FINALIZED,
    BOOKING_EXPIRED,
    BOOKING_CANCELLED,
    REFUNDED
}
//For the related diagram go to State transition diagrams -> Booking Status Diagram