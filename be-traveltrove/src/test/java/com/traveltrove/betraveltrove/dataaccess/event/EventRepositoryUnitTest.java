package com.traveltrove.betraveltrove.dataaccess.event;

import com.traveltrove.betraveltrove.dataaccess.events.Event;
import com.traveltrove.betraveltrove.dataaccess.events.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class EventRepositoryUnitTest {

    @Autowired
    private EventRepository eventRepository;

    private final String NON_EXISTING_EVENT_ID = "non-existing-event-id";
    private final String EXISTING_EVENT_ID = "EV01";

    private final String EXISTING_CITY_ID = "2702f60a-cf9e-46cf-a971-d76895b904e6";
    private final String EXISTING_COUNTRY_ID = "ad633b50-83d4-41f3-866a-26452bdd6f33";

    private final String NON_EXISTING_CITY_ID = "non-existing-city-id";
    private final String NON_EXISTING_COUNTRY_ID = "non-existing-country-id";


    private final Event event1 = Event.builder()
            .eventId("EV01")
            .name("Sample Event")
            .description("A sample event description")
            .cityId(EXISTING_CITY_ID)
            .countryId(EXISTING_COUNTRY_ID)
            .build();

    private final Event event2 = Event.builder()
            .eventId("EV02")
            .name("Another Event")
            .description("Another event description")
            .cityId(EXISTING_CITY_ID)
            .countryId(EXISTING_COUNTRY_ID)
            .build();


    @BeforeEach
    void setUp() {
        StepVerifier
                .create(eventRepository.deleteAll())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void whenFindEventByEventId_withExistingId_thenReturnEvent() {
        StepVerifier
                .create(eventRepository.save(event1))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier
                .create(eventRepository.findEventByEventId(EXISTING_EVENT_ID))
                .expectNextMatches(event -> event.getEventId().equals(EXISTING_EVENT_ID))
                .verifyComplete();
    }

    @Test
    void whenFindEventByEventId_withNonExistingId_thenReturnEmpty() {
        StepVerifier
                .create(eventRepository.findEventByEventId(NON_EXISTING_EVENT_ID))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void whenFindAllByCityId_withExistingCityId_thenReturnEvents() {
        StepVerifier
                .create(eventRepository.save(event1))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier
                .create(eventRepository.save(event2))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier
                .create(eventRepository.findAllByCityId(EXISTING_CITY_ID))
                .expectNextMatches(event -> event.getEventId().equals(EXISTING_EVENT_ID))
                .expectNextMatches(event -> event.getEventId().equals("EV02"))
                .verifyComplete();
    }

    @Test
    void whenFindAllByCityId_withNonExistingCityId_thenReturnEmpty() {
        StepVerifier
                .create(eventRepository.findAllByCityId(NON_EXISTING_CITY_ID))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void whenFindAllByCountryId_withExistingCountryId_thenReturnEvents() {
        StepVerifier
                .create(eventRepository.save(event1))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier
                .create(eventRepository.save(event2))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier
                .create(eventRepository.findAllByCountryId(EXISTING_COUNTRY_ID))
                .expectNextMatches(event -> event.getEventId().equals(EXISTING_EVENT_ID))
                .expectNextMatches(event -> event.getEventId().equals("EV02"))
                .verifyComplete();
    }

    @Test
    void whenFindAllByCountryId_withNonExistingCountryId_thenReturnEmpty() {
        StepVerifier
                .create(eventRepository.findAllByCountryId(NON_EXISTING_COUNTRY_ID))
                .expectNextCount(0)
                .verifyComplete();
    }

}