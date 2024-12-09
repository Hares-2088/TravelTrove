package com.traveltrove.betraveltrove.dataaccess.tour;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
@DataMongoTest
@ActiveProfiles("test")
class TourEventRepositoryIntegrationTest {

    @Autowired
    private TourEventRepository tourEventRepository;

    @BeforeEach
    void setup() {
        TourEvent testTour = TourEvent.builder()
                .tourId("1")
                .tourEventId("1")
                .build();

        StepVerifier.create(tourEventRepository.save(testTour))
                .expectNextMatches(savedTour -> savedTour.getTourId().equals("1"))
                .verifyComplete();
    }

    @AfterEach
    void cleanup() {
        StepVerifier.create(tourEventRepository.deleteAll())
                .verifyComplete();
    }

    @Test
    void whenFindTourEventByTourEventId_withExistingId_thenReturnExistingTourEvent() {
        Mono<TourEvent> foundTourEvent = tourEventRepository.findByTourEventId("1");

        StepVerifier.create(foundTourEvent)
                .expectNextMatches(tourEvent ->
                        tourEvent.getTourEventId().equals("1") &&
                                tourEvent.getTourId().equals("1")
                )
                .verifyComplete();
    }

    @Test
    void whenFindTourEventByTourEventId_withNonExistingId_thenReturnEmptyMono() {
        Mono<TourEvent> foundTourEvent = tourEventRepository.findByTourEventId("INVALID_ID");

        StepVerifier.create(foundTourEvent)
                .verifyComplete();
    }
}