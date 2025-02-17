package com.traveltrove.betraveltrove.business.tourpackage;

import com.traveltrove.betraveltrove.business.airport.AirportService;
import com.traveltrove.betraveltrove.business.booking.BookingService;
import com.traveltrove.betraveltrove.business.notification.NotificationService;
import com.traveltrove.betraveltrove.business.tour.TourService;
import com.traveltrove.betraveltrove.business.user.UserService;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingRepository;
import com.traveltrove.betraveltrove.dataaccess.tourpackage.Package;
import com.traveltrove.betraveltrove.dataaccess.tourpackage.PackageRepository;
import com.traveltrove.betraveltrove.dataaccess.tourpackage.PackageStatus;
import com.traveltrove.betraveltrove.dataaccess.user.UserRepository;
import com.traveltrove.betraveltrove.presentation.airport.AirportResponseModel;
import com.traveltrove.betraveltrove.presentation.tour.TourResponseModel;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageRequestModel;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageRequestStatus;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageResponseModel;
import com.traveltrove.betraveltrove.utils.entitymodelyutils.PackageEntityModelUtil;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PackageServiceImpl implements PackageService {

    private final PackageRepository packageRepository;

    private final TourService tourService;

    private final AirportService airportService;

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    private final SubscriptionService subscriptionService;

    private final BookingService bookingService;

    private final BookingRepository bookingRepository;

    private final UserService userService;

    @Value("${frontend.domain}")
    private String baseUrl;

    private PackageStatus packageStatus;

    public PackageServiceImpl(PackageRepository packageRepository, TourService tourService, AirportService airportService, NotificationService notificationService, UserRepository userRepository, @Lazy SubscriptionService subscriptionService, @Lazy BookingService bookingService, BookingRepository bookingRepository, UserService userService) {
        this.packageRepository = packageRepository;
        this.tourService = tourService;
        this.airportService = airportService;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.subscriptionService = subscriptionService;
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        this.userService = userService;
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
                            pk.setStatus(PackageStatus.UPCOMING);
                            return packageRepository.save(pk)
                                    .map(PackageEntityModelUtil::toPackageResponseModel);
                        }));
    }

    @Override
    public Mono<PackageResponseModel> updatePackageStatus(String packageId, PackageRequestStatus newStatus) {
        if (newStatus == null || newStatus.getStatus() == null) {
            log.error("Received null status for packageId={}", packageId);
            return Mono.error(new IllegalArgumentException("Package status cannot be null"));
        }

        log.info("Service: Updating package status for packageId={}, requested newStatus={}", packageId, newStatus.getStatus());

        return packageRepository.findPackageByPackageId(packageId)
                .switchIfEmpty(Mono.error(new NotFoundException("Package not found: " + packageId)))
                .doOnSuccess(pkg -> log.info("Found package in DB: packageId={}, currentStatus={}", packageId, pkg.getStatus())) // Log before checking conditions
                .flatMap(pkg -> {
                    if (pkg.getStatus() == PackageStatus.CANCELLED || pkg.getStatus() == PackageStatus.COMPLETED) {
                        log.error(" Cannot update packageId={} because it is in state {}", packageId, pkg.getStatus());
                        return Mono.error(new IllegalStateException("Cannot update a package that is " + pkg.getStatus()));
                    }

                    log.info("Changing status of packageId={} from {} to {}", packageId, pkg.getStatus(), newStatus.getStatus());
                    pkg.setStatus(newStatus.getStatus());

                    return packageRepository.save(pkg)
                            .doOnSuccess(updatedPkg -> log.info("✅ Package status successfully updated in DB: packageId={}, newStatus={}", packageId, updatedPkg.getStatus()))
                            .flatMap(updatedPkg -> {
                                // If the package is cancelled, send email notifications
                                if (newStatus.getStatus() == PackageStatus.CANCELLED) {
                                    return notifyCustomersOfCancellation(updatedPkg)
                                            .thenReturn(PackageEntityModelUtil.toPackageResponseModel(updatedPkg));
                                }
                                return Mono.just(PackageEntityModelUtil.toPackageResponseModel(updatedPkg));
                            });
                });
    }

    @Override
    public Mono<PackageResponseModel> updatePackage(String packageId, Mono<PackageRequestModel> packageRequestModel, String notificationDetails) {
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
                                        updatedPackage.setStatus(existingPackage.getStatus()); // Retain the status
                                        if (updatedPackage.getAvailableSeats() > existingPackage.getTotalSeats()) {
                                            return Mono.error(new IllegalArgumentException("Available seats cannot exceed total seats"));
                                        }
                                        return packageRepository.save(updatedPackage)
                                                .flatMap(savedPackage -> {
                                                    // Fetch all subscriptions for the package
                                                    return subscriptionService.getUsersSubscribedToPackage(packageId)
                                                            .flatMap(subscription -> userService.getUser(subscription.getUserId())
                                                                    .flatMap(user -> {
                                                                        // Send custom update email to each user
                                                                        return notificationService.sendCustomUpdateEmail(
                                                                                user.getEmail(),
                                                                                notificationDetails,
                                                                                user.getFirstName(),
                                                                                savedPackage
                                                                        );
                                                                    })
                                                            ).then(Mono.just(PackageEntityModelUtil.toPackageResponseModel(savedPackage)));
                                                });
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
                .flatMap(pkg -> {
                    if (pkg.getAvailableSeats() < quantity) {
                        return Mono.error(new IllegalArgumentException("Not enough available seats to decrease by " + quantity));
                    } else if (quantity < 0) {
                        return Mono.error(new IllegalArgumentException("Quantity of seats decreased cannot be less than 0"));
                    }

                    // Update available seats
                    pkg.setAvailableSeats(pkg.getAvailableSeats() - quantity);

                    List<Mono<Void>> notificationMonos = new ArrayList<>();

                    if (pkg.getAvailableSeats() < 10) {
                        notificationMonos.add(notifySubscribersOfLimitedSpots(pkg));
                    }
                    if (pkg.getAvailableSeats() <= 10) {
                        notificationMonos.add(notifyAdminsForLowSeats(pkg)); // Notify admins if seats <= 10
                    }

                    if (pkg.getAvailableSeats() == 0) {
                        pkg.setStatus(PackageStatus.SOLD_OUT);
                    }

                    return packageRepository.save(pkg)
                            .flatMap(savedPackage -> Mono.when(notificationMonos)
                                    .thenReturn(PackageEntityModelUtil.toPackageResponseModel(savedPackage)));
                });
    }


    private Mono<Void> notifySubscribersOfLimitedSpots(Package pkg) {
        return subscriptionService.getUsersSubscribedToPackage(pkg.getPackageId())
                .flatMap(subscription -> userService.getUser(subscription.getUserId())
                        .flatMap(user -> {
                            String bookingLink = baseUrl + "/packages/" + pkg.getPackageId();

                            return notificationService.sendLimitedSpotsEmail(
                                    user.getEmail(),
                                    user.getFirstName(),
                                    pkg.getName(),
                                    pkg.getDescription(),
                                    pkg.getStartDate().toString(),
                                    pkg.getEndDate().toString(),
                                    String.valueOf(pkg.getPriceSingle()),
                                    String.valueOf(pkg.getAvailableSeats()),
                                    bookingLink
                            );
                        })
                ).then();
    }

    @PostConstruct
    public void checkLowSeatPackagesAtStartup() {
        log.info("Checking for low seat packages at startup...");

        packageRepository.findAll()  // Assuming a method to fetch all packages
                .filter(pkg -> pkg.getAvailableSeats() <= 5)
                .doOnNext(pkg -> {
                    log.info("Found low seat package: {}", pkg.getName());
                    // Trigger email notification for each package with low seats
                    notifyAdminsForLowSeats(pkg).subscribe();  // Use subscribe to trigger the notification asynchronously
                })
                .subscribe();  // Start the process
    }


    private Mono<Void> notifyAdminsForLowSeats(Package pkg) {
        log.info("Checking if admin email needs to be sent for package: {}", pkg.getName());

        return userService.getAllUsers()  // Assuming userService.getAllUsers() returns all users
                .filter(user -> user.getRoles().contains("Admin"))  // Filter only users with the 'Admin' role
                .flatMap(user -> {
                    log.info("Found admin user: {}. Sending low seat notification.", user.getEmail());

                    String subject = "🚨 Low Quantity of Available Seats for Package: " + pkg.getName();
                    String message = "The package '" + pkg.getName() + "' (ID: " + pkg.getPackageId() + ") has only " +
                            pkg.getAvailableSeats() + " seats left. Please review the package details.";


                    return notificationService.sendAdminEmail(
                            user.getEmail(),
                            pkg.getName(),
                            pkg.getPackageId(),
                            String.valueOf(pkg.getAvailableSeats()),
                            pkg.getDescription(),
                            pkg.getStartDate().toString(),
                            pkg.getEndDate().toString(),
                            String.valueOf(pkg.getPriceSingle())
                    );
                })
                .doOnTerminate(() -> log.info("Email process completed"))
                .then();
    }


    private Mono<Void> notifyCustomersOfCancellation(Package pkg) {
        return bookingRepository.findBookingsByPackageId(pkg.getPackageId())
                .flatMap(booking -> userRepository.findById(booking.getUserId())
                        .flatMap(user -> {
                            log.info("📧 Sending cancellation email to: {}", user.getEmail());
                            return notificationService.sendCustomerCancellationEmail(
                                    user.getEmail(),
                                    user.getFirstName(),
                                    user.getLastName(),
                                    pkg.getName(),
                                    pkg.getDescription(),
                                    pkg.getStartDate().toString(),
                                    pkg.getEndDate().toString(),
                                    pkg.getPriceSingle().toString()
                            );
                        })
                ).then();
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

                    // if the available seats was 0, and we are increasing the available seats, and the status was sold_out then set the package status to booking_open
                    if (pk.getAvailableSeats() > 0 && pk.getStatus() == PackageStatus.SOLD_OUT) {
                        pk.setStatus(PackageStatus.BOOKING_OPEN);
                    }
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
