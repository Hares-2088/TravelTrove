package com.traveltrove.betraveltrove.presentation.tour;

import com.traveltrove.betraveltrove.presentation.mockserverconfigs.MockServerConfigTourService;
import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import com.traveltrove.betraveltrove.dataaccess.tour.TourRepository;
import com.traveltrove.betraveltrove.presentation.tour.TourRequestModel;
import com.traveltrove.betraveltrove.presentation.tour.TourResponseModel;
import org.junit.jupiter.api.*;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Collections;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TourControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TourRepository tourRepository;

    private MockServerConfigTourService mockServerConfigTourService;

    private final String INVALID_TOUR_ID = "clearly-not-a-valid-tour-id"; //really? I would have never guessed -Ioannis

    private final Tour tour1 = Tour.builder()
            .tourId("1")
            .name("Test Tour 1")
            .description("A sample test tour")
//            .startDate(LocalDate.of(2024, 1, 1))
//            .endDate(LocalDate.of(2024, 1, 10))
//            .overallDescription("A sample test tour")
//            .available(true)
//            .price(500.0)
//            .spotsAvailable(20)
//            .cities(Collections.emptyList())
//            .reviews(Collections.emptyList())
//            .bookings(Collections.emptyList())
//            .image("test_image.jpg")
//            .itineraryPicture("itinerary.jpg")
            .build();

    private final Tour tour2 = Tour.builder()
            .tourId("2")
            .name("Test Tour 2")
            .description("Another sample test tour")
//            .startDate(LocalDate.of(2024, 2, 1))
//            .endDate(LocalDate.of(2024, 2, 15))
//            .overallDescription("Another sample test tour")
//            .available(true)
//            .price(700.0)
//            .spotsAvailable(30)
//            .cities(Collections.emptyList())
//            .reviews(Collections.emptyList())
//            .bookings(Collections.emptyList())
//            .image("test_image2.jpg")
//            .itineraryPicture("itinerary2.jpg")
            .build();

    private final TourRequestModel tourRequestModel = TourRequestModel.builder()
            .name("Test TourRequestModel 1")
            .description("Yet another sample test tour")
            .build();
    private final TourResponseModel tourResponseModel = TourResponseModel.builder()
            .name("Test TourRequestModel 2")
            .description("Yet another sample test tour")
            .build();


    @BeforeAll
    public void startServer() {
        mockServerConfigTourService = new MockServerConfigTourService();
        mockServerConfigTourService.startMockServer();
        mockServerConfigTourService.registerGetTourByIdEndpoint(tour1);
        mockServerConfigTourService.registerGetTourByIdEndpoint(tour2);
        mockServerConfigTourService.registerGetTourByInvalidIdEndpoint(INVALID_TOUR_ID);
    }

    @AfterAll
    public void stopServer() {
        mockServerConfigTourService.stopMockServer();
    }

    @BeforeEach
    public void setupDB() {
        Publisher<Tour> setupDB = tourRepository.deleteAll()
                .thenMany(Flux.just(tour1, tour2))
                .flatMap(tourRepository::save);

        StepVerifier
                .create(setupDB)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void whenGetTours_thenReturnAllTours() {
        webTestClient.get()
                .uri("/api/v1/tours")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM + ";charset=UTF-8")
                .expectBodyList(Tour.class)
                .value(tours -> {
                    assertNotNull(tours);
                    assertEquals(2, tours.size());
                    assertEquals(tour1.getTourId(), tours.get(0).getTourId());
                    assertEquals(tour1.getName(), tours.get(0).getName());
                    assertEquals(tour2.getTourId(), tours.get(1).getTourId());
                    assertEquals(tour2.getName(), tours.get(1).getName());
                });

        StepVerifier
                .create(tourRepository.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void whenGetTours_whenNoToursExist_thenReturnEmptyList() {
        tourRepository.deleteAll().block();
        webTestClient.get()
                .uri("/api/v1/tours")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM + ";charset=UTF-8")
                .expectBodyList(Tour.class)
                .value(tours -> {
                    assertNotNull(tours);
                    assertEquals(0, tours.size());
                });

        StepVerifier
                .create(tourRepository.findAll())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void whenGetTourByTourId_withExistingId_thenReturnTourResponseModel() {
        webTestClient.get()
                .uri("/api/v1/tours/{tourId}", tour1.getTourId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Tour.class)
                .value(responseTour -> {
                    assertNotNull(responseTour);
                    assertEquals(tour1.getTourId(), responseTour.getTourId());
                    assertEquals(tour1.getName(), responseTour.getName());
                });

        StepVerifier
                .create(tourRepository.findById(tour1.getId()))
                .expectNextMatches(foundTour -> foundTour.getTourId().equals(tour1.getTourId()))
                .verifyComplete();
    }

    @Test
    void whenGetTourByTourId_withInvalidId_thenReturnNotFound() {
        webTestClient.get()
                .uri("/api/v1/tours/{tourId}", INVALID_TOUR_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Tour Id not found: " + INVALID_TOUR_ID);

        StepVerifier
                .create(tourRepository.findById(INVALID_TOUR_ID))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void whenAddTour_withValidTourRequestModel_thenReturnCreatedTourResponseModel() {


        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).post()
                .uri("/api/v1/tours")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tourRequestModel)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TourResponseModel.class)
                .value(responseTour -> {
                    assertNotNull(responseTour);
                    assertEquals(tourRequestModel.getName(), responseTour.getName());
                });

        StepVerifier.create(tourRepository.findAll())
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void whenUpdateTour_withValidTourRequestModel_thenReturnUpdatedTourResponseModel() {

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).put()
                .uri("/api/v1/tours/{tourId}", tour1.getTourId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tourRequestModel)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TourResponseModel.class)
                .value(responseTour -> {
                    assertNotNull(responseTour);
                    assertEquals(tourRequestModel.getName(), responseTour.getName());
                });

        StepVerifier.create(
                        tourRepository.findTourByTourId(tour1.getTourId())
                                .map(Tour::getName)
                )
                .expectNext(tourRequestModel.getName())
                .verifyComplete();
    }

    @Test
    void whenUpdateTour_withInvalidTourId_thenReturnNotFound() {


        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).put()
                .uri("/api/v1/tours/{tourId}", INVALID_TOUR_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tourRequestModel)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Tour Id not found: " + INVALID_TOUR_ID);

        StepVerifier
                .create(tourRepository.findTourByTourId(INVALID_TOUR_ID))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void whenDeleteTourByTourId_withValidTourId_thenReturnNoContent() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).delete()
                .uri("/api/v1/tours/{tourId}", tour1.getTourId())
                .exchange()
                .expectStatus().isNoContent();

        StepVerifier
                .create(tourRepository.findById(tour1.getId()))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void whenDeleteTourByTourId_withInvalidTourId_thenReturnNotFound() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).delete()
                .uri("/api/v1/tours/{tourId}", INVALID_TOUR_ID)
                .exchange()
                .expectStatus().isNotFound();

        StepVerifier.create(tourRepository.findById(INVALID_TOUR_ID))
                .expectNextCount(0)
                .verifyComplete();
    }

}
