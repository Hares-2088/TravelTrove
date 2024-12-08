package com.traveltrove.betraveltrove.presentation.tour;

import com.traveltrove.betraveltrove.business.tour.TourEventService;
import com.traveltrove.betraveltrove.dataaccess.tour.TourEventRepository;
import com.traveltrove.betraveltrove.utils.EntityModelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/tourevents")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class TourEventController {

    private final TourEventService tourEventService;
    private final TourEventRepository tourEventRepository;

    // Get all tour events
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TourEventResponseModel> getAllTourEvents() {
        log.info("Fetching all tour events");
        return tourEventService.getAllTourEvents();
    }

    // Get tour events by tour ID
    @GetMapping(value = "/tours/{tourId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TourEventResponseModel> getTourEventsByTourId(@PathVariable String tourId) {
        log.info("Fetching tour event with tourId: {}", tourId);
        return tourEventService.getTourEventsByTourId(tourId);
    }

    // Get a tour event by ID
    @GetMapping(value = "/{tourEventId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<TourEventResponseModel>> getCountryById(@PathVariable String tourEventId) {
        log.info("Fetching tour event with id: {}", tourEventId);
        return tourEventService.getTourEventByTourEventId(tourEventId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Add a new tour event
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TourEventResponseModel> addTourEvent(@Valid @RequestBody TourEventRequestModel request) {
        log.info("Adding new tour event for tourId: {}", request.getTourId());
        return tourEventService.addTourEvent(EntityModelUtil.toTourEventEntity(request));
    }

    // Update an existing tour event
    @PutMapping(value = "/{tourEventId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<TourEventResponseModel>> updateTourEvent(
            @PathVariable String tourEventId, @Valid @RequestBody TourEventRequestModel request) {
        log.info("Updating tour event with for tourEventId: {}", tourEventId);
        return tourEventService.updateTourEvent(tourEventId, request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Delete a tour event
    @DeleteMapping(value = "/{tourEventId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteTourEvent(@PathVariable String tourEventId) {
        log.info("Deleting TourEvent with tourEventId: {}", tourEventId);
        return tourEventService.deleteTourEvent(tourEventId);
    }


}
