package com.traveltrove.betraveltrove.presentation.country;

import com.traveltrove.betraveltrove.business.country.CountryService;
import com.traveltrove.betraveltrove.dataaccess.country.Country;
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
@RequestMapping("/api/v1/countries")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    // Get all countries
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CountryResponseModel> getAllCountries() {
        log.info("Fetching all countries");
        return countryService.getAllCountries();
    }

    // Get a country by ID
    @GetMapping(value = "/{countryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CountryResponseModel>> getCountryById(@PathVariable String countryId) {
        log.info("Fetching country with id: {}", countryId);
        return countryService.getCountryById(countryId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Add a new country
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CountryResponseModel> addCountry(@Valid @RequestBody CountryRequestModel countryRequestModel) {
        log.info("Adding new country: {}", countryRequestModel.getName());
        return countryService.addCountry(EntityModelUtil.toCountryEntity(countryRequestModel));
    }

    // Update an existing country
    @PutMapping(value = "/{countryId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CountryResponseModel>> updateCountry(
            @PathVariable String countryId,
            @Valid @RequestBody CountryRequestModel countryRequestModel) {
        log.info("Updating country with id: {}", countryId);

        Country updatedCountry = EntityModelUtil.toCountryEntity(countryRequestModel);
        updatedCountry.setCountryId(countryId);

        return countryService.updateCountry(countryId, countryRequestModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Delete a country by ID
    @DeleteMapping(value = "/{countryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteCountry(@PathVariable String countryId) {
        log.info("Deleting country with id: {}", countryId);
        return countryService.deleteCountry(countryId);
    }

}
