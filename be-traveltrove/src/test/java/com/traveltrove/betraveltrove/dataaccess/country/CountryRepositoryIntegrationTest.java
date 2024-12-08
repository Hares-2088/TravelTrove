package com.traveltrove.betraveltrove.dataaccess.country;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

@DataMongoTest
@ActiveProfiles("test")
public class CountryRepositoryIntegrationTest {

    @Autowired
    private CountryRepository countryRepository;

    private final String countryId = UUID.randomUUID().toString();

    @BeforeEach
    void setup() {
        Country testCountry = Country.builder()
                .id("1")
                .countryId(countryId)
                .name("Test Country")
                .image("test_image.jpg")
                .build();

        StepVerifier.create(countryRepository.save(testCountry))
                .expectNextMatches(savedCountry -> savedCountry.getCountryId().equals(countryId))
                .verifyComplete();
    }

    @AfterEach
    void cleanup() {
        StepVerifier.create(countryRepository.deleteAll())
                .verifyComplete();
    }

    @Test
    void whenFindCountryByCountryId_withExistingId_thenReturnExistingCountry() {
        Mono<Country> foundCountry = countryRepository.findCountryByCountryId(countryId);

        StepVerifier.create(foundCountry)
                .expectNextMatches(country ->
                        country.getCountryId().equals(countryId) &&
                                country.getName().equals("Test Country") &&
                                country.getImage().equals("test_image.jpg")
                )
                .verifyComplete();
    }

    @Test
    void whenFindCountryByCountryId_withNonExistingId_thenReturnEmptyMono() {
        Mono<Country> foundCountry = countryRepository.findCountryByCountryId("INVALID_ID");

        StepVerifier.create(foundCountry)
                .verifyComplete();
    }
}
