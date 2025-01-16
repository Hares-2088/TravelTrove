package com.traveltrove.betraveltrove.presentation.tour;

import com.traveltrove.betraveltrove.business.tour.TourService;
import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import com.traveltrove.betraveltrove.utils.EntityModelUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/tours")
@Slf4j
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;

    //get all tours
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TourResponseModel> getAllTours() {
        return tourService.getTours()
                .doOnNext(tour -> log.info("Tour: {}", tour));
    }
    //get tour by id
    @GetMapping(value = "/{tourId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<TourResponseModel>> getTourByTourId(@PathVariable String tourId) {
        return tourService.getTourByTourId(tourId)
                .doOnNext(tour -> log.info("Fetched Tour: {}", tour))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // add new tour
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TourResponseModel> addTour(@RequestBody TourRequestModel tourRequestModel) {
        log.info("Added new Tour: {}", tourRequestModel.getName());
        return tourService.addTour(EntityModelUtil.toTourEntity(tourRequestModel));
    }

    //update tour
    @PutMapping(value = {"/{tourId}"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono <ResponseEntity<TourResponseModel>> updateTour(
            @PathVariable String tourId,
            @Valid @RequestBody TourRequestModel tourRequestModel){
        log.info("Updating tour with id: {}", tourId);

        Tour updatedTour = EntityModelUtil.toTourEntity(tourRequestModel);
        updatedTour.setTourId(tourId);

        return tourService.updateTour(tourId, tourRequestModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    //delete tour
    @DeleteMapping(value = "/{tourId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteTourByTourId(@PathVariable String tourId) {
        log.info("Deleting tour with id: {}", tourId);
        return tourService.deleteTourByTourId(tourId);
    }
}
