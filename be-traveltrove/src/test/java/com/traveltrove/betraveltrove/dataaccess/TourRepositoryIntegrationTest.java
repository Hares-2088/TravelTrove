package com.traveltrove.betraveltrove.dataaccess;

import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import com.traveltrove.betraveltrove.dataaccess.tour.TourRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Collections;

@DataMongoTest
@ActiveProfiles("test")
public class TourRepositoryIntegrationTest {

    @Autowired
    private TourRepository tourRepository;

    @BeforeEach
    void setup() {
        Tour testTour = Tour.builder()
                .tourId("1")
                .name("Test Tour")
                .description("A sample test tour")
//                .startDate(LocalDate.of(2024, 1, 1))
//                .endDate(LocalDate.of(2024, 1, 10))
//                .overallDescription("A sample test tour")
//                .available(true)
//                .price(500.0)
//                .spotsAvailable(20)
//                .cities(Collections.emptyList())
//                .reviews(Collections.emptyList())
//                .bookings(Collections.emptyList())
//                .image("test_image.jpg")
//                .itineraryPicture("itinerary.jpg")
                .build();

        StepVerifier.create(tourRepository.save(testTour))
                .expectNextMatches(savedTour -> savedTour.getTourId().equals("1"))
                .verifyComplete();
    }

    @AfterEach
    void cleanup() {
        StepVerifier.create(tourRepository.deleteAll())
                .verifyComplete();
    }

    @Test
    void whenFindTourByTourId_withExistingId_thenReturnExistingTour() {
        Mono<Tour> foundTour = tourRepository.findTourByTourId("1");

        StepVerifier.create(foundTour)
                .expectNextMatches(tour ->
                        tour.getTourId().equals("1") &&
                                tour.getName().equals("Test Tour")
                )
                .verifyComplete();
    }

    @Test
    void whenFindTourByTourId_withNonExistingId_thenReturnEmptyMono() {
        Mono<Tour> foundTour = tourRepository.findTourByTourId("INVALID_ID");

        StepVerifier.create(foundTour)
                .verifyComplete();
    }
}
