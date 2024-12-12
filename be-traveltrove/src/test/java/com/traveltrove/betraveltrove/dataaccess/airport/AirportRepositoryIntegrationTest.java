package com.traveltrove.betraveltrove.dataaccess.airport;

import com.traveltrove.betraveltrove.presentation.mockserverconfigs.MockServerConfigAirportService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@DataMongoTest
@ActiveProfiles("test")
public class AirportRepositoryIntegrationTest {
    @Autowired
    private AirportRepository airportRepository;

    private final Airport airport1 = Airport.builder()
            .id("1")
            .airportId("1")
            .name("Airport 1")
            .cityId("City 1")
            .build();

    private final Airport airport2 = Airport.builder()
            .id("2")
            .airportId("2")
            .name("Airport 2")
            .cityId("City 2")
            .build();


}
