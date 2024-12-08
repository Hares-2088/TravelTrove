package com.traveltrove.betraveltrove.presentation;

import com.traveltrove.betraveltrove.dataaccess.country.Country;
import com.traveltrove.betraveltrove.dataaccess.country.CountryRepository;
import com.traveltrove.betraveltrove.presentation.mockserverconfigs.MockServerConfigCountryService;
import org.junit.jupiter.api.*;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountryControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CountryRepository countryRepository;

    private MockServerConfigCountryService mockServerConfigCountryService;

    private final String INVALID_COUNTRY_ID = "invalid-country-id";

    private final Country country1 = Country.builder()
            .id("1")
            .countryId(UUID.randomUUID().toString())
            .name("Country 1")
            .image("image1.jpg")
            .build();

    private final Country country2 = Country.builder()
            .id("2")
            .countryId(UUID.randomUUID().toString())
            .name("Country 2")
            .image("image2.jpg")
            .build();

    @BeforeAll
    public void startServer() {
        mockServerConfigCountryService = new MockServerConfigCountryService();
        mockServerConfigCountryService.startMockServer();
        mockServerConfigCountryService.registerGetCountryByIdEndpoint(country1);
        mockServerConfigCountryService.registerGetCountryByIdEndpoint(country2);
        mockServerConfigCountryService.registerGetCountryByInvalidIdEndpoint(INVALID_COUNTRY_ID);
    }

    @AfterAll
    public void stopServer() {
        mockServerConfigCountryService.stopMockServer();
    }

    @BeforeEach
    public void setupDB() {
        Publisher<Country> setupDB = countryRepository.deleteAll()
                .thenMany(Flux.just(country1, country2))
                .flatMap(countryRepository::save);

        StepVerifier.create(setupDB)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void whenGetAllCountries_thenReturnAllCountries() {
        webTestClient.get()
                .uri("/api/v1/countries")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(Country.class)
                .hasSize(2)
                .value(countries -> {
                    assertEquals(2, countries.size());
                    assertEquals(country1.getName(), countries.get(0).getName());
                    assertEquals(country2.getName(), countries.get(1).getName());
                });

        StepVerifier.create(countryRepository.findAll())
                .expectNextMatches(country -> country.getName().equals(country1.getName()))
                .expectNextMatches(country -> country.getName().equals(country2.getName()))
                .verifyComplete();
    }

    @Test
    void whenAddCountry_thenReturnCreatedCountry() {
        Country newCountry = Country.builder()
                .countryId(UUID.randomUUID().toString())
                .name("New Country")
                .image("new_image.jpg")
                .build();

        webTestClient.post()
                .uri("/api/v1/countries")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newCountry)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Country.class)
                .value(savedCountry -> {
                    assertNotNull(savedCountry);
                    assertEquals(newCountry.getName(), savedCountry.getName());
                    assertEquals(newCountry.getImage(), savedCountry.getImage());
                });

        StepVerifier.create(countryRepository.findAll())
                .expectNextMatches(country -> country.getName().equals(country1.getName()))
                .expectNextMatches(country -> country.getName().equals(country2.getName()))
                .expectNextMatches(country -> country.getName().equals("New Country"))
                .verifyComplete();
    }

    @Test
    void whenUpdateCountry_thenReturnUpdatedCountry() {
        Country updatedCountry = Country.builder()
                .name("Updated Country")
                .image("updated_image.jpg")
                .build();

        webTestClient.put()
                .uri("/api/v1/countries/{countryId}", country1.getCountryId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedCountry)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Country.class)
                .value(response -> assertEquals("Updated Country", response.getName()));

        StepVerifier.create(countryRepository.findById(country1.getId()))
                .expectNextMatches(country -> country.getName().equals("Updated Country"))
                .verifyComplete();
    }

    @Test
    void whenDeleteCountry_thenReturnNoContent() {
        webTestClient.delete()
                .uri("/api/v1/countries/{countryId}", country1.getCountryId())
                .exchange()
                .expectStatus().isNoContent();

        StepVerifier.create(countryRepository.findById(country1.getId()))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void whenUpdateCountry_withInvalidId_thenReturnNotFound() {
        Country updatedCountry = Country.builder()
                .name("Updated Country")
                .image("updated_image.jpg")
                .build();

        webTestClient.put()
                .uri("/api/v1/countries/{countryId}", INVALID_COUNTRY_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedCountry)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Country id not found: " + INVALID_COUNTRY_ID);
    }

    @Test
    void whenDeleteCountry_withInvalidId_thenReturnNotFound() {
        webTestClient.delete()
                .uri("/api/v1/countries/{countryId}", INVALID_COUNTRY_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Country id not found: " + INVALID_COUNTRY_ID);
    }

    @Test
    void whenGetCountryById_withNonExistingId_thenReturnNotFound() {
        webTestClient.get()
                .uri("/api/v1/countries/{countryId}", "non-existing-id")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Country id not found: non-existing-id");
    }

}
