package com.traveltrove.betraveltrove.presentation.tour;

import com.traveltrove.betraveltrove.business.event.EventService;
import com.traveltrove.betraveltrove.business.tour.TourService;
import com.traveltrove.betraveltrove.dataaccess.events.Event;
import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import com.traveltrove.betraveltrove.dataaccess.tour.TourEvent;
import com.traveltrove.betraveltrove.dataaccess.tour.TourEventRepository;
import com.traveltrove.betraveltrove.presentation.events.EventResponseModel;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // Add this annotation
class TourEventControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TourEventRepository tourEventRepository;

    @Mock
    private TourService tourService;

    @Mock
    private EventService eventService;

    private final String INVALID_EVENT_ID = "invalid-event-id";

    private final TourEvent tourEvent1 = TourEvent.builder()
            .Id("1")
            .tourEventId(UUID.randomUUID().toString())
            .seq(1)
            .seqDesc("Opening Ceremony")
            .tourId("tour123")
            .eventId("event456")
            .build();

    private final TourEvent tourEvent2 = TourEvent.builder()
            .Id("2")
            .tourEventId(UUID.randomUUID().toString())
            .seq(2)
            .seqDesc("Cultural Performance")
            .tourId("tour123")
            .eventId("event789")
            .build();

    private final Tour tour1 = Tour.builder()
            .tourId("tour123")
            .name("Tour 123")
            .description("Tour 123 Description")
            .build();

    private final Event event1 = Event.builder()
            .eventId("event101")
            .name("Event 456")
            .description("Event 456 Description")
            .build();

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);

        Mockito.when(tourService.getTourByTourId("tour123"))
                .thenReturn(Mono.just(new TourResponseModel("tour123", "Tour 123", "Tour 123 Description")));

        Mockito.when(eventService.getEventByEventId("event101"))
                .thenReturn(Mono.just(new EventResponseModel("event101", "Event 456", "Event 456 Description", "yes", "yes", "yes")));

    }

    @BeforeEach
    public void setupDB() {
        Publisher<TourEvent> setupDB = tourEventRepository.deleteAll()
                .thenMany(Flux.just(tourEvent1, tourEvent2))
                .flatMap(tourEventRepository::save);

        StepVerifier.create(setupDB)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void whenGetTourEventByInvalidId_thenReturnNotFound() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/tour-events/{tourEventId}", INVALID_EVENT_ID)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenGetTourEventById_thenReturnTourEvent() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/tourevents/{tourEventId}", tourEvent1.getTourEventId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody(TourEvent.class)
                .value(event -> assertEquals(tourEvent1.getSeqDesc(), event.getSeqDesc()));

        StepVerifier.create(tourEventRepository.findByTourEventId(tourEvent1.getTourEventId()))
                .expectNextMatches(event -> event.getSeqDesc().equals(tourEvent1.getSeqDesc()))
                .verifyComplete();
    }

    @Test
    void whenCreateInvalidTourIdInRequest_ReturnNotFound() {
        TourEventRequestModel newEventRequest = TourEventRequestModel.builder()
                .seq(3)
                .seqDesc("Closing Ceremony")
                .tourId("invalid-tour-id")
                .eventId("event101")
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).post()
                .uri("/api/v1/tourevents")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newEventRequest)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUpdateInvalidTourEvent_thenReturnNotFound() {
        TourEvent updatedEvent = TourEvent.builder()
                .tourEventId(INVALID_EVENT_ID)
                .seq(1)
                .seqDesc("Updated Opening Ceremony")
                .tourId("tour123")
                .eventId("event456")
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).put()
                .uri("/api/v1/tourevents/{tourEventId}", INVALID_EVENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedEvent)
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    void whenUpdateTourEvent_thenReturnUpdated() {
        TourEvent updatedEvent = TourEvent.builder()
                .tourEventId(tourEvent1.getTourEventId())
                .seq(1)
                .seqDesc("Updated Opening Ceremony")
                .tourId("tour123")
                .eventId("event456")
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).put()
                .uri("/api/v1/tourevents/{tourEventId}", tourEvent1.getTourEventId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedEvent)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody(TourEvent.class)
                .value(event -> assertEquals(updatedEvent.getSeqDesc(), event.getSeqDesc()));

        StepVerifier.create(tourEventRepository.findByTourEventId(tourEvent1.getTourEventId()))
                .expectNextMatches(event -> event.getSeqDesc().equals(updatedEvent.getSeqDesc()))
                .verifyComplete();
    }

    @Test
    void whenDeleteTourEvent_thenReturnNoContent() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).delete()
                .uri("/api/v1/tourevents/{tourEventId}", tourEvent1.getTourEventId())
                .exchange()
                .expectStatus().isNoContent();

        StepVerifier.create(tourEventRepository.findByTourEventId(tourEvent1.getTourEventId()))
                .verifyComplete();
    }

    @Test
    void whenDeleteInvalidTourEvent_thenReturnNotFound() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).delete()
                .uri("/api/v1/tourevents/{tourEventId}", INVALID_EVENT_ID)
                .exchange()
                .expectStatus().isNotFound();
    }
}
