package com.traveltrove.betraveltrove.dataaccess.airport;

import com.traveltrove.betraveltrove.presentation.mockserverconfigs.MockServerConfigAirportService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.*;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

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
