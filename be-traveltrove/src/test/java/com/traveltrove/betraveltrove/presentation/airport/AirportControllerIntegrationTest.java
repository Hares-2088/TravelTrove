package com.traveltrove.betraveltrove.presentation.airport;

import com.traveltrove.betraveltrove.dataaccess.airport.Airport;
import com.traveltrove.betraveltrove.dataaccess.airport.AirportRepository;
import com.traveltrove.betraveltrove.dataaccess.city.City;
import com.traveltrove.betraveltrove.dataaccess.city.CityRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AirportControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private CityRepository cityRepository;

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

    private final City city1 = City.builder()
            .id("1")
            .cityId("1")
            .name("City 1")
            .countryId("Country 1")
            .build();

    private final City city2 = City.builder()
            .id("2")
            .cityId("City 2")
            .name("City 2")
            .countryId("Country 2")
            .build();

    @BeforeEach
    public void setupDB() {
        // Clear the collections
        cityRepository.deleteAll().block();
        airportRepository.deleteAll().block();

        // Insert cities
        cityRepository.saveAll(Flux.just(city1, city2))
                .doOnNext(city -> System.out.println("Inserted City: " + city))
                .blockLast();

        // Insert airports
        airportRepository.saveAll(Flux.just(airport1, airport2))
                .doOnNext(airport -> System.out.println("Inserted Airport: " + airport))
                .blockLast();

        // Verify cities
        StepVerifier.create(cityRepository.findAll())
                .expectNextCount(2)
                .verifyComplete();

        // Verify airports
        StepVerifier.create(airportRepository.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }


    @Test
    public void whenGetAllAirports_thenReturnAllAirports() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/airports")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(MediaType.TEXT_EVENT_STREAM_VALUE)
                .expectBodyList(AirportResponseModel.class)
                .hasSize(2)
                .value(airport -> {
                    assertEquals(airport1.getAirportId(), airport.get(0).getAirportId());
                    assertEquals(airport1.getName(), airport.get(0).getName());
                    assertEquals(airport1.getCityId(), airport.get(0).getCityId());

                    assertEquals(airport2.getAirportId(), airport.get(1).getAirportId());
                    assertEquals(airport2.getName(), airport.get(1).getName());
                    assertEquals(airport2.getCityId(), airport.get(1).getCityId());
                });
    }

    @Test
    public void whenGetAirportById_thenReturnAirport() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/airports/" + airport1.getAirportId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody(AirportResponseModel.class)
                .value(airport -> {
                    assertEquals(airport1.getAirportId(), airport.getAirportId());
                    assertEquals(airport1.getName(), airport.getName());
                    assertEquals(airport1.getCityId(), airport.getCityId());
                });
    }

    @Test
    public void whenGetAirportByInvalidId_thenReturnNotFound() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/airports/" + INVALID_AIRPORT_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void whenAddAirport_thenReturnCreatedAirport() {
        AirportRequestModel newAirport = AirportRequestModel.builder()
                .name("New Airport")
                .cityId(city1.getCityId())
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).post()
                .uri("/api/v1/airports")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newAirport)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AirportResponseModel.class)
                .value(savedAirport -> {
                    assertEquals(newAirport.getName(), savedAirport.getName());
                    assertEquals(newAirport.getCityId(), savedAirport.getCityId());
                });

        StepVerifier
                .create(airportRepository.findAll())
                .expectNextCount(3) // Including the newly added airport
                .verifyComplete();
    }

    @Test
    public void whenAddAirportWithInvalidCityId_thenReturnNotFound() {
        AirportRequestModel newAirport = AirportRequestModel.builder()
                .name("New Airport")
                .cityId("Invalid City")
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).post()
                .uri("/api/v1/airports")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newAirport)
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    public void whenUpdateAirport_thenReturnUpdatedAirport() {
        AirportRequestModel updatedAirport = AirportRequestModel.builder()
                .name("Updated Airport")
                .cityId(city2.getCityId())
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).put()
                .uri("/api/v1/airports/" + airport1.getAirportId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedAirport)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AirportResponseModel.class)
                .value(response -> {
                    assertEquals(updatedAirport.getName(), response.getName());
                    assertEquals(updatedAirport.getCityId(), response.getCityId());
                });
    }

    @Test
    public void whenDeleteAirport_thenAirportIsDeleted() {
        // Perform delete operation
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).delete()
                .uri("/api/v1/airports/" + airport1.getAirportId())
                .exchange()
                .expectStatus().isNoContent();

        StepVerifier
                .create(airportRepository.findById(airport1.getId()))
                .expectNextCount(0)
                .verifyComplete();
    }

}
