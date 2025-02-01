package com.traveltrove.betraveltrove.business.tourpackage;

import com.traveltrove.betraveltrove.business.airport.AirportService;
import com.traveltrove.betraveltrove.business.booking.BookingService;
import com.traveltrove.betraveltrove.business.tour.TourService;
import com.traveltrove.betraveltrove.dataaccess.airport.Airport;
import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import com.traveltrove.betraveltrove.dataaccess.tour.TourRepository;
import com.traveltrove.betraveltrove.dataaccess.tourpackage.Package;
import com.traveltrove.betraveltrove.dataaccess.tourpackage.PackageRepository;
import com.traveltrove.betraveltrove.presentation.airport.AirportResponseModel;
import com.traveltrove.betraveltrove.presentation.tour.TourResponseModel;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageRequestModel;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageResponseModel;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PackageServiceUnitTest {

    @InjectMocks
    private PackageServiceImpl packageService;

    @Mock
    private PackageRepository packageRepository;

    @Mock
    private AirportService airportService;

    @Mock
    private TourService tourService;

    @Mock
    private BookingService bookingService;

    String sampleNotificationMessage = "A sample notification message";

    PackageResponseModel packageResponseModel1 = PackageResponseModel.builder()
            .packageId("1")
            .name("Sample Package")
            .description("A sample package description")
            .startDate(LocalDate.of(2024, 10, 5))
            .endDate(LocalDate.of(2024, 10, 15))
            .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
            .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
            .priceSingle(2200.0)
            .priceDouble(2000.0)
            .priceTriple(1800.0)
            .totalSeats(130)
            .availableSeats(130)
            .build();

    Package package1 = Package.builder()
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

    Package package2 = Package.builder()
        .packageId("2")
        .name("Indian Heritage Exploration")
        .description("Explore India’s vibrant heritage and landmarks with this cultural package.")
        .startDate(LocalDate.of(2024, 8, 1))
        .endDate(LocalDate.of(2024, 8, 10))
        .airportId("47c8f2e7-3d6b-402c-93b7-8a92ef43e6ab")
        .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
        .priceSingle(1700.0)
        .priceDouble(1500.0)
        .priceTriple(1300.0)
        .totalSeats(130)
        .availableSeats(130)
        .build();

    @Test
    void whenGetPackageByPackageId_withExistingId_thenReturnPackage() {
        String packageId = "1";

        when(packageRepository.findPackageByPackageId(packageId))
                .thenReturn(Mono.just(package1));

        Mono<PackageResponseModel> result = packageService.getPackageByPackageId(packageId);

        StepVerifier.create(result)
                .expectNextMatches(packageResponseModel -> packageResponseModel.getPackageId().equals(packageId))
                .verifyComplete();
    }

    @Test
    void whenGetPackageByPackageId_withNonExistingId_thenReturnNotFound() {
        String packageId = "1";

        when(packageRepository.findPackageByPackageId(packageId))
                .thenReturn(Mono.empty());

        StepVerifier.create(packageService.getPackageByPackageId(packageId))
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException)
                .verify();
    }

    @Test
    void whenGetPackages_withValidTourId_thenReturnPackages() {
        String tourId = "6a237fda-4924-4c73-a6df-73c1e0c37af2";

        TourResponseModel tourResponseModel = TourResponseModel.builder()
                .tourId(tourId)
                .description("A tour description")
                .name("A tour name")
                .build();

        // Mock the repository calls
        when(tourService.getTourByTourId(tourId)).thenReturn(Mono.just(tourResponseModel));
        when(packageRepository.findPackagesByTourId(tourId)).thenReturn(Flux.just(package1, package2));


        // Call the service
        Flux<PackageResponseModel> result = packageService.getAllPackages(tourId);

        // Verify the results
        StepVerifier.create(result)
                .expectNextMatches(packageResponseModel -> packageResponseModel.getPackageId().equals("1"))
                .expectNextMatches(packageResponseModel -> packageResponseModel.getPackageId().equals("2"))
                .verifyComplete();

        // Verify that findAll() is never called
        verify(packageRepository, never()).findAll();
    }


    @Test
    void whenGetPackages_withInvalidTourId_thenReturnNotFound() {
        String tourId = "invalid-tour-id";

        // Mock the repository and service
        when(tourService.getTourByTourId(tourId)).thenReturn(Mono.empty());

        // Call the service
        Flux<PackageResponseModel> result = packageService.getAllPackages(tourId);

        // Verify the results
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException)
                .verify();

        verify(packageRepository, never()).findAll();
    }

    @Test
    void whenGetAllPackages_withNoTourId_thenReturnAllPackages() {
        when(packageRepository.findAll())
                .thenReturn(Flux.just(package1, package2));

        Flux<PackageResponseModel> result = packageService.getAllPackages(null);

        StepVerifier.create(result)
                .expectNextMatches(packageResponseModel -> packageResponseModel.getPackageId().equals("1"))
                .expectNextMatches(packageResponseModel -> packageResponseModel.getPackageId().equals("2"))
                .verifyComplete();
    }

    @Test
    void whenCreatePackage_withValidRequest_thenReturnCreatedPackage() {
        PackageRequestModel packageRequestModel = PackageRequestModel.builder()
                .name("Sample Package")
                .description("A sample package description")
                .startDate(LocalDate.of(2024, 10, 5))
                .endDate(LocalDate.of(2024, 10, 15))
                .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                .tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                .priceSingle(2200.0)
                .priceDouble(2000.0)
                .priceTriple(1800.0)
                .totalSeats(130)
                .build();

        when(tourService.getTourByTourId(packageRequestModel.getTourId()))
                .thenReturn(Mono.just(TourResponseModel.builder()
                        .tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                        .build()));

        when(airportService.getAirportById(packageRequestModel.getAirportId()))
                .thenReturn(Mono.just(AirportResponseModel.builder()
                        .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                        .build()));

        when(packageRepository.save(any(Package.class)))
                .thenReturn(Mono.just(package1));

        Mono<PackageResponseModel> result = packageService.createPackage(Mono.just(packageRequestModel));

        StepVerifier.create(result)
                .expectNextMatches(packageResponseModel ->
                        packageResponseModel.getPackageId().equals("1") &&
                                packageResponseModel.getName().equals("Silk Road Adventure") &&
                                packageResponseModel.getDescription().equals("Experience the historic wonders of the Silk Road with guided tours and more.") &&
                                packageResponseModel.getStartDate().equals(LocalDate.of(2024, 10, 5)) &&
                                packageResponseModel.getEndDate().equals(LocalDate.of(2024, 10, 15)) &&
                                packageResponseModel.getAirportId().equals("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634") &&
                                packageResponseModel.getTourId().equals("6a237fda-4924-4c73-a6df-73c1e0c37af2") &&
                                packageResponseModel.getPriceSingle().equals(2200.0) &&
                                packageResponseModel.getPriceDouble().equals(2000.0) &&
                                packageResponseModel.getPriceTriple().equals(1800.0) &&
                                packageResponseModel.getTotalSeats().equals(130) &&
                                packageResponseModel.getAvailableSeats().equals(120)
                        )
                .verifyComplete();
    }


    @Test
    void whenCreatePackage_withInvalidTourId_thenReturnNotFound() {
        PackageRequestModel packageRequestModel = PackageRequestModel.builder()
                .name("Sample Package")
                .description("A sample package description")
                .startDate(LocalDate.of(2024, 10, 5))
                .endDate(LocalDate.of(2024, 10, 15))
                .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                .tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                .priceSingle(2200.0)
                .priceDouble(2000.0)
                .priceTriple(1800.0)
                .totalSeats(130)
                .build();

        // Mock the `getTourByTourId` to return an empty Mono
        when(tourService.getTourByTourId(packageRequestModel.getTourId()))
                .thenReturn(Mono.empty());

        // Mock the `getAirportById` to return a valid Mono
        when(airportService.getAirportById(packageRequestModel.getAirportId()))
                .thenReturn(Mono.just(AirportResponseModel.builder()
                        .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634").build()));

        StepVerifier.create(packageService.createPackage(Mono.just(packageRequestModel)))
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Tour not found: " + packageRequestModel.getTourId()))
                .verify();
    }


    @Test
    void whenCreatePackage_withInvalidAirportId_thenReturnNotFound() {
        PackageRequestModel packageRequestModel = PackageRequestModel.builder()
                .name("Sample Package")
                .description("A sample package description")
                .startDate(LocalDate.of(2024, 10, 5))
                .endDate(LocalDate.of(2024, 10, 15))
                .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                .tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                .priceSingle(2200.0)
                .priceDouble(2000.0)
                .priceTriple(1800.0)
                .totalSeats(130)
                .build();

        when(tourService.getTourByTourId(packageRequestModel.getTourId()))
                .thenReturn(Mono.just(TourResponseModel.builder().tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e").build()));

        when(airportService.getAirportById(packageRequestModel.getAirportId()))
                .thenReturn(Mono.empty());

        StepVerifier.create(packageService.createPackage(Mono.just(packageRequestModel)))
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException)
                .verify();
    }

//    @Test
//    void whenUpdatePackage_withExistingId_thenReturnUpdatedPackage() {
//        String packageId = "1";
//        PackageRequestModel packageRequestModel = PackageRequestModel.builder()
//                .name("Silk Road Adventure")
//                .description("A sample package description")
//                .startDate(LocalDate.of(2024, 10, 5))
//                .endDate(LocalDate.of(2024, 10, 15))
//                .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
//                .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
//                .priceSingle(2200.0)
//                .priceDouble(2000.0)
//                .priceTriple(1800.0)
//                .totalSeats(130)
//                .build();
//
//        Package package1 = Package.builder()
//                .packageId("1")
//                .name("Old Package Name")
//                .description("Old Description")
//                .startDate(LocalDate.of(2024, 1, 1))
//                .endDate(LocalDate.of(2024, 1, 10))
//                .airportId("old-airport-id")
//                .tourId("old-tour-id")
//                .priceSingle(1000.0)
//                .priceDouble(900.0)
//                .priceTriple(800.0)
//                .build();
//
//        when(packageRepository.findPackageByPackageId(packageId))
//                .thenReturn(Mono.just(package1));
//
//        when(tourService.getTourByTourId(packageRequestModel.getTourId()))
//                .thenReturn(Mono.just(TourResponseModel.builder().tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2").build()));
//
//        when(airportService.getAirportById(packageRequestModel.getAirportId()))
//                .thenReturn(Mono.just(AirportResponseModel.builder().airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634").build()));
//
//        when(packageRepository.save(any(Package.class)))
//                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
//
//        Mono<PackageResponseModel> result = packageService.updatePackage(packageId, Mono.just(packageRequestModel));
//
//        StepVerifier.create(result)
//                .assertNext(packageResponseModel -> {
//                    assertEquals("1", packageResponseModel.getPackageId());
//                    assertEquals("Sample Package", packageResponseModel.getName());
//                    assertEquals("A sample package description", packageResponseModel.getDescription());
//                    assertEquals(LocalDate.of(2024, 10, 5), packageResponseModel.getStartDate());
//                    assertEquals(LocalDate.of(2024, 10, 15), packageResponseModel.getEndDate());
//                    assertEquals("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634", packageResponseModel.getAirportId());
//                    assertEquals("6a237fda-4924-4c73-a6df-73c1e0c37af2", packageResponseModel.getTourId());
//                    assertEquals(2200.0, packageResponseModel.getPriceSingle());
//                    assertEquals(2000.0, packageResponseModel.getPriceDouble());
//                    assertEquals(1800.0, packageResponseModel.getPriceTriple());
//                    assertEquals(130, packageResponseModel.getTotalSeats());
//                })
//                .verifyComplete();
//    }



    @Test
    void whenUpdatePackage_withExistingId_thenReturnUpdatedPackage() {
        String packageId = "1";
        PackageRequestModel packageRequestModel = PackageRequestModel.builder()
                .name("Silk Road Adventure")
                .description("A sample package description")
                .startDate(LocalDate.of(2024, 10, 5))
                .endDate(LocalDate.of(2024, 10, 15))
                .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                .priceSingle(2200.0)
                .priceDouble(2000.0)
                .priceTriple(1800.0)
                .totalSeats(130)
                .build();

        Package package1 = Package.builder()
                .packageId("1")
                .name("Old Package Name")
                .description("Old Description")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 10))
                .airportId("old-airport-id")
                .tourId("old-tour-id")
                .priceSingle(1000.0)
                .priceDouble(900.0)
                .priceTriple(800.0)
                .totalSeats(130)
                .availableSeats(130) // Initialize availableSeats
                .build();

        when(packageRepository.findPackageByPackageId(packageId))
                .thenReturn(Mono.just(package1));

        when(tourService.getTourByTourId(packageRequestModel.getTourId()))
                .thenReturn(Mono.just(TourResponseModel.builder().tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2").build()));

        when(airportService.getAirportById(packageRequestModel.getAirportId()))
                .thenReturn(Mono.just(AirportResponseModel.builder().airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634").build()));

        when(bookingService.getBookingsByPackageId(packageId))
                .thenReturn(Flux.just());

        when(packageRepository.save(any(Package.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<PackageResponseModel> result = packageService.updatePackage(packageId, Mono.just(packageRequestModel), sampleNotificationMessage);

        StepVerifier.create(result)
                .assertNext(packageResponseModel -> {
                    assertEquals("1", packageResponseModel.getPackageId());
                    assertEquals("Silk Road Adventure", packageResponseModel.getName());
                    assertEquals("A sample package description", packageResponseModel.getDescription());
                    assertEquals(LocalDate.of(2024, 10, 5), packageResponseModel.getStartDate());
                    assertEquals(LocalDate.of(2024, 10, 15), packageResponseModel.getEndDate());
                    assertEquals("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634", packageResponseModel.getAirportId());
                    assertEquals("6a237fda-4924-4c73-a6df-73c1e0c37af2", packageResponseModel.getTourId());
                    assertEquals(2200.0, packageResponseModel.getPriceSingle());
                    assertEquals(2000.0, packageResponseModel.getPriceDouble());
                    assertEquals(1800.0, packageResponseModel.getPriceTriple());
                    assertEquals(130, packageResponseModel.getTotalSeats());
                })
                .verifyComplete();
    }

    @Test
    void whenUpdatePackage_withNonExistingId_thenReturnNotFound() {
        String packageId = "1";
        PackageRequestModel packageRequestModel = PackageRequestModel.builder()
                .name("Sample Package")
                .description("A sample package description")
                .startDate(LocalDate.of(2024, 10, 5))
                .endDate(LocalDate.of(2024, 10, 15))
                .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                .tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                .priceSingle(2200.0)
                .priceDouble(2000.0)
                .priceTriple(1800.0)
                .totalSeats(130)
                .build();

        when(packageRepository.findPackageByPackageId(packageId))
                .thenReturn(Mono.empty());

        Mono<PackageResponseModel> result = packageService.updatePackage(packageId, Mono.just(packageRequestModel), sampleNotificationMessage);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException)
                .verify();
    }

    @Test
    void whenUpdating_withNonExistingTourId_thenReturnNotFound() {
        String packageId = "1";
        PackageRequestModel packageRequestModel = PackageRequestModel.builder()
                .name("Sample Package")
                .description("A sample package description")
                .startDate(LocalDate.of(2024, 10, 5))
                .endDate(LocalDate.of(2024, 10, 15))
                .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                .tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                .priceSingle(2200.0)
                .priceDouble(2000.0)
                .priceTriple(1800.0)
                .totalSeats(130)
                .build();

        when(packageRepository.findPackageByPackageId(packageId))
                .thenReturn(Mono.just(package1));

        when(tourService.getTourByTourId(packageRequestModel.getTourId()))
                .thenReturn(Mono.empty());

        when(airportService.getAirportById(packageRequestModel.getAirportId()))
                .thenReturn(Mono.just(AirportResponseModel.builder()
                        .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                        .build()));

        Mono<PackageResponseModel> result = packageService.updatePackage(packageId, Mono.just(packageRequestModel), sampleNotificationMessage);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException)
                .verify();
    }

    @Test
    void whenUpdating_withNonExistingAirportId_thenReturnNotFound() {
        String packageId = "1";
        PackageRequestModel packageRequestModel = PackageRequestModel.builder()
                .name("Sample Package")
                .description("A sample package description")
                .startDate(LocalDate.of(2024, 10, 5))
                .endDate(LocalDate.of(2024, 10, 15))
                .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                .tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                .priceSingle(2200.0)
                .priceDouble(2000.0)
                .priceTriple(1800.0)
                .totalSeats(130)
                .build();

        when(packageRepository.findPackageByPackageId(packageId))
                .thenReturn(Mono.just(package1));

        when(tourService.getTourByTourId(packageRequestModel.getTourId()))
                .thenReturn(Mono.just(TourResponseModel.builder().tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e").build()));

        when(airportService.getAirportById(packageRequestModel.getAirportId()))
                .thenReturn(Mono.empty());

        Mono<PackageResponseModel> result = packageService.updatePackage(packageId, Mono.just(packageRequestModel), sampleNotificationMessage);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException)
                .verify();
    }

    @Test
    void whenDeletePackage_withExistingId_thenReturnDeletedPackage() {
        String packageId = "1";

        when(packageRepository.findPackageByPackageId(packageId))
                .thenReturn(Mono.just(package1));

        when(packageRepository.delete(package1))
                .thenReturn(Mono.empty());

        Mono<PackageResponseModel> result = packageService.deletePackage(packageId);

        StepVerifier.create(result)
                .expectNextMatches(packageResponseModel -> packageResponseModel.getPackageId().equals("1"))
                .verifyComplete();
    }

    @Test
    void whenDeletePackage_withNonExistingId_thenReturnNotFound() {
        String packageId = "1";

        when(packageRepository.findPackageByPackageId(packageId))
                .thenReturn(Mono.empty());

        Mono<PackageResponseModel> result = packageService.deletePackage(packageId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException)
                .verify();
    }

    @Test
    void whenDecreaseAvailableSeats_withExistingId_thenReturnUpdatedPackage() {
        String packageId = "1";
        Integer quantity = 10;

        when(packageRepository.findPackageByPackageId(packageId))
                .thenReturn(Mono.just(package1));

        when(packageRepository.save(any(Package.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0))); // Mock the save

        Mono<PackageResponseModel> result = packageService.decreaseAvailableSeats(packageId, quantity);

        StepVerifier.create(result)
                .assertNext(packageResponseModel -> {
                    assertEquals("1", packageResponseModel.getPackageId()); // Ensure the original packageId is retained
                    assertEquals(110, packageResponseModel.getAvailableSeats());
                })
                .verifyComplete();
    }

    @Test
    void whenDecreaseAvailableSeats_withNonExistingId_thenReturnNotFound() {
        String packageId = "NonExistingId";
        Integer quantity = 10;

        when(packageRepository.findPackageByPackageId(packageId))
                .thenReturn(Mono.empty());

        Mono<PackageResponseModel> result = packageService.decreaseAvailableSeats(packageId, quantity);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException)
                .verify();
    }

    @Test
    void whenIncreaseAvailableSeats_withExistingId_thenReturnUpdatedPackage() {
        String packageId = "1";
        Integer quantity = 10;

        when(packageRepository.findPackageByPackageId(packageId))
                .thenReturn(Mono.just(package1));

        when(packageRepository.save(any(Package.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0))); // Mock the save

        Mono<PackageResponseModel> result = packageService.increaseAvailableSeats(packageId, quantity);

        StepVerifier.create(result)
                .assertNext(packageResponseModel -> {
                    assertEquals("1", packageResponseModel.getPackageId()); // Ensure the original packageId is retained
                    assertEquals(130, packageResponseModel.getAvailableSeats());
                })
                .verifyComplete();
    }

    @Test
    void whenIncreaseAvailableSeats_withNonExistingId_thenReturnNotFound() {
        String packageId = "NonExistingId";
        Integer quantity = 10;

        when(packageRepository.findPackageByPackageId(packageId))
                .thenReturn(Mono.empty());

        Mono<PackageResponseModel> result = packageService.increaseAvailableSeats(packageId, quantity);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException)
                .verify();
    }

}