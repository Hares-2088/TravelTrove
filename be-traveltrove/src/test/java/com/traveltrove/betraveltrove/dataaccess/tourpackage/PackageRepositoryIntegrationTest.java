package com.traveltrove.betraveltrove.dataaccess.tourpackage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;

@DataMongoTest
@ActiveProfiles("test")
class PackageRepositoryIntegrationTest {
    @Autowired
    private PackageRepository packageRepository;

    private final String NON_EXISTING_PACKAGE_ID = "non-existing-package-id";
    private final String EXISTING_PACKAGE_ID = "PK01";

    private final String EXISTING_AIRPORT_ID = "2702f60a-cf9e-46cf-a971-d76895b904e6";
    private final String EXISTING_TOUR_ID = "ad633b50-83d4-41f3-866a-26452bdd6f33";

    private final String NON_EXISTING_AIRPORT_ID = "non-existing-airport-id";
    private final String NON_EXISTING_TOUR_ID = "non-existing-tour-id";

    @BeforeEach
    void setUp() {
        Package pk = Package.builder()
                .id("1")
                .packageId(EXISTING_PACKAGE_ID)
                .name("Test Package")
                .description("Test Package Description")
                .airportId(EXISTING_AIRPORT_ID)
                .tourId(EXISTING_TOUR_ID)
                .priceSingle(100.0)
                .priceDouble(200.0)
                .priceTriple(300.0)
                // start date and end date in LocalDate format
                .startDate(LocalDate.of(2021, 12, 1))
                .endDate(LocalDate.of(2021, 12, 10))
                .totalSeats(130)
                .availableSeats(120)
                .packageStatus(PackageStatus.OPEN)
                .build();

        StepVerifier.create(packageRepository.save(pk))
                .expectNextMatches(savedPackage -> savedPackage.getPackageId().equals(EXISTING_PACKAGE_ID))
                .verifyComplete();
    }

    @AfterEach
    void cleanUp() {
        StepVerifier.create(packageRepository.deleteAll())
                .verifyComplete();
    }

    @Test
    void whenFindPackageByPackageId_withExistingId_thenReturnExistingPackage() {
        StepVerifier.create(packageRepository.findPackageByPackageId(EXISTING_PACKAGE_ID))
                .expectNextMatches(pk ->
                        pk.getPackageId().equals(EXISTING_PACKAGE_ID) &&
                                pk.getName().equals("Test Package") &&
                                pk.getDescription().equals("Test Package Description") &&
                                pk.getAirportId().equals(EXISTING_AIRPORT_ID) &&
                                pk.getTourId().equals(EXISTING_TOUR_ID) &&
                                pk.getPriceSingle().equals(100.0) &&
                                pk.getPriceDouble().equals(200.0) &&
                                pk.getPriceTriple().equals(300.0) &&
                                pk.getStartDate().equals(LocalDate.of(2021, 12, 1)) &&
                                pk.getEndDate().equals(LocalDate.of(2021, 12, 10)) &&
                                pk.getTotalSeats().equals(130) &&
                                pk.getAvailableSeats().equals(120) &&
                                pk.getPackageStatus().equals(PackageStatus.OPEN)
                )
                .verifyComplete();
    }

    @Test
    void whenFindPackageByPackageId_withNonExistingId_thenReturnEmptyMono() {
        StepVerifier.create(packageRepository.findPackageByPackageId(NON_EXISTING_PACKAGE_ID))
                .verifyComplete();
    }

    @Test
    void whenFindAllByTourId_withExistingId_thenReturnExistingPackage() {
        StepVerifier.create(packageRepository.findPackagesByTourId(EXISTING_TOUR_ID))
                .expectNextMatches(pk ->
                        pk.getPackageId().equals(EXISTING_PACKAGE_ID) &&
                                pk.getName().equals("Test Package") &&
                                pk.getDescription().equals("Test Package Description") &&
                                pk.getAirportId().equals(EXISTING_AIRPORT_ID) &&
                                pk.getTourId().equals(EXISTING_TOUR_ID) &&
                                pk.getPriceSingle().equals(100.0) &&
                                pk.getPriceDouble().equals(200.0) &&
                                pk.getPriceTriple().equals(300.0) &&
                                pk.getStartDate().equals(LocalDate.of(2021, 12, 1)) &&
                                pk.getEndDate().equals(LocalDate.of(2021, 12, 10)) &&
                                pk.getTotalSeats().equals(130) &&
                                pk.getAvailableSeats().equals(120) &&
                                pk.getPackageStatus().equals(PackageStatus.OPEN)
                )
                .verifyComplete();
    }

    @Test
    void whenFindAllByTourId_withNonExistingId_thenReturnEmptyFlux() {
        StepVerifier.create(packageRepository.findPackagesByTourId(NON_EXISTING_TOUR_ID))
                .verifyComplete();
    }

    @Test
    void whenFindAllByPackageStatus_withExistingStatus_thenReturnExistingPackage() {
        StepVerifier.create(packageRepository.findPackagesByPackageStatus(PackageStatus.OPEN))
                .expectNextMatches(pk ->
                        pk.getPackageId().equals(EXISTING_PACKAGE_ID) &&
                                pk.getName().equals("Test Package") &&
                                pk.getDescription().equals("Test Package Description") &&
                                pk.getAirportId().equals(EXISTING_AIRPORT_ID) &&
                                pk.getTourId().equals(EXISTING_TOUR_ID) &&
                                pk.getPriceSingle().equals(100.0) &&
                                pk.getPriceDouble().equals(200.0) &&
                                pk.getPriceTriple().equals(300.0) &&
                                pk.getStartDate().equals(LocalDate.of(2021, 12, 1)) &&
                                pk.getEndDate().equals(LocalDate.of(2021, 12, 10)) &&
                                pk.getTotalSeats().equals(130) &&
                                pk.getAvailableSeats().equals(120) &&
                                pk.getPackageStatus().equals(PackageStatus.OPEN)
                )
                .verifyComplete();
    }

    @Test
    void whenFindAllByPackageStatus_withNoResultStatus_thenReturnEmptyFlux() {
        StepVerifier.create(packageRepository.findPackagesByPackageStatus(PackageStatus.CLOSED)) // No data with status CLOSED
                .verifyComplete();
    }

}