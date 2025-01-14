package com.traveltrove.betraveltrove.presentation.tourpackage;

import com.traveltrove.betraveltrove.business.airport.AirportService;
import com.traveltrove.betraveltrove.business.tour.TourService;
import com.traveltrove.betraveltrove.business.tourpackage.PackageService;
import com.traveltrove.betraveltrove.business.tourpackage.PackageServiceImpl;
import com.traveltrove.betraveltrove.dataaccess.tourpackage.Package;
import com.traveltrove.betraveltrove.dataaccess.tourpackage.PackageRepository;
import com.traveltrove.betraveltrove.presentation.airport.AirportResponseModel;
import com.traveltrove.betraveltrove.presentation.mockserverconfigs.MockServerConfigPackageService;
import com.traveltrove.betraveltrove.presentation.tour.TourResponseModel;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static reactor.core.publisher.Mono.when;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PackageControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    PackageRepository packageRepository;

    private MockServerConfigPackageService mockServerConfigPackageService;

    @MockitoBean
    private AirportService airportService;

    @MockitoBean
    private TourService tourService;

    private final Package package1 = Package.builder()
            .packageId("1")
            .name("Silk Road Adventure")
            .description("Experience the historic wonders of the Silk Road with guided tours and more.")
            .startDate(LocalDate.of(2024, 10, 5))
            .endDate(LocalDate.of(2024, 10, 15))
            .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
            .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
            .priceSingle(2200.0)
            .priceDouble(2000.0)
            .priceTriple(1800.0)
            .totalSeats(130)
            .availableSeats(120)
            .build();

    private final Package package2 = Package.builder()
            .packageId("2")
            .name("Machu Picchu Trek")
            .description("Hike the Inca Trail to Machu Picchu with a group of fellow adventurers.")
            .startDate(LocalDate.of(2024, 11, 5))
            .endDate(LocalDate.of(2024, 11, 15))
            .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
            .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
            .priceSingle(1800.0)
            .priceDouble(1600.0)
            .priceTriple(1400.0)
            .totalSeats(130)
            .availableSeats(120)
            .build();
    @Autowired
    private PackageServiceImpl packageServiceImpl;

    @BeforeAll
    void startServer() {
        mockServerConfigPackageService = new MockServerConfigPackageService();
        mockServerConfigPackageService.startMockServer();
        mockServerConfigPackageService.registerGetAllPackagesByTourIdEndpoint(package1.getTourId());
        mockServerConfigPackageService.registerGetAllPackagesEndpoint();
        mockServerConfigPackageService.registerGetPackageByPackageIdEndpoint(package1.getPackageId());
        mockServerConfigPackageService.registerCreatePackageEndpoint();
        mockServerConfigPackageService.registerUpdatePackageEndpoint(package1.getPackageId());
        mockServerConfigPackageService.registerDeletePackageEndpoint(package1.getPackageId());
    }

    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.openMocks(this);

        Mockito.when(airportService.getAirportById(package1.getAirportId())).thenReturn(Mono.just(AirportResponseModel.builder()
                .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                .name("Los Angeles International Airport")
                .build()));

        Mockito.when(airportService.getAirportById("invalid")).thenReturn(Mono.error(new NotFoundException("Airport not found with ID: invalid")));

        Mockito.when(tourService.getTourByTourId(package1.getTourId())).thenReturn(Mono.just(TourResponseModel.builder()
                .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                .name("Silk Road Adventure")
                .description("Experience the historic wonders of the Silk Road with guided tours and more.")
                .build()));

        Mockito.when(tourService.getTourByTourId("invalid")).thenReturn(Mono.error(new NotFoundException("Tour not found with ID: invalid")));
    }

    @AfterAll
    void stopServer() {
        mockServerConfigPackageService.stopMockServer();
    }

    @BeforeEach
    void setUp() {
        packageRepository.deleteAll().block();
        packageRepository.save(package1).block();
        packageRepository.save(package2).block();
    }


    @Test
    void whenGetAllPackages_withValidTourId_thenReturnPackages() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).get()
                .uri("/api/v1/packages?tourId=" + package1.getTourId())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PackageResponseModel.class)
                .hasSize(2)
                .contains(PackageResponseModel.builder()
                        .packageId("1")
                        .name("Silk Road Adventure")
                        .description("Experience the historic wonders of the Silk Road with guided tours and more.")
                        .startDate(LocalDate.of(2024, 10, 5))
                        .endDate(LocalDate.of(2024, 10, 15))
                        .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                        .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                        .priceSingle(2200.0)
                        .priceDouble(2000.0)
                        .priceTriple(1800.0)
                        .totalSeats(130)
                        .availableSeats(120)
                        .build());
    }

    @Test
    void whenGetAllPackages_withInvalidTourId_thenReturnNotFound() {
        webTestClient.get()
                .uri("/api/v1/packages?tourId=invalid")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenGetAllPackages_thenReturnPackages() {
        webTestClient.get()
                .uri("/api/v1/packages")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PackageResponseModel.class)
                .hasSize(2)
                .contains(
                        PackageResponseModel.builder()
                                .packageId("1")
                                .name("Silk Road Adventure")
                                .description("Experience the historic wonders of the Silk Road with guided tours and more.")
                                .startDate(LocalDate.of(2024, 10, 5))
                                .endDate(LocalDate.of(2024, 10, 15))
                                .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                                .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                                .priceSingle(2200.0)
                                .priceDouble(2000.0)
                                .priceTriple(1800.0)
                                .totalSeats(130)
                                .availableSeats(120)
                                .build(),
                        PackageResponseModel.builder()
                                .packageId("2")
                                .name("Machu Picchu Trek")
                                .description("Hike the Inca Trail to Machu Picchu with a group of fellow adventurers.")
                                .startDate(LocalDate.of(2024, 11, 5))
                                .endDate(LocalDate.of(2024, 11, 15))
                                .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                                .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                                .priceSingle(1800.0)
                                .priceDouble(1600.0)
                                .priceTriple(1400.0)
                                .totalSeats(130)
                                .availableSeats(120)
                                .build()
                );
    }

    @Test
    void whenGetPackageByPackageId_withValidPackageId_thenReturnPackage() {
        webTestClient.get()
                .uri("/api/v1/packages/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PackageResponseModel.class)
                .isEqualTo(PackageResponseModel.builder()
                        .packageId("1")
                        .name("Silk Road Adventure")
                        .description("Experience the historic wonders of the Silk Road with guided tours and more.")
                        .startDate(LocalDate.of(2024, 10, 5))
                        .endDate(LocalDate.of(2024, 10, 15))
                        .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                        .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                        .priceSingle(2200.0)
                        .priceDouble(2000.0)
                        .priceTriple(1800.0)
                        .totalSeats(130)
                        .availableSeats(120)
                        .build());
    }

    @Test
    void whenGetPackageByPackageId_withInvalidPackageId_thenReturnNotFound() {
        webTestClient.get()
                .uri("/api/v1/packages/invalid")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenCreatePackage_withValidPackageRequestModel_thenReturnCreatedPackage() {
        PackageRequestModel packageRequestModel = PackageRequestModel.builder()
                .name("New Package")
                .description("New Package Description")
                .startDate(LocalDate.of(2024, 12, 5))
                .endDate(LocalDate.of(2024, 12, 15))
                .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                .priceSingle(2000.0)
                .priceDouble(1800.0)
                .priceTriple(1600.0)
                .totalSeats(130)
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).post()
                .uri("/api/v1/packages")
                .body(Mono.just(packageRequestModel), PackageRequestModel.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PackageResponseModel.class)
                .consumeWith(response -> {
                    PackageResponseModel actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertNotNull(actualResponse.getPackageId()); // Ensure packageId is generated
                    assertEquals("New Package", actualResponse.getName());
                    assertEquals("New Package Description", actualResponse.getDescription());
                    assertEquals(LocalDate.of(2024, 12, 5), actualResponse.getStartDate());
                    assertEquals(LocalDate.of(2024, 12, 15), actualResponse.getEndDate());
                    assertEquals("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634", actualResponse.getAirportId());
                    assertEquals("6a237fda-4924-4c73-a6df-73c1e0c37af2", actualResponse.getTourId());
                    assertEquals(2000.0, actualResponse.getPriceSingle());
                    assertEquals(1800.0, actualResponse.getPriceDouble());
                    assertEquals(1600.0, actualResponse.getPriceTriple());
                    assertEquals(130, actualResponse.getTotalSeats());
                    assertEquals(130, actualResponse.getAvailableSeats());
                });
    }


    @Test
    void whenCreatePackage_withInvalidAirportId_thenReturnNotFound() {
        PackageRequestModel packageRequestModel = PackageRequestModel.builder()
                .name("New Package")
                .description("New Package Description")
                .startDate(LocalDate.of(2024, 12, 5))
                .endDate(LocalDate.of(2024, 12, 15))
                .airportId("invalid")
                .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                .priceSingle(2000.0)
                .priceDouble(1800.0)
                .priceTriple(1600.0)
                .totalSeats(130)
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).post()
                .uri("/api/v1/packages")
                .body(Mono.just(packageRequestModel), PackageRequestModel.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenCreatePackage_withInvalidTourId_thenReturnNotFound() {
        PackageRequestModel packageRequestModel = PackageRequestModel.builder()
                .name("New Package")
                .description("New Package Description")
                .startDate(LocalDate.of(2024, 12, 5))
                .endDate(LocalDate.of(2024, 12, 15))
                .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                .tourId("invalid")
                .priceSingle(2000.0)
                .priceDouble(1800.0)
                .priceTriple(1600.0)
                .totalSeats(130)
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).post()
                .uri("/api/v1/packages")
                .body(Mono.just(packageRequestModel), PackageRequestModel.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUpdatePackage_withValidPackageRequestModel_thenReturnUpdatedPackage() {
        PackageRequestModel packageRequestModel = PackageRequestModel.builder()
                .name("Updated Package")
                .description("Updated Package Description")
                .startDate(LocalDate.of(2024, 12, 5))
                .endDate(LocalDate.of(2024, 12, 15))
                .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                .priceSingle(2000.0)
                .priceDouble(1800.0)
                .priceTriple(1600.0)
                .totalSeats(130)
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).put()
                .uri("/api/v1/packages/1")
                .body(Mono.just(packageRequestModel), PackageRequestModel.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PackageResponseModel.class)
                .isEqualTo(PackageResponseModel.builder()
                        .packageId("1")
                        .name("Updated Package")
                        .description("Updated Package Description")
                        .startDate(LocalDate.of(2024, 12, 5))
                        .endDate(LocalDate.of(2024, 12, 15))
                        .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                        .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                        .priceSingle(2000.0)
                        .priceDouble(1800.0)
                        .priceTriple(1600.0)
                        .totalSeats(130)
                        .availableSeats(120)
                        .build());
    }

    @Test
    void whenUpdatePackage_withInvalidAirportId_thenReturnNotFound() {
        PackageRequestModel packageRequestModel = PackageRequestModel.builder()
                .name("Updated Package")
                .description("Updated Package Description")
                .startDate(LocalDate.of(2024, 12, 5))
                .endDate(LocalDate.of(2024, 12, 15))
                .airportId("invalid")
                .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                .priceSingle(2000.0)
                .priceDouble(1800.0)
                .priceTriple(1600.0)
                .totalSeats(130)
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).put()
                .uri("/api/v1/packages/1")
                .body(Mono.just(packageRequestModel), PackageRequestModel.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUpdatePackage_withInvalidTourId_thenReturnNotFound() {
        PackageRequestModel packageRequestModel = PackageRequestModel.builder()
                .name("Updated Package")
                .description("Updated Package Description")
                .startDate(LocalDate.of(2024, 12, 5))
                .endDate(LocalDate.of(2024, 12, 15))
                .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                .tourId("invalid")
                .priceSingle(2000.0)
                .priceDouble(1800.0)
                .priceTriple(1600.0)
                .totalSeats(130)
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).put()
                .uri("/api/v1/packages/1")
                .body(Mono.just(packageRequestModel), PackageRequestModel.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUpdatePackage_withInvalidPackageId_thenReturnNotFound() {
        PackageRequestModel packageRequestModel = PackageRequestModel.builder()
                .name("Updated Package")
                .description("Updated Package Description")
                .startDate(LocalDate.of(2024, 12, 5))
                .endDate(LocalDate.of(2024, 12, 15))
                .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                .priceSingle(2000.0)
                .priceDouble(1800.0)
                .priceTriple(1600.0)
                .totalSeats(130)
                .build();

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).put()
                .uri("/api/v1/packages/invalid")
                .body(Mono.just(packageRequestModel), PackageRequestModel.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenDeletePackage_withValidPackageId_thenReturnDeletedPackage() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).delete()
                .uri("/api/v1/packages/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PackageResponseModel.class)
                .isEqualTo(PackageResponseModel.builder()
                        .packageId("1")
                        .name("Silk Road Adventure")
                        .description("Experience the historic wonders of the Silk Road with guided tours and more.")
                        .startDate(LocalDate.of(2024, 10, 5))
                        .endDate(LocalDate.of(2024, 10, 15))
                        .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                        .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                        .priceSingle(2200.0)
                        .priceDouble(2000.0)
                        .priceTriple(1800.0)
                        .totalSeats(130)
                        .availableSeats(120)
                        .build());
    }

    @Test
    void whenDeletePackage_withInvalidPackageId_thenReturnNotFound() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).delete()
                .uri("/api/v1/packages/invalid")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenDecreaseAvailableSeats_thenAvailableSeatsDecreased() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).patch()
                .uri("/api/v1/packages/1/decreaseAvailableSeats?quantity=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PackageResponseModel.class)
                .consumeWith(response -> {
                    PackageResponseModel actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(110, actualResponse.getAvailableSeats());
                });
    }

    @Test
    void whenDecreaseAvailableSeats_withInvalidPackageId_thenReturnNotFound() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).patch()
                .uri("/api/v1/packages/invalid/decreaseAvailableSeats?quantity=10")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenDecreaseAvailableSeats_withInvalidQuantity_thenReturnBadRequest() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).patch()
                .uri("/api/v1/packages/1/decreaseAvailableSeats?quantity=-10")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenDecreaseAvailableSeats_withQuantityGreaterThanAvailableSeats_thenReturnBadRequest() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).patch()
                .uri("/api/v1/packages/1/decreaseAvailableSeats?quantity=200")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenIncreaseAvailableSeats_thenAvailableSeatsIncreased() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).patch()
                .uri("/api/v1/packages/1/increaseAvailableSeats?quantity=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PackageResponseModel.class)
                .consumeWith(response -> {
                    PackageResponseModel actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(130, actualResponse.getAvailableSeats());
                });
    }

    @Test
    void whenIncreaseAvailableSeats_withInvalidPackageId_thenReturnNotFound() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).patch()
                .uri("/api/v1/packages/invalid/increaseAvailableSeats?quantity=10")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenIncreaseAvailableSeats_withInvalidQuantity_thenReturnBadRequest() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).patch()
                .uri("/api/v1/packages/1/increaseAvailableSeats?quantity=-10")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenIncreaseAvailableSeats_withQuantityGreaterThanTotalSeats_thenReturnBadRequest() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).patch()
                .uri("/api/v1/packages/1/increaseAvailableSeats?quantity=200")
                .exchange()
                .expectStatus().isBadRequest();
    }
}