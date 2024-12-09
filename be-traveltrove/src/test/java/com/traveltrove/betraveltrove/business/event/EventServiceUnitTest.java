package com.traveltrove.betraveltrove.business.event;

import com.traveltrove.betraveltrove.dataaccess.events.Event;
import com.traveltrove.betraveltrove.dataaccess.events.EventRepository;
import com.traveltrove.betraveltrove.presentation.events.EventRequestModel;
import com.traveltrove.betraveltrove.presentation.events.EventResponseModel;
import com.traveltrove.betraveltrove.utils.exceptions.InvalidInputException;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceUnitTest {

    @InjectMocks
    private EventServiceImpl eventService;

    @Mock
    private EventRepository eventRepository;

    Event event1 = Event.builder()
            .eventId("1")
            .name("Sample Event")
            .description("A sample event description")
            .build();

    Event event2 = Event.builder()
            .eventId("2")
            .name("Another Event")
            .description("Another event description")
            .build();

    @Test
    void whenGetEventByEventId_withExistingId_thenReturnEvent() {
        String eventId = "1";

        when(eventRepository.findEventByEventId(eventId))
                .thenReturn(Mono.just(event1));

        Mono<EventResponseModel> result = eventService.getEventByEventId(eventId);

        StepVerifier.create(result)
                .expectNextMatches(eventResponseModel -> eventResponseModel.getEventId().equals(eventId))
                .verifyComplete();
    }

    @Test
    void whenGetEventByEventId_withNonExistingId_thenReturnNotFound() {
        String eventId = "1";

        when(eventRepository.findEventByEventId(eventId))
                .thenReturn(Mono.empty());

        Mono<EventResponseModel> result = eventService.getEventByEventId(eventId);

        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof NotFoundException && error.getMessage().equals("Event id not found: " + eventId))
                .verify();
    }

    @Test
    void whenGetEvents_withCityId_thenReturnEvents() {
        String cityId = "1";

        when(eventRepository.findAllByCityId(cityId))
                .thenReturn(Flux.just(event1, event2));

        Flux<EventResponseModel> result = eventService.getEvents(cityId, null);

        StepVerifier.create(result)
                .expectNextMatches(eventResponseModel -> eventResponseModel.getEventId().equals("1"))
                .expectNextMatches(eventResponseModel -> eventResponseModel.getEventId().equals("2"))
                .verifyComplete();
    }

    @Test
    void whenGetEvents_withCountryId_thenReturnEvents() {
        String countryId = "1";

        when(eventRepository.findAllByCountryId(countryId))
                .thenReturn(Flux.just(event1, event2));

        Flux<EventResponseModel> result = eventService.getEvents(null, countryId);

        StepVerifier.create(result)
                .expectNextMatches(eventResponseModel -> eventResponseModel.getEventId().equals("1"))
                .expectNextMatches(eventResponseModel -> eventResponseModel.getEventId().equals("2"))
                .verifyComplete();
    }

    @Test
    void whenGetEvents_withNoFilters_thenReturnAllEvents() {
        when(eventRepository.findAll())
                .thenReturn(Flux.just(event1, event2));

        Flux<EventResponseModel> result = eventService.getEvents(null, null);

        StepVerifier.create(result)
                .expectNextMatches(eventResponseModel -> eventResponseModel.getEventId().equals("1"))
                .expectNextMatches(eventResponseModel -> eventResponseModel.getEventId().equals("2"))
                .verifyComplete();
    }

    @Test
    void whenCreateEvent_withValidRequest_thenReturnCreatedEvent() {
        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .cityId("1")
                .countryId("1")
                .name("Sample Event")
                .description("A sample event description")
                .build();

        // Use argument matching with Mockito
        when(eventRepository.save(any(Event.class)))
                .thenAnswer(invocation -> {
                    Event event = invocation.getArgument(0);
                    event.setEventId("1"); // Simulate the repository behavior of assigning an ID
                    return Mono.just(event);
                });

        Mono<EventResponseModel> result = eventService.createEvent(Mono.just(eventRequestModel));

        StepVerifier.create(result)
                .expectNextMatches(eventResponseModel ->
                        eventResponseModel.getEventId().equals("1") &&
                                eventResponseModel.getName().equals("Sample Event") &&
                                eventResponseModel.getCityId().equals("1") &&
                                eventResponseModel.getCountryId().equals("1"))
                .verifyComplete();
    }


    @Test
    void whenCreateEvent_withEmptyRequest_thenReturnInvalidInput() {
        Mono<EventResponseModel> result = eventService.createEvent(Mono.empty());

        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof InvalidInputException
                        && error.getMessage().equals("Event request model is empty"))
                .verify();
    }

    @Test
    void whenCreateEvent_withMissingFields_thenReturnInvalidInput() {
        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .name("") // Empty name, invalid
                .description(null) // Missing description, invalid
                .build();

        Mono<EventResponseModel> result = eventService.createEvent(Mono.just(eventRequestModel));

        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof InvalidInputException
                        && error.getMessage().equals("Event name is required"))
                .verify();
    }


    @Test
    void whenUpdateEvent_withExistingId_thenReturnUpdatedEvent() {
        String eventId = "1";
        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .cityId("1")
                .countryId("1")
                .name("Sample Event")
                .description("A sample event description")
                .build();

        when(eventRepository.findEventByEventId(eventId))
                .thenReturn(Mono.just(event1));

        when(eventRepository.save(any(Event.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<EventResponseModel> result = eventService.updateEvent(eventId, Mono.just(eventRequestModel));

        StepVerifier.create(result)
                .expectNextMatches(eventResponseModel -> eventResponseModel.getEventId().equals("1"))
                .verifyComplete();
    }

    @Test
    void whenUpdateEvent_withNonExistingId_thenReturnNotFound() {
        String eventId = "1";
        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .cityId("1")
                .countryId("1")
                .name("Sample Event")
                .description("A sample event description")
                .build();

        when(eventRepository.findEventByEventId(eventId))
                .thenReturn(Mono.empty());

        Mono<EventResponseModel> result = eventService.updateEvent(eventId, Mono.just(eventRequestModel));

        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof NotFoundException && error.getMessage().equals("Event id not found: " + eventId))
                .verify();
    }

    @Test
    void whenDeleteEvent_withExistingId_thenReturnDeletedEvent() {
        String eventId = "1";

        // Stub findEventByEventId to return event1
        when(eventRepository.findEventByEventId(eventId))
                .thenReturn(Mono.just(event1));

        // Stub delete to return Mono.empty() as it completes with no value
        when(eventRepository.delete(event1))
                .thenReturn(Mono.empty());

        // Call the deleteEvent method
        Mono<EventResponseModel> result = eventService.deleteEvent(eventId);

        // Verify the result
        StepVerifier.create(result)
                .expectNextMatches(eventResponseModel -> eventResponseModel.getEventId().equals("1"))
                .verifyComplete();
    }


    @Test
    void whenDeleteEvent_withNonExistingId_thenReturnNotFound() {
        String eventId = "1";

        when(eventRepository.findEventByEventId(eventId))
                .thenReturn(Mono.empty());

        Mono<EventResponseModel> result = eventService.deleteEvent(eventId);

        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof NotFoundException && error.getMessage().equals("Event id not found: " + eventId))
                .verify();
    }

}