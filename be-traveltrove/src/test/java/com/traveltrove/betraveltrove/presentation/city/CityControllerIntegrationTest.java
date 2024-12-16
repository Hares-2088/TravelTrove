package com.traveltrove.betraveltrove.presentation.city;

import com.traveltrove.betraveltrove.dataaccess.city.City;
import com.traveltrove.betraveltrove.dataaccess.city.CityRepository;
import com.traveltrove.betraveltrove.presentation.mockserverconfigs.MockServerConfigCityService;
import org.junit.jupiter.api.*;
import org.reactivestreams.Publisher;
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
public class CityControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CityRepository cityRepository;

    private MockServerConfigCityService mockServerConfigCityService;

    private final String INVALID_CITY_ID = "invalid-city-id";

    private final City city1 = City.builder()
            .id("1")
            .cityId("1")
            .name("City 1")
            .countryId("1")
            .build();

    private final City city2 = City.builder()
            .id("2")
            .cityId("2")
            .name("City 2")
            .countryId("2")
            .build();

    @BeforeAll
    public void startServer() {
        mockServerConfigCityService = new MockServerConfigCityService();
        mockServerConfigCityService.startMockServer();
        mockServerConfigCityService.registerGetCityByIdEndpoint(city1);
        mockServerConfigCityService.registerGetCityByIdEndpoint(city2);
        mockServerConfigCityService.registerGetCityByInvalidIdEndpoint(INVALID_CITY_ID);
    }

    @AfterAll
    public void stopServer() {
        mockServerConfigCityService.stopMockServer();
    }

    @BeforeEach
    public void setupDB() {
        Publisher<City> cities = cityRepository.deleteAll()
                .thenMany(Flux.just(city1, city2))
                .flatMap(cityRepository::save);

        StepVerifier.create(cities)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void whenGetCityById_thenReturnCity() {
        webTestClient.get()
                .uri("/api/v1/cities/{cityId}", city1.getCityId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(City.class)
                .value(city -> assertEquals(city1.getName(), city.getName()));

        StepVerifier.create(cityRepository.findCityByCityId(city1.getId()))
                .expectNextMatches(city -> city.getName().equals(city1.getName()))
                .verifyComplete();
    }

    @Test
    void whenGetCityByInvalidId_thenReturnNotFound() {
        webTestClient.get()
                .uri("/api/v1/cities/{cityId}", INVALID_CITY_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenGetAllCities_thenReturnAllCities() {
        webTestClient.get()
                .uri("/api/v1/cities")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(City.class)
                .hasSize(2)
                .value(cities -> {
                    assertEquals(2, cities.size());
                    assertEquals(city1.getName(), cities.get(0).getName());
                    assertEquals(city2.getName(), cities.get(1).getName());
                });

        StepVerifier.create(cityRepository.findAll())
                .expectNextMatches(city -> city.getName().equals(city1.getName()))
                .expectNextMatches(city -> city.getName().equals(city2.getName()))
                .verifyComplete();
    }

    @Test
    void whenAddCity_thenReturnCreatedCity() {
        City newCity = City.builder()
                .cityId("3")
                .name("New City")
                .countryId("3")
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).post()
                .uri("/api/v1/cities")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newCity)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(City.class)
                .value(savedCity -> {
                    assertEquals(newCity.getName(), savedCity.getName());
                    assertEquals(newCity.getCountryId(), savedCity.getCountryId());
                });

        StepVerifier.create(cityRepository.findAll())
                .expectNextMatches(city -> city.getName().equals(city1.getName()))
                .expectNextMatches(city -> city.getName().equals(city2.getName()))
                .expectNextMatches(city -> city.getName().equals("New City"))
                .verifyComplete();
    }

    @Test
    void whenUpdateCity_thenReturnUpdatedCity() {
        City updatedCity = City.builder()
                .name("Updated City")
                .countryId("3")
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).put()
                .uri("/api/v1/cities/{cityId}", city1.getCityId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedCity)
                .exchange()
                .expectStatus().isOk()
                .expectBody(City.class)
                .value(savedCity -> {
                    assertEquals(updatedCity.getName(), savedCity.getName());
                    assertEquals(updatedCity.getCountryId(), savedCity.getCountryId());
                });

        StepVerifier.create(cityRepository.findCityByCityId(city1.getId()))
                .expectNextMatches(city -> city.getName().equals("Updated City"))
                .verifyComplete();
    }

    @Test
    void whenDeleteCity_thenReturnNoContent() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).delete()
                .uri("/api/v1/cities/{cityId}", city1.getCityId())
                .exchange()
                .expectStatus().isNoContent();

        StepVerifier.create(cityRepository.findCityByCityId(city1.getId()))
                .verifyComplete();
    }

    @Test
    void whenUpdateCity_withInvalidId_thenReturnNotFound() {
        City updatedCity = City.builder()
                .name("Updated City")
                .countryId("3")
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).put()
                .uri("/api/v1/cities/{cityId}", INVALID_CITY_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedCity)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenDeleteCity_withInvalidId_thenReturnNotFound() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).delete()
                .uri("/api/v1/cities/{cityId}", INVALID_CITY_ID)
                .exchange()
                .expectStatus().isNotFound();
    }

}
