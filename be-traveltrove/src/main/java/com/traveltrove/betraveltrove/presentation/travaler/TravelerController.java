package com.traveltrove.betraveltrove.presentation.travaler;

import com.traveltrove.betraveltrove.business.traveler.TravelerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/travelers")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class TravelerController {

    private final TravelerService travelerService;

    public TravelerController(TravelerService travelerService) {
        this.travelerService = travelerService;
    }

    //get all travelers
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TravelerResponseModel> getAllTravelers(@RequestParam(required = false) String firstName) {
        return travelerService.getAllTravelers(firstName)
                .doOnNext(traveler -> log.info("Traveler: {}", traveler));
    }

    //get traveler by id
    @GetMapping(value = "/travelerId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TravelerResponseModel> getTraveler(@RequestParam String travelerId) {
        log.info("Fetching traveler with id: {}", travelerId);
        return travelerService.getTraveler(travelerId);
    }

    //create traveler
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TravelerResponseModel> createTraveler(@RequestBody TravelerRequestModel travelerRequestModel) {
        log.info("Creating traveler: {}", travelerRequestModel);
        return travelerService.createTraveler(travelerRequestModel);
    }

    //update traveler
    @PutMapping(value = "/{travelerId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TravelerResponseModel> updateTraveler(@PathVariable String travelerId,
                                                      @RequestBody TravelerRequestModel travelerRequestModel) {
        log.info("Updating traveler with id: {}", travelerId);
        return travelerService.updateTraveler(travelerId, travelerRequestModel);
    }

    //delete traveler
    @DeleteMapping(value = "/{travelerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TravelerResponseModel> deleteTraveler(@PathVariable String travelerId) {
        log.info("Deleting traveler with id: {}", travelerId);
        return travelerService.deleteTraveler(travelerId);
    }
}
