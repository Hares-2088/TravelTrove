package com.traveltrove.betraveltrove.business.tourpackage;

import com.traveltrove.betraveltrove.business.airport.AirportService;
import com.traveltrove.betraveltrove.business.booking.BookingService;
import com.traveltrove.betraveltrove.business.notification.NotificationService;
import com.traveltrove.betraveltrove.business.tour.TourService;
import com.traveltrove.betraveltrove.business.user.UserService;
import com.traveltrove.betraveltrove.dataaccess.tourpackage.Package;
import com.traveltrove.betraveltrove.dataaccess.tourpackage.PackageRepository;
import com.traveltrove.betraveltrove.presentation.airport.AirportResponseModel;
import com.traveltrove.betraveltrove.presentation.tour.TourResponseModel;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageRequestModel;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageResponseModel;
import com.traveltrove.betraveltrove.presentation.user.UserResponseModel;
import com.traveltrove.betraveltrove.utils.entitymodelyutils.PackageEntityModelUtil;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class PackageServiceImpl implements PackageService {

    private final PackageRepository packageRepository;

    private final TourService tourService;

    private final AirportService airportService;

    private final BookingService bookingService;

    private final UserService userService;

    private final NotificationService notificationService;

    public PackageServiceImpl(PackageRepository packageRepository, TourService tourService, AirportService airportService, BookingService bookingService, UserService userService, NotificationService notificationService) {
        this.packageRepository = packageRepository;
        this.tourService = tourService;
        this.airportService = airportService;
        this.bookingService = bookingService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Override
    public Flux<PackageResponseModel> getAllPackages(String tourId) {
        if (tourId == null) {
            log.info("No tourId provided, fetching all packages.");
            return packageRepository.findAll()
                    .map(PackageEntityModelUtil::toPackageResponseModel);
        }
        return getTour(tourId)
                .flatMapMany(tour -> {
                    log.info("Fetching packages for tourId={}", tourId);
                    return packageRepository.findPackagesByTourId(tourId)
                            .doOnComplete(() -> log.info("Finished fetching packages for tourId={}", tourId))
                            .doOnError(error -> log.error("Error fetching packages: {}", error.getMessage()))
                            .map(PackageEntityModelUtil::toPackageResponseModel);
                });
    }


    @Override
    public Mono<PackageResponseModel> getPackageByPackageId(String packageId) {
        return packageRepository.findPackageByPackageId(packageId)
                .map(PackageEntityModelUtil::toPackageResponseModel)
                .switchIfEmpty(Mono.error(new NotFoundException("Package not found: " + packageId)));
    }

    @Override
    public Mono<PackageResponseModel> createPackage(Mono<PackageRequestModel> packageRequestModel) {
        return packageRequestModel
                .flatMap(requestModel -> Mono.zip(getTour(requestModel.getTourId()), getAirport(requestModel.getAirportId()))
                        .doOnNext(tuple -> validatePackageRequestModel(requestModel))
                        .flatMap(tuple -> {
                            Package pk = PackageEntityModelUtil.toPackage(requestModel, tuple.getT1(), tuple.getT2());
                            pk.setAvailableSeats(pk.getTotalSeats());
                            return packageRepository.save(pk)
                                    .map(PackageEntityModelUtil::toPackageResponseModel);
                        }));
    }

    @Override
    public Mono<PackageResponseModel> updatePackage(String packageId, Mono<PackageRequestModel> packageRequestModel, String notificationMessage) {
        final String finalNotificationMessage = (notificationMessage == null || notificationMessage.isEmpty()) ?
                "Hello, \n\nA package you have booked has been updated. Please visit our website to see the details of the changes. \n\nSincerely,\n\nDar El Salam's Management"
                : notificationMessage;

        return packageRepository.findPackageByPackageId(packageId)
                .switchIfEmpty(Mono.error(new NotFoundException("Package not found: " + packageId)))
                .flatMap(existingPackage -> packageRequestModel
                        .flatMap(requestModel -> {
                            validatePackageRequestModel(requestModel);
                            return Mono.zip(getTour(requestModel.getTourId()), getAirport(requestModel.getAirportId()))
                                    .flatMap(tuple -> {
                                        Package updatedPackage = PackageEntityModelUtil.toPackage(requestModel, tuple.getT1(), tuple.getT2());
                                        updatedPackage.setId(existingPackage.getId()); // Retain the DB ID
                                        updatedPackage.setPackageId(existingPackage.getPackageId()); // Retain the original packageId
                                        updatedPackage.setAvailableSeats(existingPackage.getAvailableSeats()); // Retain the available seats
                                        if (updatedPackage.getAvailableSeats() > existingPackage.getTotalSeats()) {
                                            return Mono.error(new IllegalArgumentException("Available seats cannot exceed total seats"));
                                        }
                                        return bookingService.getBookingsByPackageId(packageId)
                                                .flatMap(booking -> userService.getUser(booking.getUserId())
                                                        .flatMap(user -> notificationService.sendCustomEmail(user.getEmail(), "Travel Trove Notice: " + updatedPackage.getName() +
                                                                " Package Update", finalNotificationMessage))
                                                )
                                                .then(packageRepository.save(updatedPackage))
                                                .map(PackageEntityModelUtil::toPackageResponseModel);
                                    });
                        }));
    }


    @Override
    public Mono<PackageResponseModel> deletePackage(String packageId) {
        return packageRepository.findPackageByPackageId(packageId)
                // get the package using the packageId and return a notfound exception if the package is not found
                .switchIfEmpty(Mono.error(new NotFoundException("Package not found: " + packageId)))
                // then delete the package entity model
                .flatMap(pk -> packageRepository.delete(pk)
                        // then return the package response model using the package entity model we just deleted
                        .thenReturn(PackageEntityModelUtil.toPackageResponseModel(pk)));
    }

    @Override
    public Mono<PackageResponseModel> decreaseAvailableSeats(String packageId, Integer quantity) {
        return packageRepository.findPackageByPackageId(packageId)
                .switchIfEmpty(Mono.error(new NotFoundException("Package not found: " + packageId)))
                .flatMap(pk -> {
                    if (pk.getAvailableSeats() < quantity) {
                        return Mono.error(new IllegalArgumentException("Not enough available seats to decrease by " + quantity));
                    } else if (quantity < 0) {
                        return Mono.error(new IllegalArgumentException("Quantity of seats decreased cannot be less than 0"));
                    }
                    pk.setAvailableSeats(pk.getAvailableSeats() - quantity);
                    return packageRepository.save(pk)
                            .map(PackageEntityModelUtil::toPackageResponseModel);
                });
    }

    @Override
    public Mono<PackageResponseModel> increaseAvailableSeats(String packageId, Integer quantity) {
        return packageRepository.findPackageByPackageId(packageId)
                .switchIfEmpty(Mono.error(new NotFoundException("Package not found: " + packageId)))
                .flatMap(pk -> {
                    if (pk.getAvailableSeats() + quantity > pk.getTotalSeats()) {
                        return Mono.error(new IllegalArgumentException("Not enough total seats to increase by " + quantity));
                    } else if (quantity < 0) {
                        return Mono.error(new IllegalArgumentException("Quantity of seats increased cannot be less than 0"));
                    }
                    pk.setAvailableSeats(pk.getAvailableSeats() + quantity);
                    return packageRepository.save(pk)
                            .map(PackageEntityModelUtil::toPackageResponseModel);
                });
    }

    // a method to get the tour using the tourService and the tourId and return a notfound exception if the tour is not found
    private Mono<String> getTour(String tourId) {
        return tourService.getTourByTourId(tourId)
                .doOnNext(tour -> log.info("Found tour: {}", tour))
                .doOnError(error -> log.error("Error fetching tour: {}", error.getMessage()))
                .switchIfEmpty(Mono.error(new NotFoundException("Tour not found: " + tourId)))
                .map(TourResponseModel::getTourId);
    }

    private Mono<String> getAirport(String airportId) {
        return Mono.justOrEmpty(airportId) // Guard against null `airportId`
                .flatMap(airportService::getAirportById)
                .switchIfEmpty(Mono.error(new NotFoundException("Airport not found: " + airportId)))
                .map(AirportResponseModel::getAirportId);
    }


    // a method to check that the package request model is valid
    private void validatePackageRequestModel(PackageRequestModel packageRequestModel) {
        // check that the packageRequestModel is not null
        if (packageRequestModel == null) {
            throw new IllegalArgumentException("Package request model is required");
        }
        // check that the packageRequestModel name is not null
        if (packageRequestModel.getName() == null) {
            throw new IllegalArgumentException("Package name is required");
        }
        // check that the packageRequestModel description is not null
        if (packageRequestModel.getDescription() == null) {
            throw new IllegalArgumentException("Package description is required");
        }
        // check that the packageRequestModel startDate is not null
        if (packageRequestModel.getStartDate() == null) {
            throw new IllegalArgumentException("Package start date is required");
        }
        // check that the packageRequestModel endDate is not null
        if (packageRequestModel.getEndDate() == null) {
            throw new IllegalArgumentException("Package end date is required");
        }
        // check that the packageRequestModel priceSingle is not null
        if (packageRequestModel.getPriceSingle() == null) {
            throw new IllegalArgumentException("Package price single is required");
        }
        // check that the packageRequestModel totalSeats is not null
        if (packageRequestModel.getTotalSeats() == null) {
            throw new IllegalArgumentException("Package total seats is required");
        }
    }
}
