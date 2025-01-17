package com.traveltrove.betraveltrove.dataaccess.city;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

@DataMongoTest
@ActiveProfiles("test")
public class CityRepositoryUnitTest {

    @Autowired
    private CityRepository cityRepository;

    private final String NON_EXISTING_CITY_ID = "non-existing-city-id";
    private final String EXISTING_CITY_ID = "CITY01";

    private final String EXISTING_COUNTRY_ID = "COUNTRY01";
    private final String NON_EXISTING_COUNTRY_ID = "non-existing-country-id";

    private final City city1 = City.builder()
            .cityId("CITY01")
            .name("Sample City")
            .countryId(EXISTING_COUNTRY_ID)
            .build();

    private final City city2 = City.builder()
            .cityId("CITY02")
            .name("Another City")
            .countryId(EXISTING_COUNTRY_ID)
            .build();

    @BeforeEach
    void setUp() {
        StepVerifier
                .create(cityRepository.deleteAll())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void whenFindCityByCityId_withExistingId_thenReturnCity() {
        StepVerifier
                .create(cityRepository.save(city1))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier
                .create(cityRepository.findCityByCityId(EXISTING_CITY_ID))
                .expectNextMatches(city ->
                        city.getCityId().equals(EXISTING_CITY_ID) &&
                        city.getName().equals("Sample City") &&
                        city.getCountryId().equals(EXISTING_COUNTRY_ID))
                .verifyComplete();
    }

    @Test
    void whenFindCityByCityId_withNonExistingId_thenReturnEmpty() {
        StepVerifier
                .create(cityRepository.findCityByCityId(NON_EXISTING_CITY_ID))
                .verifyComplete();
    }

    @Test
    void whenFindAllByCountryId_withExistingCountryId_thenReturnCities() {
        StepVerifier
                .create(cityRepository.save(city1))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier
                .create(cityRepository.save(city2))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier
                .create(cityRepository.findAllCitiesByCountryId(EXISTING_COUNTRY_ID))
                .expectNextMatches(city -> city.getCityId().equals(EXISTING_CITY_ID))
                .expectNextMatches(city -> city.getCityId().equals("CITY02"))
                .verifyComplete();
    }

    @Test
    void whenFindAllByCountryId_withNonExistingCountryId_thenReturnEmpty() {
        StepVerifier
                .create(cityRepository.findAllCitiesByCountryId(NON_EXISTING_COUNTRY_ID))
                .verifyComplete();
    }

    @Test
    void whenFindAllCities_thenReturnAllCities() {
        StepVerifier
                .create(cityRepository.save(city1))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier
                .create(cityRepository.save(city2))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier
                .create(cityRepository.findAll())
                .expectNextMatches(city ->
                        city.getCityId().equals("CITY01") &&
                        city.getName().equals("Sample City") &&
                        city.getCountryId().equals(EXISTING_COUNTRY_ID))
                .expectNextMatches(city ->
                        city.getCityId().equals("CITY02") &&
                        city.getName().equals("Another City") &&
                        city.getCountryId().equals(EXISTING_COUNTRY_ID))
                .verifyComplete();
    }

    @Test
    void whenFindAllCities_withEmptyRepository_thenReturnEmpty() {
        StepVerifier
                .create(cityRepository.findAll())
                .verifyComplete();
    }

}
