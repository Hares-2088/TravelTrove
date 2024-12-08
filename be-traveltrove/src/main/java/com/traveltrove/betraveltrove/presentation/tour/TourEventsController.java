package com.traveltrove.betraveltrove.presentation.tour;

import com.traveltrove.betraveltrove.business.tour.TourEventsService;
import com.traveltrove.betraveltrove.dataaccess.tour.TourEvents;
import com.traveltrove.betraveltrove.utils.EntityModelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/tour-events")
public class TourEventsController {

    @Autowired
    private TourEventsService tourEventsService;

    public TourEventsController(TourEventsService tourEventsService) {
        this.tourEventsService = tourEventsService;
    }

    @GetMapping
    public Flux<TourEventsResponseModel> getAllTourEvents() {
        return tourEventsService.getAllTourEvents()
                .map(event -> {
                    if (event == null) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tour events not found");
                    }
                    return event;
                });
    }

    @GetMapping("{tourId}")
    public Mono<TourEventsResponseModel> getTourEventsByTourId(@PathVariable String tourId) {
        return tourEventsService.getTourEventsByTourId(tourId);
    }

    @PostMapping
    public Mono<ResponseEntity<TourEventsResponseModel>> addTourEvent(@RequestBody TourEventsRequestModel request) {
        return tourEventsService.addTourEvent(EntityModelUtil.toTourEventsEntity(request))
                .map(event -> ResponseEntity.status(HttpStatus.CREATED).body(event));
    }

    @PutMapping("/{toursEventId}")
    public Mono<ResponseEntity<TourEventsResponseModel>> updateTourEvent(@PathVariable String toursEventId,
                                                                         @RequestBody TourEventsRequestModel request) {
        return tourEventsService.updateTourEvent(toursEventId, request)
                .map(event -> ResponseEntity.ok(event))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{toursEventId}")
    public Mono<ResponseEntity<Void>> deleteTourEvent(@PathVariable String toursEventId) {
        return tourEventsService.deleteTourEvent(toursEventId)
                .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
    }
}
