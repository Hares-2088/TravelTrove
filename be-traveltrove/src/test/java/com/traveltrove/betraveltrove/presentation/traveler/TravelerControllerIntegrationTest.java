package com.traveltrove.betraveltrove.presentation.traveler;

import com.traveltrove.betraveltrove.business.country.CountryService;
import com.traveltrove.betraveltrove.dataaccess.traveler.Traveler;
import com.traveltrove.betraveltrove.dataaccess.traveler.TravelerRepository;
import com.traveltrove.betraveltrove.presentation.country.CountryResponseModel;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TravelerControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TravelerRepository travelerRepository;

    @MockitoBean
    private CountryService countryService;

    private final String INVALID_TRAVELER_ID = "invalid-traveler-id";

    private final Traveler traveler1 = Traveler.builder()
            .id("1")
            .travelerId(UUID.randomUUID().toString())
            .seq(1)
            .firstName("John")
            .lastName("Doe")
            .addressLine1("123 Street")
            .addressLine2("Apt 1")
            .city("Cityville")
            .state("Stateville")
            .email("john.doe@example.com")
            .countryId("1")
            .build();

    private final Traveler traveler2 = Traveler.builder()
            .id("2")
            .travelerId(UUID.randomUUID().toString())
            .seq(2)
            .firstName("Jane")
            .lastName("Smith")
            .addressLine1("456 Avenue")
            .addressLine2("Suite 2")
            .city("Townsville")
            .state("Province")
            .email("jane.smith@example.com")
            .countryId("2")
            .build();

    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.openMocks(this); // Initialize @Mock and @InjectMocks

        Mockito.when(countryService.getCountryById("1"))
                .thenReturn(Mono.just(new CountryResponseModel("1", "Country 1", "image1.jpg")));

        Mockito.when(countryService.getCountryById("invalid-country-id"))
                .thenReturn(Mono.error(new NotFoundException("Country id not found: invalid-country-id")));

    }

    @BeforeEach
    public void setupDB() {
        travelerRepository.deleteAll().block();
        System.out.println("Database cleared.");
        travelerRepository.saveAll(Flux.just(traveler1, traveler2)).blockLast();
        System.out.println("Test data inserted.");
    }


    @Test
    void whenGetAllTravelers_thenReturnAllTravelers() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/travelers")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(Traveler.class)
                .hasSize(2)
                .value(travelers -> {
                    assertEquals(2, travelers.size());
                    assertEquals(traveler1.getFirstName(), travelers.get(0).getFirstName());
                    assertEquals(traveler2.getFirstName(), travelers.get(1).getFirstName());
                });

        StepVerifier.create(travelerRepository.findAll())
                .expectNextMatches(traveler -> {
                    System.out.println("Traveler1 Expected: " + traveler1 + ", Found: " + traveler);
                    return traveler.getFirstName().equals(traveler1.getFirstName());
                })
                .expectNextMatches(traveler -> {
                    System.out.println("Traveler2 Expected: " + traveler2 + ", Found: " + traveler);
                    return traveler.getFirstName().equals(traveler2.getFirstName());
                })
                .verifyComplete();
    }

    @Test
    void whenGetTravelerById_thenReturnTraveler() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/travelers/" + traveler1.getTravelerId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody(Traveler.class)
                .value(traveler -> assertEquals(traveler1.getFirstName(), traveler.getFirstName()));

        StepVerifier.create(travelerRepository.findTravelerByTravelerId(traveler1.getTravelerId()))
                .expectNextMatches(traveler -> traveler.getFirstName().equals(traveler1.getFirstName()))
                .verifyComplete();
    }

    @Test
    void whenGetTravelerByInvalidId_thenReturnNotFound() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/travelers/" + INVALID_TRAVELER_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenCreateTraveler_thenReturnCreatedTraveler() {
        Traveler newTraveler = Traveler.builder()
                .travelerId(UUID.randomUUID().toString())
                .seq(3)
                .firstName("Alice")
                .lastName("Johnson")
                .addressLine1("789 Boulevard")
                .addressLine2("Unit 3")
                .city("Metropolis")
                .state("Region")
                .email("alice.johnson@example.com")
                .countryId("1")
                .build();

        // Create the traveler via WebTestClient
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).post()
                .uri("/api/v1/travelers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newTraveler)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Traveler.class)
                .value(traveler -> {
                    assertNotNull(traveler);
                    assertEquals("Alice", traveler.getFirstName());
                    assertEquals("alice.johnson@example.com", traveler.getEmail());
                    assertEquals("Metropolis", traveler.getCity());
                    assertEquals("Region", traveler.getState());
                });

        // Validate that there is now 3 travelers in the database
        StepVerifier.create(travelerRepository.findAll().filter(t -> t.getEmail().equals("alice.johnson@example.com")))
                .expectNextMatches(traveler -> traveler.getFirstName().equals("Alice"))
                .verifyComplete();
    }

    @Test
    void whenUpdateTraveler_thenReturnUpdatedTraveler() {
        Traveler updatedTraveler = Traveler.builder()
                .firstName("Updated John")
                .lastName("Updated Doe")
                .addressLine1("New Address")
                .addressLine2("Updated Apt")
                .city("Updated City")
                .state("Updated State")
                .email("updated.john@example.com")
                .countryId("1")
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).put()
                .uri("/api/v1/travelers/{travelerId}", traveler1.getTravelerId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedTraveler)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Traveler.class)
                .value(traveler -> assertEquals("Updated John", traveler.getFirstName()));

        StepVerifier.create(travelerRepository.findById(traveler1.getId()))
                .expectNextMatches(traveler -> traveler.getFirstName().equals("Updated John"))
                .verifyComplete();
    }

    @Test
    void whenDeleteTraveler_thenTravelerIsDeleted() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).delete()
                .uri("/api/v1/travelers/{travelerId}", traveler1.getTravelerId())
                .exchange()
                .expectStatus().isOk();

        StepVerifier.create(travelerRepository.findById(traveler1.getId()))
                .verifyComplete();
    }

    @Test
    void whenDeleteTraveler_withInvalidId_thenReturnNotFound() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).delete()
                .uri("/api/v1/travelers/{travelerId}", INVALID_TRAVELER_ID)
                .exchange()
                .expectStatus().isNotFound();
    }
}
