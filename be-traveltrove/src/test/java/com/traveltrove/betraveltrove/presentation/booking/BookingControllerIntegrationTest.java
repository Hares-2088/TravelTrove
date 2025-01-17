package com.traveltrove.betraveltrove.presentation.booking;

import com.traveltrove.betraveltrove.business.tourpackage.PackageService;
import com.traveltrove.betraveltrove.business.user.UserService;
import com.traveltrove.betraveltrove.dataaccess.booking.Booking;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingRepository;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import com.traveltrove.betraveltrove.dataaccess.tourpackage.Package;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageResponseModel;
import com.traveltrove.betraveltrove.presentation.user.UserResponseModel;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.awt.print.Book;
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

    @MockitoBean
    private PackageService packageService;

    @MockitoBean
    private UserService userService;

    private final String INVALID_BOOKING_ID = "invalid-booking-id";

    private final Booking booking1 = Booking.builder()
            .id("1")
            .bookingId("8f8ede78-b31b-4b7a-a53e-0efaa5d625a5")
            .userId("auth0|675f4bb4e184fd643a8ed903")
            .packageId("4a1a6107-dd5c-4b3b-b71f-4e23f061b51e")
            .totalPrice(1200.00)
            .status(BookingStatus.PAYMENT_PENDING)
            .bookingDate(LocalDate.of(2025, 4, 4))
            .build();

    private final Booking booking2 = Booking.builder()
            .id("2")
            .bookingId("c6779b67-a875-4a32-a62a-5d1964039038")
            .userId("auth0|675f4bb4e184fd643a8ed902")
            .packageId("4a1a6107-dd5c-4b3b-b71f-4e23f061b51e")
            .totalPrice(1300.00)
            .status(BookingStatus.BOOKING_CONFIRMED)
            .bookingDate(LocalDate.of(2025, 5, 5))
            .build();

    private final Booking booking3 = Booking.builder()
            .id("3")
            .bookingId("396b5b1f-ed04-4558-b9b6-291a6b06fcf7")
            .userId("auth0|675f4bb4e184fd643a8ed909")
            .packageId("a522256a-3fef-4b27-8a77-50b173c4d6f0")
            .totalPrice(1300.00)
            .status(BookingStatus.BOOKING_CANCELLED)
            .bookingDate(LocalDate.of(2025, 6,5))
            .build();

    private final PackageResponseModel packageResponseModel1 = PackageResponseModel.builder()
            .packageId("a522256a-3fef-4b27-8a77-50b173c4d6f0")
            .name("Package 1")
            .description("Package 1 Description")
            .airportId("a1")
            .priceSingle(1200.00)
            .priceDouble(1300.00)
            .priceTriple(1400.00)
            .startDate(LocalDate.of(2025, 4, 4))
            .endDate(LocalDate.of(2025, 4, 11))
            .description("Package 1 Description")
            .availableSeats(10)
            .totalSeats(10)
            .build();

    private final PackageResponseModel packageResponseModel2 = PackageResponseModel.builder()
            .packageId("4a1a6107-dd5c-4b3b-b71f-4e23f061b51e")
            .name("Package 1")
            .description("Package 1 Description")
            .airportId("a1")
            .priceSingle(1200.00)
            .priceDouble(1300.00)
            .priceTriple(1400.00)
            .startDate(LocalDate.of(2025, 4, 4))
            .endDate(LocalDate.of(2025, 4, 11))
            .description("Package 1 Description")
            .availableSeats(10)
            .totalSeats(10)
            .build();

    private final UserResponseModel userResponseModel = UserResponseModel.builder()
            .userId("auth0|675f4bb4e184fd643a8ed903")
            .email("user3@example.com")
            .firstName("John")
            .lastName("Doe")
            .roles(List.of("Customer"))
            .permissions(List.of("read:bookings", "write:bookings"))
            .build();

    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.openMocks(this);

        //packages
        Mockito.when(packageService.getPackageByPackageId(packageResponseModel1.getPackageId()))
                .thenReturn(Mono.just(packageResponseModel1));

        Mockito.when(packageService.getPackageByPackageId(packageResponseModel2.getPackageId()))
                .thenReturn(Mono.just(packageResponseModel2));

        //users
        Mockito.when(userService.getUser(userResponseModel.getUserId()))
                .thenReturn(Mono.just(userResponseModel));
    }

    @BeforeEach
    public void setupDB() {
        Publisher<Booking> setupDB = bookingRepository.deleteAll()
                .thenMany(Flux.just(booking1, booking2, booking3))
                .flatMap(bookingRepository::save);

        StepVerifier.create(setupDB)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void whenGetAllBookings_thenReturnAllBookings() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/bookings")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookingResponseModel.class)
                .hasSize(3)
                .value(bookings -> {
                    assertEquals(3, bookings.size());
                    assertEquals(booking1.getBookingId(), bookings.get(0).getBookingId());
                    assertEquals(booking2.getBookingId(), bookings.get(1).getBookingId());
                    assertEquals(booking3.getBookingId(), bookings.get(2).getBookingId());
                });

        StepVerifier.create(bookingRepository.findAll())
                .expectNextMatches(booking -> booking.getBookingId().equals(booking1.getBookingId()))
                .expectNextMatches(booking -> booking.getBookingId().equals(booking2.getBookingId()))
                .expectNextMatches(booking -> booking.getBookingId().equals(booking3.getBookingId()))
                .verifyComplete();
    }

    @Test
    void whenGetBookingsByPackageId_thenReturnBookings() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/bookings?PackageId=" + packageResponseModel2.getPackageId())
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookingResponseModel.class)
                .hasSize(2)
                .value(bookings -> {
                    assertEquals(2, bookings.size());
                    assertEquals(booking1.getBookingId(), bookings.get(0).getBookingId());
                    assertEquals(booking2.getBookingId(), bookings.get(1).getBookingId());
                });

        StepVerifier.create(bookingRepository.findBookingsByPackageId(packageResponseModel2.getPackageId()))
                .expectNextMatches(booking -> booking.getBookingId().equals(booking1.getBookingId()))
                .expectNextMatches(booking -> booking.getBookingId().equals(booking2.getBookingId()))
                .verifyComplete();
    }

    @Test
    void whenGetBookingById_thenReturnBooking() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/bookings/booking?bookingId=" + booking1.getBookingId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookingResponseModel.class)
                .value(response -> assertEquals(booking1.getBookingId(), response.getBookingId()));

        StepVerifier.create(bookingRepository.findBookingByBookingId(booking1.getBookingId()))
                .expectNextMatches(booking -> booking.getBookingId().equals(booking1.getBookingId()))
                .verifyComplete();
    }

    @Test
    void whenGetBookingByInvalidId_thenReturnNotFound() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/bookings/booking?bookingId=" + INVALID_BOOKING_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenCreateBooking_withValidUserAndPackage_thenReturnCreatedBooking() {
        BookingRequestModel newBooking = BookingRequestModel.builder()
                .userId(userResponseModel.getUserId())
                .packageId("1")
                .totalPrice(1400.00)
                .status(BookingStatus.PAYMENT_PENDING)
                .bookingDate(LocalDate.of(2025, 6, 6))
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newBooking)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookingResponseModel.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(newBooking.getUserId(), response.getUserId());
                    assertEquals(newBooking.getPackageId(), response.getPackageId());
                });

        StepVerifier.create(bookingRepository.findAll())
                .expectNextCount(3) // Existing 2 bookings + newly created one
                .verifyComplete();
    }


    @Test
    void whenDeleteBooking_thenReturnNoContent() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).delete()
                .uri("/api/v1/bookings/" + booking1.getBookingId())
                .exchange()
                .expectStatus().isNoContent();

        StepVerifier.create(bookingRepository.findBookingByBookingId(booking1.getBookingId()))
                .verifyComplete();
    }
}