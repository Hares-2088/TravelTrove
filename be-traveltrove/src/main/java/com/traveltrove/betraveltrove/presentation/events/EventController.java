package com.traveltrove.betraveltrove.presentation.events;

import com.traveltrove.betraveltrove.business.event.EventService;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/events")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Get all events
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<EventResponseModel> getAllEvents() {
        log.info("Fetching all events");
        return eventService.getEvents();
    }

    // Get all events by city ID
    @GetMapping(value = "/city/{cityId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<EventResponseModel> getEventsByCityId(@PathVariable String cityId) {
        log.info("Fetching all events by city ID: {}", cityId);
        return eventService.getEventsByCityId(cityId);
    }

    // Get all events by country ID
    @GetMapping(value = "/country/{countryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<EventResponseModel> getEventsByCountryId(@PathVariable String countryId) {
        log.info("Fetching all events by country ID: {}", countryId);
        return eventService.getEventsByCountryId(countryId);
    }

    // Get an event by event ID
    @GetMapping(value = "/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<EventResponseModel>> getEventByEventId(@PathVariable String eventId) {
        log.info("Fetching event by event ID: {}", eventId);
        return eventService.getEventByEventId(eventId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    // Create a new event
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<EventResponseModel>> createEvent(@RequestBody Mono<EventRequestModel> eventRequestModel) {
        log.info("Creating new event");
        return eventService.createEvent(eventRequestModel)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
    }

    // Update an existing event
    @PutMapping(value = "/{eventId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<EventResponseModel>> updateEvent(
            @PathVariable String eventId,
            @RequestBody Mono<EventRequestModel> eventRequestModel) {
        log.info("Updating event with event ID: {}", eventId);
        return eventService.updateEvent(eventId, eventRequestModel)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    // Delete an event by event ID
    @DeleteMapping(value = "/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> deleteEvent(@PathVariable String eventId) {
        log.info("Deleting event with event ID: {}", eventId);
        return eventService.deleteEvent(eventId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
