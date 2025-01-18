package com.traveltrove.betraveltrove.business.booking;

import com.traveltrove.betraveltrove.business.tourpackage.PackageService;
import com.traveltrove.betraveltrove.business.traveler.TravelerService;
import com.traveltrove.betraveltrove.business.user.UserService;
import com.traveltrove.betraveltrove.dataaccess.booking.Booking;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingRepository;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import com.traveltrove.betraveltrove.externalservices.auth0.Auth0Service;
import com.traveltrove.betraveltrove.presentation.booking.BookingRequestModel;
import com.traveltrove.betraveltrove.presentation.booking.BookingResponseModel;
import com.traveltrove.betraveltrove.presentation.booking.BookingStatusUpdateRequest;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageResponseModel;
import com.traveltrove.betraveltrove.presentation.traveler.TravelerRequestModel;
import com.traveltrove.betraveltrove.presentation.traveler.TravelerResponseModel;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private PackageService packageService;

    @Mock
    private TravelerService travelerService;

    private final String bookingId1 = "a0fca78b-54e6-4428-8ec3-8a42ac0dfd4e";
    private final String bookingId2 = "b1fdf006-b0c1-49fe-98ed-21f816ad524e";

    private final String packageId1 = "2484e7bf-51ee-445f-86de-cb5c1a349954";
    private final String packageId2 = "8c4a4b10-aed4-4fa1-8268-2c372df4b072";

    private final List<String> travelerIds = List.of("1", "2", "3");

    @Test
    public void whenGetAllBookings_thenReturnAllBookings() {
        Booking booking1 = new Booking("1", bookingId1, "user1", "pack1", 1200.00, BookingStatus.BOOKING_CONFIRMED, LocalDate.of(2025, 4, 4), travelerIds);
        Booking booking2 = new Booking("2", bookingId2, "user2", "pack2", 1300.00, BookingStatus.PAYMENT_PENDING, LocalDate.of(2025, 5, 5), travelerIds);

        when(bookingRepository.findAll()).thenReturn(Flux.just(booking1, booking2));

        StepVerifier.create(bookingService.getBookings())
                .expectNextMatches(response -> response.getBookingId().equals(bookingId1) && response.getStatus() == BookingStatus.BOOKING_CONFIRMED)
                .expectNextMatches(response -> response.getBookingId().equals(bookingId2) && response.getStatus() == BookingStatus.PAYMENT_PENDING)
                .verifyComplete();
    }

    @Test
    public void whenGetBookings_withExistingPackageId_thenReturnBookings() {
        // Given
        String existingPackageId = packageId1; // or any test packageId

        // Create some test bookings that share the same packageId
        Booking booking1 = new Booking(
                "1",
                bookingId1,
                "user1",
                existingPackageId,
                1200.00,
                BookingStatus.BOOKING_CONFIRMED,
                LocalDate.of(2025, 4, 4),
                java.util.List.of("1", "2", "3")
        );

        Booking booking2 = new Booking(
                "2",
                bookingId2,
                "user2",
                existingPackageId,
                1300.00,
                BookingStatus.PAYMENT_PENDING,
                LocalDate.of(2025, 5, 5),
                java.util.List.of("4", "5", "6")
        );

        // Mock PackageService to return a non-empty Mono (valid package), simulating that the package exists
        when(packageService.getPackageByPackageId(existingPackageId))
                .thenReturn(Mono.just(
                        PackageResponseModel.builder()
                                .packageId(existingPackageId)
                                .build()
                ));

        // Mock BookingRepository to return two bookings for the existingPackageId
        when(bookingRepository.findBookingsByPackageId(existingPackageId))
                .thenReturn(Flux.just(booking1, booking2));

        // When
        StepVerifier.create(bookingService.getBookingsByPackageId(existingPackageId))

                // Then
                .expectNextMatches(response ->
                        response.getBookingId().equals("a0fca78b-54e6-4428-8ec3-8a42ac0dfd4e")
                                && response.getPackageId().equals(existingPackageId)
                                && response.getStatus().equals(BookingStatus.BOOKING_CONFIRMED)
                )
                .expectNextMatches(response ->
                        response.getBookingId().equals("b1fdf006-b0c1-49fe-98ed-21f816ad524e")
                                && response.getPackageId().equals(existingPackageId)
                                && response.getStatus().equals(BookingStatus.PAYMENT_PENDING)
                )
                .verifyComplete();
    }

    @Test
    public void whenGetBookings_withNonExistingPackageId_thenReturnEmpty() {
        // Given
        String nonExistingPackageId = "YEAH_NOPE";

        // Mock PackageService to return an empty Mono (invalid package), simulating that the package does not exist
        when(packageService.getPackageByPackageId(nonExistingPackageId))
                .thenReturn(Mono.empty());

        // When
        StepVerifier.create(bookingService.getBookingsByPackageId(nonExistingPackageId))
                .expectErrorMatches(error -> error instanceof NotFoundException &&
                        error.getMessage().equals("Package not found with ID: " + nonExistingPackageId))
                .verify();
    }

    @Test
    public void whenGetBooking_withExistingBookingId_thenReturnBooking() {
        Booking booking = new Booking("1", bookingId1, "user1", "pack1", 1200.00, BookingStatus.BOOKING_CONFIRMED, LocalDate.of(2025, 4, 4), travelerIds);

        when(bookingRepository.findBookingByBookingId(bookingId1)).thenReturn(Mono.just(booking));

        StepVerifier.create(bookingService.getBooking(bookingId1))
                .expectNextMatches(response -> response.getBookingId().equals(bookingId1))
                .verifyComplete();
    }

    @Test
    public void whenGetBooking_withNonExistingBookingId_thenReturnNotFound() {
        when(bookingRepository.findBookingByBookingId(bookingId1)).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.getBooking(bookingId1))
                .expectErrorMatches(error -> error instanceof NotFoundException &&
                        error.getMessage().equals("Booking not found with ID: " + bookingId1))
                .verify();
    }

    @Test
    public void whenCreateBooking_withTravelers_thenCreateOrMergeTravelersAndSaveBooking() {
        // -------------------------------------------------------
        // 1) Define INPUT: A BookingRequestModel with travelers
        // -------------------------------------------------------
        String userId = "user123";
        String packageId = "packABC";

        // Suppose the user wants to book with two travelers: one that already exists, one new
        List<TravelerRequestModel> requestedTravelers = List.of(
                TravelerRequestModel.builder()
                        .firstName("Alice")
                        .lastName("Wonderland")
                        .build(),
                TravelerRequestModel.builder()
                        .firstName("Bob")
                        .lastName("Marley")
                        .build()
        );

        BookingRequestModel requestModel = BookingRequestModel.builder()
                .userId(userId)
                .packageId(packageId)
                .totalPrice(1200.00)
                .status(BookingStatus.PAYMENT_PENDING)
                .bookingDate(LocalDate.of(2025, 4, 4))
                .travelers(requestedTravelers)  // <--- The travelers
                .build();

        // -------------------------------------------------------
        // 2) Define what the final saved Booking should look like
        // (after traveler logic). We'll mock the "save" result.
        // -------------------------------------------------------
        List<String> finalTravelerIds = List.of("TID-EXISTING-111", "TID-NEW-222");

        Booking savedBooking = Booking.builder()
                .id("1")
                .bookingId("generated-booking-id-123")
                .userId(userId)
                .packageId(packageId)
                .totalPrice(1200.00)
                .status(BookingStatus.PAYMENT_PENDING)
                .bookingDate(LocalDate.of(2025, 4, 4))
                .travelerIds(finalTravelerIds)
                .build();

        // -------------------------------------------------------
        // 3) MOCK DEPENDENCIES
        // -------------------------------------------------------

        // 3a) PackageService => The package must exist
        when(packageService.getPackageByPackageId(packageId))
                .thenReturn(Mono.just(
                        PackageResponseModel.builder()
                                .packageId(packageId)
                                .build()
                ));

        // 3b) userService.syncUserWithAuth0(...)
        UserResponseModel mockUser = UserResponseModel.builder()
                .userId(userId)
                .travelerId("TID-EXISTING-111")   // One "primary" traveler ID
                .travelerIds(new ArrayList<>())   // For illustration, empty list
                .build();
        when(userService.syncUserWithAuth0(userId))
                .thenReturn(Mono.just(mockUser));

        // 3c) travelerService.getTravelerByTravelerId(...)
        // We only expect "TID-EXISTING-111" to be fetched, so let's remove the fallback.
        TravelerResponseModel existingTraveler = TravelerResponseModel.builder()
                .travelerId("TID-EXISTING-111")
                .firstName("Alice")
                .lastName("Wonderland")
                .build();
        when(travelerService.getTravelerByTravelerId("TID-EXISTING-111"))
                .thenReturn(Mono.just(existingTraveler));

        // 3d) travelerService.createTraveler(...) => for the brand-new traveler (Bob Marley)
        TravelerResponseModel newTraveler = TravelerResponseModel.builder()
                .travelerId("TID-NEW-222")
                .firstName("Bob")
                .lastName("Marley")
                .build();
        when(travelerService.createTraveler(any(TravelerRequestModel.class)))
                .thenReturn(Mono.just(newTraveler));

        // 3e) userService.updateUserProfile(...)
        when(userService.updateUserProfile(eq(userId), any()))
                .thenReturn(Mono.just(
                        mockUser.toBuilder()
                                // now user has both TID-EXISTING-111 & TID-NEW-222
                                .travelerIds(List.of("TID-EXISTING-111", "TID-NEW-222"))
                                .build()
                ));

        // 3f) bookingRepository => must not already exist
        when(bookingRepository.findBookingByPackageIdAndUserId(packageId, userId))
                .thenReturn(Mono.empty());

        // 3g) bookingRepository.save(...) => return the final saved booking
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(Mono.just(savedBooking));

        // 4) VERIFY
        StepVerifier.create(bookingService.createBooking(requestModel))
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals("generated-booking-id-123", response.getBookingId());
                    assertEquals(2, response.getTravelerIds().size());
                    assertTrue(response.getTravelerIds().contains("TID-EXISTING-111"));
                    assertTrue(response.getTravelerIds().contains("TID-NEW-222"));
                    assertEquals(BookingStatus.PAYMENT_PENDING, response.getStatus());
                })
                .verifyComplete();
    }


    @Test
    public void whenUpdateBookingStatus_withExistingId_thenReturnUpdatedBooking() {

    List<String> travelerIds = List.of("1", "2", "3");

    Booking booking = new Booking("1", bookingId1, "user1", "pack1", 1200.00, BookingStatus.PAYMENT_PENDING, LocalDate.of(2025, 4, 4), travelerIds);
    Booking updatedBooking = new Booking("1", bookingId1, "user1", "pack1", 1200.00, BookingStatus.BOOKING_CONFIRMED, LocalDate.of(2025, 4, 4), travelerIds);
        // Mock repository calls
        when(bookingRepository.findBookingByBookingId(bookingId1)).thenReturn(Mono.just(booking));
        when(bookingRepository.save(booking)).thenReturn(Mono.just(updatedBooking));

        // Create a BookingStatusUpdateRequest
        BookingStatusUpdateRequest request = BookingStatusUpdateRequest.builder()
                .status(BookingStatus.BOOKING_CONFIRMED)
                .build();

        // Execute the test
        StepVerifier.create(bookingService.updateBookingStatus(bookingId1, request.getStatus()))
                .expectNextMatches(response -> response.getStatus() == BookingStatus.BOOKING_CONFIRMED)
                .verifyComplete();
    }

    @Test
    public void whenUpdateBookingStatus_withNonExistingId_thenReturnNotFound() {
        // Mock repository to return Mono.empty()
        when(bookingRepository.findBookingByBookingId(bookingId1)).thenReturn(Mono.empty());

        BookingStatusUpdateRequest request = BookingStatusUpdateRequest.builder()
                .status(BookingStatus.BOOKING_CONFIRMED)
                .build();

        // Execute the test
        StepVerifier.create(bookingService.updateBookingStatus(bookingId1, request.getStatus()))
                .expectErrorMatches(error -> error instanceof NotFoundException &&
                        error.getMessage().equals("Booking not found with ID: " + bookingId1))
                .verify();
    }

    @Test
    public void whenUpdateBookingStatus_withSameStatus_thenReturnSameStatusException() {
        Booking booking = new Booking("1", bookingId1, "user1", "pack1", 1200.00, BookingStatus.BOOKING_CONFIRMED, LocalDate.of(2025, 4, 4), travelerIds);

        // Mock repository to return a booking with the same status
        when(bookingRepository.findBookingByBookingId(bookingId1)).thenReturn(Mono.just(booking));

        BookingStatusUpdateRequest request = BookingStatusUpdateRequest.builder()
                .status(BookingStatus.BOOKING_CONFIRMED)
                .build();

        // Execute the test
        StepVerifier.create(bookingService.updateBookingStatus(bookingId1, request.getStatus()))
                .expectErrorMatches(error -> error instanceof SameStatusException &&
                        error.getMessage().equals("Booking is already in the status: " + BookingStatus.BOOKING_CONFIRMED))
                .verify();
    }

    @Test
    public void whenDeleteBooking_withExistingId_thenCompleteSuccessfully() {
        Booking booking = new Booking("1", bookingId1, "user1", "pack1", 1200.00, BookingStatus.BOOKING_CONFIRMED, LocalDate.of(2025, 4, 4), travelerIds);

        when(bookingRepository.findBookingByBookingId(bookingId1)).thenReturn(Mono.just(booking));
        when(bookingRepository.delete(booking)).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.deleteBooking(bookingId1))
                .expectNextMatches(response ->
                        response.getBookingId().equals(bookingId1) &&
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
        when(bookingRepository.findBookingByBookingId(bookingId1)).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.deleteBooking(bookingId1))
                .expectErrorMatches(error -> error instanceof NotFoundException &&
                        error.getMessage().equals("Booking not found with ID: " + bookingId1))
                .verify();
    }

}