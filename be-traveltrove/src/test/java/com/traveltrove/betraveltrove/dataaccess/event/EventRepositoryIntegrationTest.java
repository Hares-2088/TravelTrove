package com.traveltrove.betraveltrove.dataaccess.event;

import com.traveltrove.betraveltrove.dataaccess.events.Event;
import com.traveltrove.betraveltrove.dataaccess.events.EventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class EventRepositoryIntegrationTest {

    @Autowired
    private EventRepository eventRepository;

    private final String NON_EXISTING_EVENT_ID = "non-existing-event-id";
    private final String EXISTING_EVENT_ID = "EV01";

    private final String EXISTING_CITY_ID = "2702f60a-cf9e-46cf-a971-d76895b904e6";
    private final String EXISTING_COUNTRY_ID = "ad633b50-83d4-41f3-866a-26452bdd6f33";

    private final String NON_EXISTING_CITY_ID = "non-existing-city-id";
    private final String NON_EXISTING_COUNTRY_ID = "non-existing-country-id";


    @BeforeEach
    void setUp() {
        Event event = Event.builder()
                .id("1")
                .eventId(EXISTING_EVENT_ID)
                .name("Test Event")
                .description("Test Event Description")
                .cityId(EXISTING_CITY_ID)
                .countryId(EXISTING_COUNTRY_ID)
                .image("test_image.jpg")
                .build();

        StepVerifier.create(eventRepository.save(event))
                .expectNextMatches(savedEvent -> savedEvent.getEventId().equals(EXISTING_EVENT_ID))
                .verifyComplete();
    }

    @AfterEach
    void cleanUp() {
        StepVerifier.create(eventRepository.deleteAll())
                .verifyComplete();
    }

    @Test
    void whenFindEventByEventId_withExistingId_thenReturnExistingEvent() {
        StepVerifier.create(eventRepository.findEventByEventId(EXISTING_EVENT_ID))
                .expectNextMatches(event ->
                        event.getEventId().equals(EXISTING_EVENT_ID) &&
                                event.getName().equals("Test Event") &&
                                event.getDescription().equals("Test Event Description") &&
                                event.getCityId().equals(EXISTING_CITY_ID) &&
                                event.getCountryId().equals(EXISTING_COUNTRY_ID) &&
                                event.getImage().equals("test_image.jpg")
                )
                .verifyComplete();
    }

    @Test
    void whenFindEventByEventId_withNonExistingId_thenReturnEmptyMono() {
        StepVerifier.create(eventRepository.findEventByEventId(NON_EXISTING_EVENT_ID))
                .verifyComplete();
    }

    @Test
    void whenFindAllByCityId_withExistingId_thenReturnExistingEvent() {
        StepVerifier.create(eventRepository.findAllByCityId(EXISTING_CITY_ID))
                .expectNextMatches(event ->
                        event.getEventId().equals(EXISTING_EVENT_ID) &&
                                event.getName().equals("Test Event") &&
                                event.getDescription().equals("Test Event Description") &&
                                event.getCityId().equals(EXISTING_CITY_ID) &&
                                event.getCountryId().equals(EXISTING_COUNTRY_ID) &&
                                event.getImage().equals("test_image.jpg")
                )
                .verifyComplete();
    }

    @Test
    void whenFindAllByCityId_withNonExistingId_thenReturnEmptyFlux() {
        StepVerifier.create(eventRepository.findAllByCityId(NON_EXISTING_CITY_ID))
                .verifyComplete();
    }

    @Test
    void whenFindAllByCountryId_withExistingId_thenReturnExistingEvent() {
        StepVerifier.create(eventRepository.findAllByCountryId(EXISTING_COUNTRY_ID))
                .expectNextMatches(event ->
                        event.getEventId().equals(EXISTING_EVENT_ID) &&
                                event.getName().equals("Test Event") &&
                                event.getDescription().equals("Test Event Description") &&
                                event.getCityId().equals(EXISTING_CITY_ID) &&
                                event.getCountryId().equals(EXISTING_COUNTRY_ID) &&
                                event.getImage().equals("test_image.jpg")
                )
                .verifyComplete();
    }

    @Test
    void whenFindAllByCountryId_withNonExistingId_thenReturnEmptyFlux() {
        StepVerifier.create(eventRepository.findAllByCountryId(NON_EXISTING_COUNTRY_ID))
                .verifyComplete();
    }
}