package com.traveltrove.betraveltrove.presentation.city;

import com.traveltrove.betraveltrove.business.city.CityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/v1/cities")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CityResponseModel> addCity(@Valid @RequestBody CityRequestModel cityRequestModel) {
        return cityService.addCity(cityRequestModel);
    }

    @GetMapping(value = "/{cityId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CityResponseModel>> getCityById(@PathVariable String cityId) {
        return cityService.getCityById(cityId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CityResponseModel> getAllCities() {
        return cityService.getAllCities();
    }

    @PutMapping(value = "/{cityId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CityResponseModel>> updateCity(@PathVariable String cityId, @Valid @RequestBody CityRequestModel cityRequestModel) {
        return cityService.updateCity(cityId, cityRequestModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping(value = "/{cityId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteCityByCityId(@PathVariable String cityId) {
        return cityService.deleteCityByCityId(cityId);
    }

    @GetMapping(value = "/country/{countryId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ResponseEntity<CityResponseModel>> getAllCitiesByCountryId(@PathVariable String countryId) {
        return cityService.getAllCitiesByCountryId(countryId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping(value = "/{cityId}/country/{countryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CityResponseModel>> getCityByCityIdAndCountryId(@PathVariable String cityId, @PathVariable String countryId) {
        return cityService.getCityByCityIdAndCountryId(cityId, countryId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

}
