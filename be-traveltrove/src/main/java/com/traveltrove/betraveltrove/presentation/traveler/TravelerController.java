package com.traveltrove.betraveltrove.presentation.traveler;

import com.traveltrove.betraveltrove.business.traveler.TravelerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/travelers")
@Slf4j
@RequiredArgsConstructor
public class TravelerController {

    private final TravelerService travelerService;

    //get all travelers
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TravelerResponseModel> getAllTravelers(@RequestParam(required = false) String firstName) {
        return travelerService.getAllTravelers(firstName)
                .doOnNext(traveler -> log.info("Traveler: {}", traveler));
    }

    //get traveler by id
    @GetMapping(value = "/{travelerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<TravelerResponseModel>> getTraveler(@PathVariable String travelerId) {
        log.info("Fetching traveler with id: {}", travelerId);
        return travelerService.getTravelerByTravelerId(travelerId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    //create traveler
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<TravelerResponseModel>> createTraveler(@RequestBody TravelerRequestModel travelerRequestModel) {
        log.info("Creating traveler: {}", travelerRequestModel);
        return travelerService.createTraveler(travelerRequestModel)
                .map(travelerResponseModel -> ResponseEntity.status(201).body(travelerResponseModel))
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
    }

    @PostMapping(value = "/create-traveler-user", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<TravelerResponseModel>> createTravelerUser(@RequestBody TravelerWithIdRequestModel travelerWithIdRequestModel) {
        log.info("Creating traveler user: {}", travelerWithIdRequestModel);
        return travelerService.createTravelerUser(travelerWithIdRequestModel)
                .map(response -> ResponseEntity.status(201).body(response))
                .onErrorResume(e -> {
                    log.error("Error creating traveler user", e);
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    //update traveler
    @PutMapping(value = "/{travelerId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<TravelerResponseModel>> updateTraveler(@PathVariable String travelerId,
                                                      @RequestBody TravelerRequestModel travelerRequestModel) {
        log.info("Updating traveler with id: {}", travelerId);
        return travelerService.updateTraveler(travelerId, travelerRequestModel)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error updating traveler with id: {}", travelerId, e);
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    //delete traveler
    @DeleteMapping(value = "/{travelerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<TravelerResponseModel>> deleteTraveler(@PathVariable String travelerId) {
        log.info("Deleting traveler with id: {}", travelerId);
        return travelerService.deleteTraveler(travelerId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
