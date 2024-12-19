package com.traveltrove.betraveltrove.utils.entitymodelyutils;

import com.traveltrove.betraveltrove.dataaccess.booking.Booking;
import com.traveltrove.betraveltrove.presentation.booking.BookingRequestModel;
import com.traveltrove.betraveltrove.presentation.booking.BookingResponseModel;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

public class BookingEntityModelUtil {

    //Method to convert a Booking entity to a BookingResponseModel
    public static BookingResponseModel toBookingResponseModel(Booking booking) {
        BookingResponseModel bookingResponseModel = new BookingResponseModel();
        BeanUtils.copyProperties(booking, bookingResponseModel);

        if (booking.getBookingId() != null) {
            bookingResponseModel.setBookingId(booking.getBookingId());
        }
        if (booking.getPackageId() != null) {
            bookingResponseModel.setPackageId(booking.getPackageId());
        }
        if (booking.getTotalPrice() != null) {
            bookingResponseModel.setTotalPrice(booking.getTotalPrice());
        }
        if (booking.getStatus() != null) {
            bookingResponseModel.setStatus(booking.getStatus());
        }
        if (booking.getBookingDate() != null) {
            bookingResponseModel.setBookingDate(booking.getBookingDate());
        }

        return bookingResponseModel;
    }

    //Method to map a BookingRequestModel to a Booking entity
    public static Booking toBookingEntity(BookingRequestModel bookingRequestModel) {
        return Booking.builder()
                .bookingId(generateUUIDString()) // Generate a unique bookingId
                .packageId(bookingRequestModel.getPackageId())
                .totalPrice(bookingRequestModel.getTotalPrice())
                .status(bookingRequestModel.getStatus())
                .bookingDate(bookingRequestModel.getBookingDate())
                .build();
    }


    // Utility method to generate a UUID string
    private static String generateUUIDString() {
        return java.util.UUID.randomUUID().toString();
    }


}
