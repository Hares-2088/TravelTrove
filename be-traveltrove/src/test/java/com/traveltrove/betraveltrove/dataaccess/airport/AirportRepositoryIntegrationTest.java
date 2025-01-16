package com.traveltrove.betraveltrove.dataaccess.airport;

import org.junit.jupiter.api.*;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.UUID;

@DataMongoTest
@ActiveProfiles("test")
public class AirportRepositoryIntegrationTest {

    @Autowired
    private AirportRepository airportRepository;
    private final String airportId = UUID.randomUUID().toString();


    @BeforeEach
    void setup() {
        Airport testAirport = Airport.builder()
                .id("1")
                .airportId(airportId)
                .name("Test Airport")
                .cityId("Test City")
                .build();

        StepVerifier.create(airportRepository.save(testAirport))
                .expectNextMatches(savedAirport -> savedAirport.getAirportId().equals(airportId))
                .verifyComplete();
    }

    @AfterEach
    void cleanup() {
        StepVerifier.create(airportRepository.deleteAll())
                .verifyComplete();
    }
    @Test
    public void whenGetAirportByAirportId_withExistingId_thenReturnExistingAirport() {
        Publisher<Airport> foundAirport = airportRepository.findAirportByAirportId(airportId);

        StepVerifier.create(foundAirport)
                .expectNextMatches(airport ->
                        airport.getAirportId().equals(airportId) &&
                                airport.getName().equals("Test Airport") &&
                                airport.getCityId().equals("Test City")
                )
                .verifyComplete();
    }

    @Test
    public void whenGetAirportByAirportId_withNonExistingId_thenReturnEmptyMono() {
        Publisher<Airport> foundAirport = airportRepository.findAirportByAirportId("INVALID_ID");

        StepVerifier.create(foundAirport)
                .verifyComplete();
    }

}
