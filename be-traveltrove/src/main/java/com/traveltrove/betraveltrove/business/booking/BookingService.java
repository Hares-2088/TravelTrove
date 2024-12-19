package com.traveltrove.betraveltrove.business.booking;

import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import com.traveltrove.betraveltrove.presentation.booking.BookingRequestModel;
import com.traveltrove.betraveltrove.presentation.booking.BookingResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingService {

    Flux<BookingResponseModel> getBookings();
    Flux<BookingResponseModel> getBookingsByUserId(String userId);
    Flux<BookingResponseModel> getBookingsByPackageId(String packageId);
    Flux<BookingResponseModel> getBookingsByStatus(BookingStatus status);

    Mono<BookingResponseModel> getBooking(String bookingId);
    Mono<BookingResponseModel> getBookingByPackageIdAndUserId(String packageId, String userId);

    Mono<BookingResponseModel> createBooking(BookingRequestModel bookingRequestModel);

    // a method for each status transition
    Mono<BookingResponseModel> confirmBooking(String bookingId);
    Mono<BookingResponseModel> paymentPending(String bookingId);
    Mono<BookingResponseModel> paymentTentative2(String bookingId);
    Mono<BookingResponseModel> paymentTentative3(String bookingId);
    Mono<BookingResponseModel> bookingFailed(String bookingId);
    Mono<BookingResponseModel> paymentSuccess(String bookingId);
    Mono<BookingResponseModel> finalizeBooking(String bookingId);
    Mono<BookingResponseModel> expireBooking(String bookingId);
    Mono<BookingResponseModel> refundBooking(String bookingId);
    Mono<BookingResponseModel> cancelBooking(String bookingId);


    Mono<BookingResponseModel> deleteBooking(String bookingId);
}
