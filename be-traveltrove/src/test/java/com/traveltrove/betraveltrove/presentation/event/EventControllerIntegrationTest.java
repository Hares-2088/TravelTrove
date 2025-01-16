package com.traveltrove.betraveltrove.presentation.event;

import com.traveltrove.betraveltrove.dataaccess.events.Event;
import com.traveltrove.betraveltrove.dataaccess.events.EventRepository;
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

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private EventRepository eventRepository;

    private final String INVALID_EVENT_ID = "invalid-event-id";

    private final Event event1 = Event.builder()
            .id("1")
            .eventId("9d024dc8-3205-4d5b-96de-f70e8e819377")
            .name("Event 1")
            .countryId("1")
            .cityId("1")
            .description("Description 1")
            .image("image1.jpg")
            .build();

    private final Event event2 = Event.builder()
            .id("2")
            .eventId("7faf2bcf-a4f3-479e-8c1a-4e1db3b3d339")
            .name("Event 2")
            .countryId("2")
            .cityId("2")
            .description("Description 2")
            .image("image2.jpg")
            .build();

    @BeforeEach
    public void setupDB() {
        StepVerifier.create(eventRepository.deleteAll())
                .verifyComplete();

        Publisher<Event> setupDB = Flux.just(event1, event2)
                .flatMap(eventRepository::save);

        StepVerifier.create(setupDB)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void whenGetAllEvents_thenReturnAllEvents() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/events")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(Event.class)
                .hasSize(2)
                .value(events -> {
                    // Ensure the list is sorted by name
                    events.sort(Comparator.comparing(Event::getName));
                    assertEquals(2, events.size());
                    assertEquals(event1.getName(), events.get(0).getName());
                    assertEquals(event2.getName(), events.get(1).getName());
                });

        StepVerifier.create(eventRepository.findAll())
                .expectNextMatches(event -> event.getName().equals(event1.getName()))
                .expectNextMatches(event -> event.getName().equals(event2.getName()))
                .verifyComplete();
    }

    @Test
    void whenGetEventById_thenReturnEvent() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/events/" + event1.getEventId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody(Event.class)
                .value(event -> assertEquals(event1.getName(), event.getName()));

        StepVerifier.create(eventRepository.findEventByEventId(event1.getEventId()))
                .expectNextMatches(event -> event.getName().equals(event1.getName()))
                .verifyComplete();
    }

    @Test
    void whenGetEventByInvalidId_thenReturnNotFound() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/events/" + INVALID_EVENT_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenGetEventsByCityId_thenReturnEvents() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/events?cityId=" + event1.getCityId())
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(Event.class)
                .hasSize(1)
                .value(events -> {
                    assertEquals(1, events.size());
                    assertEquals(event1.getName(), events.get(0).getName());
                });

        StepVerifier.create(eventRepository.findAllByCityId(event1.getCityId()))
                .expectNextMatches(event -> event.getName().equals(event1.getName()))
                .verifyComplete();
    }

    //@Test
    void whenAddEvent_thenReturnCreatedEvent() {
        // Arrange
        Event newEvent = Event.builder()
                .eventId("86ae1e54-8612-4528-a2a2-7649c0b8ee78")
                .name("Event 3")
                .description("Description 3")
                .cityId("3")
                .countryId("3")
                .image("image3.jpg")
                .build();

        // Act & Assert
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).post()
                .uri("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newEvent)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Event.class)
                .value(savedEvent -> {
                    assertNotNull(savedEvent);
                    assertEquals(newEvent.getName(), savedEvent.getName());
                    assertEquals(newEvent.getDescription(), savedEvent.getDescription());
                    assertEquals(newEvent.getCityId(), savedEvent.getCityId());
                    assertEquals(newEvent.getCountryId(), savedEvent.getCountryId());
                    assertEquals(newEvent.getImage(), savedEvent.getImage());
                });

        // Verify repository contents
        StepVerifier.create(eventRepository.findAll())
                .expectNextMatches(event -> event.getName().equals(event1.getName())
                        && event.getDescription().equals(event1.getDescription())
                        && event.getCityId().equals(event1.getCityId())
                        && event.getCountryId().equals(event1.getCountryId())
                        && event.getImage().equals(event1.getImage()))
                .expectNextMatches(event -> event.getName().equals(event2.getName())
                        && event.getDescription().equals(event2.getDescription())
                        && event.getCityId().equals(event2.getCityId())
                        && event.getCountryId().equals(event2.getCountryId())
                        && event.getImage().equals(event2.getImage()))
                .expectNextMatches(event -> event.getName().equals("Event 3")
                        && event.getDescription().equals("Description 3")
                        && event.getCityId().equals("3")
                        && event.getCountryId().equals("3")
                        && event.getImage().equals("image3.jpg"))
                .verifyComplete();
    }

    @Test
    void whenUpdateEvent_thenReturnUpdatedEvent() {
        Event updatedEvent = Event.builder()
                .name("Updated Event")
                .description("Updated Description")
                .cityId("3")
                .countryId("3")
                .image("updated_image.jpg")
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).put()
                .uri("/api/v1/events/{eventId}", event1.getEventId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedEvent)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Event.class)
                .value(event -> {
                    assertNotNull(event);
                    assertEquals(updatedEvent.getName(), event.getName());
                    assertEquals(updatedEvent.getDescription(), event.getDescription());
                    assertEquals(updatedEvent.getCityId(), event.getCityId());
                    assertEquals(updatedEvent.getCountryId(), event.getCountryId());
                    assertEquals(updatedEvent.getImage(), event.getImage());
                });

        StepVerifier.create(eventRepository.findById(event1.getId()))
                .expectNextMatches(event -> {
                    assertNotNull(event);
                    return event.getName().equals("Updated Event");
                })
                .verifyComplete();
    }

    @Test
    void whenDeleteEvent_thenEventIsDeleted() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).delete()
                .uri("/api/v1/events/{eventId}", event1.getEventId())
                .exchange()
                .expectStatus().isOk();

        StepVerifier.create(eventRepository.findById(event1.getEventId()))
                .verifyComplete();
    }

    @Test
    void whenDeleteEvent_withInvalidId_thenReturnNotFound() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).delete()
                .uri("/api/v1/events/{eventId}", INVALID_EVENT_ID)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenGetEventsByCountryId_thenReturnEvents() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/events?countryId=" + event1.getCountryId())
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(Event.class)
                .hasSize(1)
                .value(events -> {
                    assertEquals(1, events.size());
                    assertEquals(event1.getName(), events.get(0).getName());
                });

        StepVerifier.create(eventRepository.findAllByCountryId(event1.getCountryId()))
                .expectNextMatches(event -> event.getName().equals(event1.getName()))
                .verifyComplete();
    }

    @Test
    void whenGetEventsByCountryId_withInvalidId_thenReturnEmpty() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/events?countryId=invalid-country-id")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Event.class)
                .hasSize(0);

        StepVerifier.create(eventRepository.findAllByCountryId("invalid-country-id"))
                .verifyComplete();
    }

    @Test
    void whenGetEventsByCityId_withInvalidId_thenReturnEmpty() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/events?cityId=invalid-city-id")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Event.class)
                .hasSize(0);

        StepVerifier.create(eventRepository.findAllByCityId("invalid-city-id"))
                .verifyComplete();
    }
}