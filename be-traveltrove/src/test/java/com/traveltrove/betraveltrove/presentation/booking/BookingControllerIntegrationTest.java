package com.traveltrove.betraveltrove.presentation.booking;

import com.traveltrove.betraveltrove.dataaccess.booking.Booking;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingRepository;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import com.traveltrove.betraveltrove.presentation.user.UserResponseModel;
import org.junit.jupiter.api.*;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BookingRepository bookingRepository;


    private final String INVALID_BOOKING_ID = "invalid-booking-id";

    private final Booking booking1 = Booking.builder()
            .id("1")
            .bookingId(UUID.randomUUID().toString())
            .userId("user1")
            .packageId("pack1")
            .totalPrice(1200.00)
            .status(BookingStatus.PAYMENT_PENDING)
            .bookingDate(LocalDate.of(2025, 4, 4))
            .build();

    private final Booking booking2 = Booking.builder()
            .id("2")
            .bookingId(UUID.randomUUID().toString())
            .userId("user2")
            .packageId("pack2")
            .totalPrice(1300.00)
            .status(BookingStatus.BOOKING_CONFIRMED)
            .bookingDate(LocalDate.of(2025, 5, 5))
            .build();

    private final UserResponseModel mockUser = UserResponseModel.builder()
            .userId("auth0|675f4bb4e184fd643a8ed903")
            .email("user3@example.com")
            .firstName("John")
            .lastName("Doe")
            .roles(List.of("Customer"))
            .permissions(List.of("read:bookings", "write:bookings"))
            .build();

    @BeforeEach
    public void setupDB() {
        Publisher<Booking> setupDB = bookingRepository.deleteAll()
                .thenMany(Flux.just(booking1, booking2))
                .flatMap(bookingRepository::save);

        StepVerifier.create(setupDB)
                .expectNextCount(2)
                .verifyComplete();
    }

//    @Test
//    void whenGetAllBookings_thenReturnAllBookings() {
//        webTestClient.get()
//                .uri("/api/v1/bookings")
//                .accept(MediaType.TEXT_EVENT_STREAM)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBodyList(BookingResponseModel.class)
//                .hasSize(2)
//                .value(bookings -> {
//                    assertEquals(2, bookings.size());
//                    assertEquals(booking1.getBookingId(), bookings.get(0).getBookingId());
//                    assertEquals(booking2.getBookingId(), bookings.get(1).getBookingId());
//                });
//
//        StepVerifier.create(bookingRepository.findAll())
//                .expectNextMatches(booking -> booking.getBookingId().equals(booking1.getBookingId()))
//                .expectNextMatches(booking -> booking.getBookingId().equals(booking2.getBookingId()))
//                .verifyComplete();
//    }

//    @Test
//    void whenGetBookingById_thenReturnBooking() {
//        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
//                .get()
//                .uri("/api/v1/bookings/booking?bookingId=" + booking1.getBookingId())
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(BookingResponseModel.class)
//                .value(response -> assertEquals(booking1.getBookingId(), response.getBookingId()));
//
//        StepVerifier.create(bookingRepository.findBookingByBookingId(booking1.getBookingId()))
//                .expectNextMatches(booking -> booking.getBookingId().equals(booking1.getBookingId()))
//                .verifyComplete();
//    }
//
//    @Test
//    void whenGetBookingByInvalidId_thenReturnNotFound() {
//        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
//                .get()
//                .uri("/api/v1/bookings/booking?bookingId=" + INVALID_BOOKING_ID)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isNotFound();
//    }

//    @Test
//    void whenCreateBooking_withValidUserAndPackage_thenReturnCreatedBooking() {
//        BookingRequestModel newBooking = BookingRequestModel.builder()
//                .userId(mockUser.getUserId())
//                .packageId("1")
//                .totalPrice(1400.00)
//                .status(BookingStatus.PAYMENT_PENDING)
//                .bookingDate(LocalDate.of(2025, 6, 6))
//                .build();
//
//        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
//                .post()
//                .uri("/api/v1/bookings")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(newBooking)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(BookingResponseModel.class)
//                .value(response -> {
//                    assertNotNull(response);
//                    assertEquals(newBooking.getUserId(), response.getUserId());
//                    assertEquals(newBooking.getPackageId(), response.getPackageId());
//                });
//
//        StepVerifier.create(bookingRepository.findAll())
//                .expectNextCount(3) // Existing 2 bookings + newly created one
//                .verifyComplete();
//    }

//
//    @Test
//    void whenDeleteBooking_thenReturnNoContent() {
//        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
//                .mutateWith(SecurityMockServerConfigurers.csrf()).delete()
//                .uri("/api/v1/bookings/" + booking1.getBookingId())
//                .exchange()
//                .expectStatus().isNoContent();
//
//        StepVerifier.create(bookingRepository.findBookingByBookingId(booking1.getBookingId()))
//                .verifyComplete();
//    }
}