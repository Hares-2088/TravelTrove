package com.traveltrove.betraveltrove.business.booking;

import com.traveltrove.betraveltrove.business.booking.BookingServiceImpl;
import com.traveltrove.betraveltrove.business.tourpackage.PackageService;
import com.traveltrove.betraveltrove.business.user.UserService;
import com.traveltrove.betraveltrove.dataaccess.booking.Booking;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingRepository;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import com.traveltrove.betraveltrove.externalservices.auth0.Auth0Service;
import com.traveltrove.betraveltrove.presentation.booking.BookingRequestModel;
import com.traveltrove.betraveltrove.presentation.booking.BookingResponseModel;
import com.traveltrove.betraveltrove.presentation.booking.BookingStatusUpdateRequest;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageResponseModel;
import com.traveltrove.betraveltrove.presentation.user.UserResponseModel;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import com.traveltrove.betraveltrove.utils.exceptions.SameStatusException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private Auth0Service auth0Service;

    @Mock
    private UserService userService;

    @Mock
    private PackageService packageService;

    @Test
    public void whenGetAllBookings_thenReturnAllBookings() {
        String bookingId1 = UUID.randomUUID().toString();
        String bookingId2 = UUID.randomUUID().toString();

        Booking booking1 = new Booking("1", bookingId1, "user1", "pack1", 1200.00, BookingStatus.BOOKING_CONFIRMED, LocalDate.of(2025, 4, 4));
        Booking booking2 = new Booking("2", bookingId2, "user2", "pack2", 1300.00, BookingStatus.PAYMENT_PENDING, LocalDate.of(2025, 5, 5));

        when(bookingRepository.findAll()).thenReturn(Flux.just(booking1, booking2));

        StepVerifier.create(bookingService.getBookings())
                .expectNextMatches(response -> response.getBookingId().equals(bookingId1) && response.getStatus() == BookingStatus.BOOKING_CONFIRMED)
                .expectNextMatches(response -> response.getBookingId().equals(bookingId2) && response.getStatus() == BookingStatus.PAYMENT_PENDING)
                .verifyComplete();
    }

    @Test
    public void whenGetBooking_withExistingId_thenReturnBooking() {
        String bookingId = UUID.randomUUID().toString();
        Booking booking = new Booking("1", bookingId, "user1", "pack1", 1200.00, BookingStatus.BOOKING_CONFIRMED, LocalDate.of(2025, 4, 4));

        when(bookingRepository.findBookingByBookingId(bookingId)).thenReturn(Mono.just(booking));

        StepVerifier.create(bookingService.getBooking(bookingId))
                .expectNextMatches(response -> response.getBookingId().equals(bookingId))
                .verifyComplete();
    }

    @Test
    public void whenGetBooking_withNonExistingId_thenReturnNotFound() {
        String bookingId = UUID.randomUUID().toString();

        when(bookingRepository.findBookingByBookingId(bookingId)).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.getBooking(bookingId))
                .expectErrorMatches(error -> error instanceof NotFoundException &&
                        error.getMessage().equals("Booking not found with ID: " + bookingId))
                .verify();
    }

    @Test
    public void whenCreateBooking_thenReturnSavedBooking() {
        String userId = "user1";
        String packageId = "pack1";

        BookingRequestModel requestModel = BookingRequestModel.builder()
                .userId(userId) // Ensure userId is set
                .packageId(packageId)
                .totalPrice(1200.00)
                .status(BookingStatus.PAYMENT_PENDING)
                .bookingDate(LocalDate.of(2025, 4, 4))
                .build();

        Booking booking = new Booking("1", UUID.randomUUID().toString(), userId, packageId, 1200.00, BookingStatus.PAYMENT_PENDING, LocalDate.of(2025, 4, 4));

        // Mock userService
        when(userService.syncUserWithAuth0(userId))
                .thenReturn(Mono.just(UserResponseModel.builder()
                        .userId(userId)
                        .build()));

        // Mock packageService
        when(packageService.getPackageByPackageId(packageId))
                .thenReturn(Mono.just(PackageResponseModel.builder()
                        .packageId(packageId)
                        .build()));

        // Mock bookingRepository
        when(bookingRepository.findBookingByPackageIdAndUserId(packageId, userId))
                .thenReturn(Mono.empty());
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(Mono.just(booking));

        // Verify
        StepVerifier.create(bookingService.createBooking(requestModel))
                .expectNextMatches(response ->
                        response.getUserId().equals(userId) &&
                                response.getPackageId().equals(packageId) &&
                                response.getTotalPrice() == 1200.00 &&
                                response.getStatus() == BookingStatus.PAYMENT_PENDING &&
                                response.getBookingDate().equals(LocalDate.of(2025, 4, 4))
                )
                .verifyComplete();
    }

    @Test
    public void whenUpdateBookingStatus_withExistingId_thenReturnUpdatedBooking() {
        String bookingId = UUID.randomUUID().toString();
        Booking booking = new Booking("1", bookingId, "user1", "pack1", 1200.00, BookingStatus.PAYMENT_PENDING, LocalDate.of(2025, 4, 4));
        Booking updatedBooking = new Booking("1", bookingId, "user1", "pack1", 1200.00, BookingStatus.BOOKING_CONFIRMED, LocalDate.of(2025, 4, 4));

        // Mock repository calls
        when(bookingRepository.findBookingByBookingId(bookingId)).thenReturn(Mono.just(booking));
        when(bookingRepository.save(booking)).thenReturn(Mono.just(updatedBooking));

        // Create a BookingStatusUpdateRequest
        BookingStatusUpdateRequest request = new BookingStatusUpdateRequest();
        request.setStatus(BookingStatus.BOOKING_CONFIRMED);

        // Execute the test
        StepVerifier.create(bookingService.updateBookingStatus(bookingId, request.getStatus()))
                .expectNextMatches(response -> response.getStatus() == BookingStatus.BOOKING_CONFIRMED)
                .verifyComplete();
    }

    @Test
    public void whenUpdateBookingStatus_withNonExistingId_thenReturnNotFound() {
        String bookingId = UUID.randomUUID().toString();

        // Mock repository to return Mono.empty()
        when(bookingRepository.findBookingByBookingId(bookingId)).thenReturn(Mono.empty());

        BookingStatusUpdateRequest request = new BookingStatusUpdateRequest();
        request.setStatus(BookingStatus.BOOKING_CONFIRMED);

        // Execute the test
        StepVerifier.create(bookingService.updateBookingStatus(bookingId, request.getStatus()))
                .expectErrorMatches(error -> error instanceof NotFoundException &&
                        error.getMessage().equals("Booking not found with ID: " + bookingId))
                .verify();
    }

    @Test
    public void whenUpdateBookingStatus_withSameStatus_thenReturnSameStatusException() {
        String bookingId = UUID.randomUUID().toString();
        Booking booking = new Booking("1", bookingId, "user1", "pack1", 1200.00, BookingStatus.BOOKING_CONFIRMED, LocalDate.of(2025, 4, 4));

        // Mock repository to return a booking with the same status
        when(bookingRepository.findBookingByBookingId(bookingId)).thenReturn(Mono.just(booking));

        BookingStatusUpdateRequest request = new BookingStatusUpdateRequest();
        request.setStatus(BookingStatus.BOOKING_CONFIRMED);

        // Execute the test
        StepVerifier.create(bookingService.updateBookingStatus(bookingId, request.getStatus()))
                .expectErrorMatches(error -> error instanceof SameStatusException &&
                        error.getMessage().equals("Booking is already in the status: " + BookingStatus.BOOKING_CONFIRMED))
                .verify();
    }


    @Test
    public void whenDeleteBooking_withExistingId_thenCompleteSuccessfully() {
        String bookingId = UUID.randomUUID().toString();
        Booking booking = new Booking("1", bookingId, "user1", "pack1", 1200.00, BookingStatus.BOOKING_CONFIRMED, LocalDate.of(2025, 4, 4));

        when(bookingRepository.findBookingByBookingId(bookingId)).thenReturn(Mono.just(booking));
        when(bookingRepository.delete(booking)).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.deleteBooking(bookingId))
                .expectNextMatches(response ->
                        response.getBookingId().equals(bookingId) &&
                                response.getUserId().equals("user1") &&
                                response.getPackageId().equals("pack1") &&
                                response.getTotalPrice() == 1200.00 &&
                                response.getStatus() == BookingStatus.BOOKING_CONFIRMED &&
                                response.getBookingDate().equals(LocalDate.of(2025, 4, 4))
                )
                .verifyComplete();
    }


    @Test
    public void whenDeleteBooking_withNonExistingId_thenReturnNotFound() {
        String bookingId = UUID.randomUUID().toString();

        when(bookingRepository.findBookingByBookingId(bookingId)).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.deleteBooking(bookingId))
                .expectErrorMatches(error -> error instanceof NotFoundException &&
                        error.getMessage().equals("Booking not found with ID: " + bookingId))
                .verify();
    }


}