package com.traveltrove.betraveltrove.presentation.airport;

import com.traveltrove.betraveltrove.business.airport.AirportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/airports")
@Slf4j
@RequiredArgsConstructor
public class AirportController {

    private final AirportService airportService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AirportResponseModel> getAllAirports() {
        log.info("Fetching all airports");
        return airportService.getAllAirports();
    }

    @GetMapping(value = "/{airportId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<AirportResponseModel>> getAirportById(@PathVariable String airportId) {
        log.info("Fetching airport with id: {}", airportId);
        return airportService.getAirportById(airportId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AirportResponseModel> addAirport(@RequestBody AirportRequestModel airportRequestModel) {
        log.info("Adding new airport: {}", airportRequestModel.getName());
        return airportService.addAirport(airportRequestModel);
    }

    @PutMapping(value = "/{airportId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<AirportResponseModel>> updateAirport(@PathVariable String airportId, @RequestBody AirportRequestModel airportRequestModel) {
        log.info("Adding new airport: {}", airportRequestModel.getName());
        return airportService.updateAirport(airportId, airportRequestModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());

    }

    @DeleteMapping(value = "/{airportId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAirport(@PathVariable String airportId) {
        log.info("Deleting airport with id: {}", airportId);
        return airportService.deleteAirport(airportId);

    }


}
