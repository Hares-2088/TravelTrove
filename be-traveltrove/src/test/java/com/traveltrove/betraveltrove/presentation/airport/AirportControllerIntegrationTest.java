package com.traveltrove.betraveltrove.presentation.airport;

import com.traveltrove.betraveltrove.dataaccess.airport.Airport;
import com.traveltrove.betraveltrove.dataaccess.airport.AirportRepository;
import com.traveltrove.betraveltrove.presentation.mockserverconfigs.MockServerConfigAirportService;
import org.junit.jupiter.api.*;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AirportControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private MockServerConfigAirportService mockServerConfigAirportService;

    @Autowired
    private AirportRepository airportRepository;

    private final String INVALID_AIRPORT_ID = "invalid-airport-id";


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

    @BeforeAll
    public void startServer() {
        mockServerConfigAirportService = new MockServerConfigAirportService();
        mockServerConfigAirportService.startMockServer();
        mockServerConfigAirportService.registerGetAirportByIdEndpoint(airport1);
        mockServerConfigAirportService.registerGetAirportByIdEndpoint(airport2);
        mockServerConfigAirportService.registerGetAirportByInvalidIdEndpoint(INVALID_AIRPORT_ID);
    }

    @AfterAll
    public void stopServer() {
        mockServerConfigAirportService.stopMockServer();
    }

    @BeforeEach
    public void setupDB() {
        Publisher<Airport> setupDB = airportRepository.deleteAll()
                .thenMany(Flux.just(airport1, airport2))
                .flatMap(airportRepository::save);

        StepVerifier.create(setupDB)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void whenGetAllAirports
    )



}
