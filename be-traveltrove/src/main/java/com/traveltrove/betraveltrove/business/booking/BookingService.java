package com.traveltrove.betraveltrove.business.booking;

import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import com.traveltrove.betraveltrove.presentation.booking.BookingRequestModel;
import com.traveltrove.betraveltrove.presentation.booking.BookingResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingService {

    // Retrieve bookings based on different criteria
    Flux<BookingResponseModel> getBookings();
    Flux<BookingResponseModel> getBookingsByUserId(String userId);
    Flux<BookingResponseModel> getBookingsByPackageId(String packageId);
    Flux<BookingResponseModel> getBookingsByStatus(BookingStatus status);

    // Retrieve specific booking details
    Mono<BookingResponseModel> getBooking(String bookingId);
    Mono<BookingResponseModel> getBookingByPackageIdAndUserId(String packageId, String userId);

    // Create a new booking
    Mono<BookingResponseModel> createBooking(BookingRequestModel bookingRequestModel);

    // Generalized method for updating booking status
    Mono<BookingResponseModel> updateBookingStatus(String bookingId, BookingStatus newStatus);

    // Delete a booking
    Mono<BookingResponseModel> deleteBooking(String bookingId);
}
