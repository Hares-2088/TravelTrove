package com.traveltrove.betraveltrove.dataaccess.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.UUID;

@DataMongoTest
@ActiveProfiles("test")
class BookingRepositoryIntegrationTest {

    @Autowired
    private BookingRepository bookingRepository;

    private final String bookingId1 = UUID.randomUUID().toString();
    private final String bookingId2 = UUID.randomUUID().toString();

    @BeforeEach
    void setup() {
        Booking booking1 = Booking.builder()
                .bookingId(bookingId1)
                .packageId("1")
                .userId("1")
                .totalPrice(1000.0)
                .bookingDate(LocalDate.of(2021, 1, 1))
                .status(BookingStatus.PAYMENT_PENDING)
                .build();

        Booking booking2 = Booking.builder()
                .bookingId(bookingId2)
                .packageId("2")
                .userId("1")
                .totalPrice(2000.0)
                .bookingDate(LocalDate.of(2022, 2, 2))
                .status(BookingStatus.BOOKING_CONFIRMED)
                .build();

        StepVerifier.create(bookingRepository.save(booking1))
                .expectNextMatches(savedBooking -> savedBooking.getBookingId().equals(bookingId1))
                .verifyComplete();

        StepVerifier.create(bookingRepository.save(booking2))
                .expectNextMatches(savedBooking -> savedBooking.getBookingId().equals(bookingId2))
                .verifyComplete();
    }

    @AfterEach
    void cleanup() {
        StepVerifier.create(bookingRepository.deleteAll())
                .verifyComplete();
    }

    @Test
    void whenFindBookingByBookingId_withExistingId_thenReturnExistingBooking() {
        StepVerifier.create(bookingRepository.findBookingByBookingId(bookingId1))
                .expectNextMatches(booking ->
                        booking.getBookingId().equals(bookingId1) &&
                                booking.getPackageId().equals("1") &&
                                booking.getUserId().equals("1") &&
                                booking.getTotalPrice().equals(1000.0) &&
                                booking.getBookingDate().equals(LocalDate.of(2021, 1, 1)) &&
                                booking.getStatus().equals(BookingStatus.PAYMENT_PENDING)
                )
                .verifyComplete();
    }

    @Test
    void whenFindBookingByBookingId_withNonExistingId_thenReturnEmptyMono() {
        StepVerifier.create(bookingRepository.findBookingByBookingId("INVALID_ID"))
                .verifyComplete();
    }

    @Test
    void whenFindBookingsByPackageId_withExistingId_thenReturnBookings() {
        StepVerifier.create(bookingRepository.findBookingsByPackageId("1"))
                .expectNextMatches(booking ->
                        booking.getPackageId().equals("1") &&
                                booking.getBookingId().equals(bookingId1))
                .verifyComplete();
    }

    @Test
    void whenFindBookingsByPackageId_withNonExistingId_thenReturnEmptyFlux() {
        StepVerifier.create(bookingRepository.findBookingsByPackageId("INVALID_PACKAGE_ID"))
                .verifyComplete();
    }

    @Test
    void whenFindBookingsByUserId_withExistingUserId_thenReturnBookings() {
        StepVerifier.create(bookingRepository.findBookingsByUserId("1"))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void whenFindBookingsByUserId_withNonExistingUserId_thenReturnEmptyFlux() {
        StepVerifier.create(bookingRepository.findBookingsByUserId("INVALID_USER_ID"))
                .verifyComplete();
    }

    @Test
    void whenFindBookingsByStatus_withExistingStatus_thenReturnBookings() {
        StepVerifier.create(bookingRepository.findBookingsByStatus(BookingStatus.PAYMENT_PENDING))
                .expectNextMatches(booking -> booking.getStatus().equals(BookingStatus.PAYMENT_PENDING))
                .verifyComplete();
    }

    @Test
    void whenFindBookingsByStatus_withNonExistingStatus_thenReturnEmptyFlux() {
        StepVerifier.create(bookingRepository.findBookingsByStatus(BookingStatus.REFUNDED))
                .verifyComplete();
    }

    @Test
    void whenFindBookingByPackageIdAndUserId_withExistingData_thenReturnBooking() {
        StepVerifier.create(bookingRepository.findBookingByPackageIdAndUserId("2", "1"))
                .expectNextMatches(booking ->
                        booking.getPackageId().equals("2") &&
                                booking.getUserId().equals("1"))
                .verifyComplete();
    }

    @Test
    void whenFindBookingByPackageIdAndUserId_withNonExistingData_thenReturnEmptyMono() {
        StepVerifier.create(bookingRepository.findBookingByPackageIdAndUserId("INVALID_PACKAGE_ID", "INVALID_USER_ID"))
                .verifyComplete();
    }
}
