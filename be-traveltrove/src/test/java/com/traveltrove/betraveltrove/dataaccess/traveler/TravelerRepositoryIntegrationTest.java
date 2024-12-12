package com.traveltrove.betraveltrove.dataaccess.traveler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class TravelerRepositoryIntegrationTest {

    @Autowired
    private TravelerRepository travelerRepository;

    private final String NON_EXISTING_TRAVELER_ID = "non-existing-traveler-id";

    private final String EXISTING_TRAVELER_ID = "j0e91f6c-723e-43b9-812f-2f3d3bfb4090";

    private final String EXISTING_COUNTRY_ID = "ad633b50-83d4-41f3-866a-26452bdd6f33";
    private final String NON_EXISTING_COUNTRY_ID = "non-existing-country-id";

    @BeforeEach
    void setUp() {
        Traveler traveler = Traveler.builder()
                .id("1")
                .travelerId(EXISTING_TRAVELER_ID)
                .seq(1)
                .firstName("Test")
                .lastName("Traveler")
                .addressLine1("123 Test Street")
                .addressLine2("Apt 1")
                .city("Test City")
                .state("Test State")
                .countryId("EXISTING_COUNTRY_ID")
                .email("testtraveler@gmail.com")
                .build();

        Traveler traveler2 = Traveler.builder()
                .id("2")
                .travelerId("j0e91f6c-723e-43b9-812f-2f3d3bfb4091")
                .seq(2)
                .firstName("Test")
                .lastName("Traveler2")
                .addressLine1("123 Test Street")
                .addressLine2("Apt 1")
                .city("Test City")
                .state("Test State")
                .countryId("EXISTING_COUNTRY_ID")
                .email("traveler2test@gmail.com")
                .build();

        StepVerifier.create(travelerRepository.save(traveler))
                .expectNextMatches(savedTraveler -> savedTraveler.getTravelerId().equals(EXISTING_TRAVELER_ID))
                .verifyComplete();

        StepVerifier.create(travelerRepository.save(traveler2))
                .expectNextMatches(savedTraveler -> savedTraveler.getTravelerId().equals("j0e91f6c-723e-43b9-812f-2f3d3bfb4091"))
                .verifyComplete();
    }

    @AfterEach
    void cleanUp() {
        StepVerifier.create(travelerRepository.deleteAll())
                .verifyComplete();
    }

    @Test
    void whenFindTravelerByTravelerId_withExistingId_thenReturnExistingTraveler() {
        StepVerifier.create(travelerRepository.findTravelerByTravelerId(EXISTING_TRAVELER_ID))
                .expectNextMatches(traveler ->
                        traveler.getTravelerId().equals(EXISTING_TRAVELER_ID) &&
                                traveler.getFirstName().equals("Test") &&
                                traveler.getLastName().equals("Traveler") &&
                                traveler.getAddressLine1().equals("123 Test Street") &&
                                traveler.getAddressLine2().equals("Apt 1") &&
                                traveler.getCity().equals("Test City") &&
                                traveler.getState().equals("Test State") &&
                                traveler.getEmail().equals("testtraveler@gmail.com") &&
                                traveler.getCountryId().equals(EXISTING_COUNTRY_ID)
                )
                .verifyComplete();
    }

    @Test
    void whenFindTravelerByTravelerId_withNonExistingId_thenReturnEmptyMono() {
        StepVerifier.create(travelerRepository.findTravelerByTravelerId(NON_EXISTING_TRAVELER_ID))
                .verifyComplete();
    }

    @Test
    void whenFindTravelerByFirstName_withExistingName_thenReturnAllMatchingTravelers() {
        StepVerifier.create(travelerRepository.findTravelerByFirstName("Test"))
                .expectNextMatches(traveler ->
                        traveler.getTravelerId().equals(EXISTING_TRAVELER_ID) &&
                                traveler.getFirstName().equals("Test") &&
                                traveler.getLastName().equals("Traveler") &&
                                traveler.getAddressLine1().equals("123 Test Street") &&
                                traveler.getAddressLine2().equals("Apt 1") &&
                                traveler.getCity().equals("Test City") &&
                                traveler.getState().equals("Test State")
                )
                .expectNextMatches(traveler ->
                        traveler.getTravelerId().equals("j0e91f6c-723e-43b9-812f-2f3d3bfb4091") &&
                                traveler.getFirstName().equals("Test") &&
                                traveler.getLastName().equals("Traveler2") &&
                                traveler.getAddressLine1().equals("123 Test Street") &&
                                traveler.getAddressLine2().equals("Apt 1") &&
                                traveler.getCity().equals("Test City") &&
                                traveler.getState().equals("Test State")
                )
                .verifyComplete();
    }

    @Test
    void whenFindTravelerByFirstName_withNonExistingName_thenReturnEmptyFlux() {
        StepVerifier.create(travelerRepository.findTravelerByFirstName("NonExistingName"))
                .verifyComplete();
    }
}

