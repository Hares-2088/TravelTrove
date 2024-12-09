package com.traveltrove.betraveltrove.presentation.tour;

import com.traveltrove.betraveltrove.dataaccess.events.Event;
import com.traveltrove.betraveltrove.dataaccess.tour.TourEvent;
import com.traveltrove.betraveltrove.dataaccess.tour.TourEventRepository;
import com.traveltrove.betraveltrove.presentation.mockserverconfigs.MockServerConfigTourEventService;
import org.junit.jupiter.api.*;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TourEventControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TourEventRepository tourEventRepository;

    private MockServerConfigTourEventService mockServerConfigTourEventService;

    private final String INVALID_EVENT_ID = "invalid-event-id";

    private final TourEvent event1 = TourEvent.builder()
            .Id("1")
            .tourEventId(UUID.randomUUID().toString())
            .seq(1)
            .seqDesc("Opening Ceremony")
            .tourId("tour123")
            .eventId("event456")
            .build();

    private final TourEvent event2 = TourEvent.builder()
            .Id("2")
            .tourEventId(UUID.randomUUID().toString())
            .seq(2)
            .seqDesc("Cultural Performance")
            .tourId("tour123")
            .eventId("event789")
            .build();


    @BeforeAll
    public void startServer() {
        mockServerConfigTourEventService = new MockServerConfigTourEventService();
        mockServerConfigTourEventService.startMockServer();
        mockServerConfigTourEventService.registerGetAllTourEventsEndpoint(event1);
        mockServerConfigTourEventService.registerGetAllTourEventsEndpoint(event2);
        mockServerConfigTourEventService.registerGetTourEventByInvalidIdEndpoint(INVALID_EVENT_ID);
    }


    @AfterAll
    public void stopServer() {
        mockServerConfigTourEventService.stopMockServer();
    }


    @BeforeEach
    public void setupDB() {
        Publisher<TourEvent> setupDB = tourEventRepository.deleteAll()
                .thenMany(Flux.just(event1, event2))
                .flatMap(tourEventRepository::save);

        StepVerifier.create(setupDB)
                .expectNextCount(2)
                .verifyComplete();
    }



    @Test
    void whenGetTourEventByInvalidId_thenReturnNotFound() {
        webTestClient.get()
                .uri("/api/v1/tour-events/{tourEventId}", INVALID_EVENT_ID)
                .exchange()
                .expectStatus().isNotFound();
    }




    @Test
    void whenGetTourEventById_thenReturnTourEvent() {
        webTestClient.get()
                .uri("/api/v1/tourevents/{tourEventId}", event1.getTourEventId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody(TourEvent.class)
                .value(event -> assertEquals(event1.getSeqDesc(), event.getSeqDesc()));

        StepVerifier.create(tourEventRepository.findByTourEventId(event1.getTourEventId()))
                .expectNextMatches(event -> event.getSeqDesc().equals(event1.getSeqDesc()))
                .verifyComplete();
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

        webTestClient.put()
                .uri("/api/v1/tourevents/{tourEventId}", INVALID_EVENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedEvent)
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    void whenUpdateTourEvent_thenReturnUpdated() {
        TourEvent updatedEvent = TourEvent.builder()
                .tourEventId(event1.getTourEventId())
                .seq(1)
                .seqDesc("Updated Opening Ceremony")
                .tourId("tour123")
                .eventId("event456")
                .build();

        webTestClient.put()
                .uri("/api/v1/tourevents/{tourEventId}", event1.getTourEventId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedEvent)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody(TourEvent.class)
                .value(event -> assertEquals(updatedEvent.getSeqDesc(), event.getSeqDesc()));

        StepVerifier.create(tourEventRepository.findByTourEventId(event1.getTourEventId()))
                .expectNextMatches(event -> event.getSeqDesc().equals(updatedEvent.getSeqDesc()))
                .verifyComplete();
    }

    @Test
    void whenDeleteTourEvent_thenReturnNoContent() {
        webTestClient.delete()
                .uri("/api/v1/tourevents/{tourEventId}", event1.getTourEventId())
                .exchange()
                .expectStatus().isNoContent();

        StepVerifier.create(tourEventRepository.findByTourEventId(event1.getTourEventId()))
                .verifyComplete();
    }

    @Test
    void whenDeleteInvalidTourEvent_thenReturnNotFound() {
        webTestClient.delete()
                .uri("/api/v1/tourevents/{tourEventId}", INVALID_EVENT_ID)
                .exchange()
                .expectStatus().isNotFound();
    }
}
