package com.traveltrove.betraveltrove.dataaccess.tour;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@DataMongoTest
@ActiveProfiles("test")
class TourEventsRepositoryIntegrationTest {

    @Autowired
    private TourEventsRepository tourEventsRepository;


    @BeforeEach
    void setup() {
        TourEvents testTourEvent = TourEvents.builder()
                .toursEventId("1")
                .seq(1)
                .seqDesc("First")
                .tourId("1")
                .events("Event 1")
                .build();

        StepVerifier.create(tourEventsRepository.save(testTourEvent))
                .expectNextMatches(savedTourEvent -> savedTourEvent.getToursEventId().equals("1"))
                .verifyComplete();
    }




    @Test
    void whenFindTourEventByToursEventId_withExistingId_thenReturnExistingTourEvent() {
        Mono<TourEvents> foundTourEvent = tourEventsRepository.findByToursEventId("1");

        StepVerifier.create(foundTourEvent)
                .expectNextMatches(tourEvent ->
                        tourEvent.getToursEventId().equals("1") &&
                                tourEvent.getEvents().equals("Event 1")
                )
                .verifyComplete();
    }

    @Test
    void whenFindTourEventByTourId_withInvalidId_thenReturnEmpty() {
        Mono<TourEvents> foundTourEvent = tourEventsRepository.findByTourId("2");

        StepVerifier.create(foundTourEvent)
                .verifyComplete();
    }
}
