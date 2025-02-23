package com.traveltrove.betraveltrove.business.booking;

import com.traveltrove.betraveltrove.business.notification.NotificationService;
import com.traveltrove.betraveltrove.business.tourpackage.PackageService;
import com.traveltrove.betraveltrove.business.tourpackage.SubscriptionService;
import com.traveltrove.betraveltrove.business.traveler.TravelerService;
import com.traveltrove.betraveltrove.business.user.UserService;
import com.traveltrove.betraveltrove.dataaccess.booking.Booking;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingRepository;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import com.traveltrove.betraveltrove.presentation.booking.BookingRequestModel;
import com.traveltrove.betraveltrove.presentation.booking.BookingResponseModel;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageResponseModel;
import com.traveltrove.betraveltrove.presentation.traveler.TravelerRequestModel;
import com.traveltrove.betraveltrove.presentation.traveler.TravelerResponseModel;
import com.traveltrove.betraveltrove.presentation.user.UserResponseModel;
import com.traveltrove.betraveltrove.presentation.user.UserUpdateRequest;
import com.traveltrove.betraveltrove.utils.entitymodelyutils.BookingEntityModelUtil;
import com.traveltrove.betraveltrove.utils.exceptions.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final PackageService packageService;
    private final UserService userService;
    private final TravelerService travelerService;
    private final NotificationService notificationService;
    private final TaskScheduler taskScheduler;
    private final SubscriptionService subscriptionService;

    @Value("${EMAIL_REVIEW_DELAY}")
    private String emailReviewDelay;

    @Value("${frontend.domain}")
    private String baseUrl;

    @Override
    public Flux<BookingResponseModel> getBookings() {
        return bookingRepository.findAll()
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    @Override
    public Flux<BookingResponseModel> getBookingsByUserId(String userId) {
        return userExistsReactive(userId) // Validate the user reactively
                .thenMany(bookingRepository.findBookingsByUserId(userId)) // Fetch bookings if user exists
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }


    @Override
    public Flux<BookingResponseModel> getBookingsByPackageId(String packageId) {
        return packageExistsReactive(packageId) // Validate the package reactively
                .thenMany(bookingRepository.findBookingsByPackageId(packageId)) // Fetch bookings if package exists
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }


    @Override
    public Flux<BookingResponseModel> getBookingsByStatus(BookingStatus status) {
        if (!isValidStatus(status)) {
            return Flux.empty();
        }
        return bookingRepository.findBookingsByStatus(status)
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    @Override
    public Mono<BookingResponseModel> getBooking(String bookingId) {
        return bookingRepository.findBookingByBookingId(bookingId)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found with ID: " + bookingId)))
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    @Override
    public Mono<BookingResponseModel> getBookingByPackageIdAndUserId(String packageId, String userId) {
        return bookingRepository.findBookingByPackageIdAndUserId(packageId, userId)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found with package ID: " + packageId + " and user ID: " + userId)))
                .flatMap(booking ->
                        userExistsReactive(booking.getUserId())
                                .then(packageExistsReactive(booking.getPackageId()))
                                .thenReturn(booking) // Continue with the booking after validation
                )
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    @Override
    public Mono<BookingResponseModel> createBooking(BookingRequestModel bookingRequestModel) {
        log.info("Creating booking for userId={} and packageId={}", bookingRequestModel.getUserId(), bookingRequestModel.getPackageId());
        return Mono.just(bookingRequestModel)
                // Convert request model to entity
                .map(BookingEntityModelUtil::toBookingEntity)

                // 1) Validate user/package and check if booking already exists
                .flatMap(this::validateUserAndPackage)
                .doOnNext(booking -> log.info("Validated user and package for bookingId={}", booking.getBookingId()))

                // 2) Fetch the user from Auth0 (ensuring user exists)
                .flatMap(this::fetchUserFromAuth0)
                .doOnNext(tuple -> log.info("Fetched user from Auth0 for userId={}", tuple.getT2().getUserId()))

                // 3) Ensure at least one traveler is present
                .flatMap(tuple ->
                        ensureAtLeastOneTravelerPresent(bookingRequestModel, tuple.getT2())
                                .thenReturn(tuple)
                )
                .doOnNext(tuple -> log.info("Ensured at least one traveler is present for userId={}", tuple.getT2().getUserId()))

                // 3) Gather existing traveler details
                .flatMap(tuple -> gatherExistingTravelerDetails(tuple.getT1(), tuple.getT2()))
                .doOnNext(tuple -> log.info("Gathered existing traveler details for userId={}", tuple.getT2().getUserId()))

                // 4) Compare travelers in the request with existing travelers & create new ones if needed
                .flatMap(tuple -> compareTravelersAndCreateNew(
                        tuple.getT1(),
                        tuple.getT2(),
                        tuple.getT3(),
                        bookingRequestModel
                ))
                .doOnNext(tuple -> log.info("Compared travelers and created new ones if needed for userId={}", tuple.getT2().getUserId()))

                // 5) Update the user’s traveler IDs to include any newly created travelers
                .flatMap(tuple -> updateUserTravelerIds(
                        tuple.getT1(),
                        tuple.getT2(),
                        tuple.getT3(),
                        tuple.getT4()
                ))
                .doOnNext(booking -> log.info("Updated user traveler IDs for bookingId={}", booking.getBookingId()))

                .flatMap(booking -> {
                    booking.setStatus(BookingStatus.PAYMENT_PENDING);
                    return Mono.just(booking);
                })
                .doOnNext(booking -> log.info("Set booking status to PAYMENT_PENDING for bookingId={}", booking.getBookingId()))

                // 6) Save the booking in Mongo and convert to response
                .flatMap(bookingRepository::save)
                .doOnNext(savedBooking -> log.info("Saved booking in Mongo for bookingId={}", savedBooking.getBookingId()))

                .flatMap(savedBooking ->
                        subscriptionService.subscribeUserToPackage(savedBooking.getUserId(), savedBooking.getPackageId())
                                // Don't subscribe if user is already subscribed
                                .onErrorResume(IllegalArgumentException.class, e -> {
                                    log.warn("User {} is already subscribed to package {}", savedBooking.getUserId(), savedBooking.getPackageId());
                                    return Mono.empty();
                                })
                                .thenReturn(savedBooking)
                )
                .doOnNext(savedBooking -> log.info("Subscribed user to package for bookingId={}", savedBooking.getBookingId()))

                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    @Override
    public Mono<BookingResponseModel> updateBookingStatus(String bookingId, BookingStatus newStatus) {
        return bookingRepository.findBookingByBookingId(bookingId)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found with ID: " + bookingId)))
                .flatMap(booking -> {
                    BookingStatus currentStatus = booking.getStatus();

                    // Rule 1: If the new status is the same as the current status, throw an error
                    if (currentStatus == newStatus) {
                        return Mono.error(new SameStatusException("Booking is already in the status: " + newStatus));
                    }

                    // Rule 2: If the current status is BOOKING_CONFIRMED, only allow transition to REFUNDED
                    if (currentStatus == BookingStatus.BOOKING_CONFIRMED && newStatus != BookingStatus.REFUNDED) {
                        return Mono.error(new InvalidStatusTransitionException("Cannot transition from BOOKING_CONFIRMED to " + newStatus));
                    }

                    // Rule 3: If the current status is PAYMENT_ATTEMPT2_PENDING, disallow transition to PAYMENT_PENDING
                    if (currentStatus == BookingStatus.PAYMENT_ATTEMPT2_PENDING && newStatus == BookingStatus.PAYMENT_PENDING) {
                        return Mono.error(new InvalidStatusTransitionException("Cannot transition from PAYMENT_ATTEMPT2_PENDING to PAYMENT_PENDING"));
                    }

                    // Rule 4: Disallow transition to REFUNDED unless the current status is BOOKING_CONFIRMED
                    if (newStatus == BookingStatus.REFUNDED && currentStatus != BookingStatus.BOOKING_CONFIRMED) {
                        return Mono.error(new InvalidStatusTransitionException("Cannot transition to REFUNDED from " + currentStatus));
                    }

                    // If all rules are satisfied, update the status
                    booking.setStatus(newStatus);
                    return bookingRepository.save(booking);
                })
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    @Override
    public Mono<BookingResponseModel> deleteBooking(String bookingId) {
        return bookingRepository.findBookingByBookingId(bookingId)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found with ID: " + bookingId)))
                .flatMap(booking -> bookingRepository.delete(booking).thenReturn(booking))
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    public Mono<BookingResponseModel> confirmBookingPayment(String bookingId) {
        log.info("🔄 Confirming payment for bookingId={}", bookingId);

        return bookingRepository.findBookingByBookingId(bookingId)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found with ID: " + bookingId)))
                .flatMap(booking -> {
                    if (booking.getStatus() == BookingStatus.BOOKING_CONFIRMED) {
                        return Mono.error(new SameStatusException("Booking is already confirmed."));
                    }
                    booking.setStatus(BookingStatus.BOOKING_CONFIRMED);

                    return packageService.decreaseAvailableSeats(booking.getPackageId(), booking.getTravelerIds().size())
                            .doOnSuccess(updatedPackage -> log.info("✅ Successfully decreased seats for packageId={}", booking.getPackageId()))
                            .doOnError(error -> log.error("❌ Error decreasing seats for packageId={}: {}", booking.getPackageId(), error.getMessage()))
                            .then(bookingRepository.save(booking))
                            .flatMap(savedBooking ->
                                    packageService.getPackageByPackageId(savedBooking.getPackageId())
                                            .doOnSuccess(pkg -> {
                                                try {
                                                    log.info("📧 Attempting to schedule email");
                                                    schedulePostTripEmail(savedBooking, pkg);
                                                } catch (Exception e) {
                                                    log.error("📧 Email scheduling failed: {}", e.getMessage());
                                                }
                                            })
                                            .onErrorResume(error -> {
                                                log.error("📧 Package fetch failed but continuing: {}", error.getMessage());
                                                return Mono.empty();
                                            })
                                            .thenReturn(savedBooking)
                            )
                            .doOnSuccess(savedBooking -> log.info("✅ Booking confirmed: bookingId={}", savedBooking.getBookingId()))
                            .doOnError(error -> log.error("❌ Failed to confirm booking: {}", error.getMessage()));
                })
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    // methods for validation -> userExists, packageExists, isValidStatus
    Mono<Void> userExistsReactive(String userId) {
        return userService.syncUserWithAuth0(userId)
                .hasElement()
                .flatMap(exists -> exists ? Mono.empty() : Mono.error(new NotFoundException("User not found with ID: " + userId)));
    }

    Mono<Void> packageExistsReactive(String packageId) {
        return packageService.getPackageByPackageId(packageId)
                .hasElement()
                .flatMap(exists -> exists ? Mono.empty() : Mono.error(new NotFoundException("Package not found with ID: " + packageId)));
    }

    private boolean isValidStatus(BookingStatus status) {
        if (status == null) {
            throw new InvalidStatusException("Status cannot be null");
        }

        if (!EnumSet.allOf(BookingStatus.class).contains(status)) {
            throw new InvalidStatusException("Invalid booking status: " + status);
        }

        return true;
    }

    Mono<Booking> validateUserAndPackage(Booking booking) {
        return userExistsReactive(booking.getUserId())
                .then(packageExistsReactive(booking.getPackageId()))
                .then(Mono.defer(() -> bookingRepository.findBookingByPackageIdAndUserId(
                        booking.getPackageId(),
                        booking.getUserId()
                )))
                .flatMap(existingBooking -> {
                    // If the booking is in PAYMENT_PENDING, delete it
                    if (existingBooking.getStatus() == BookingStatus.PAYMENT_PENDING) {
                        return bookingRepository.delete(existingBooking)
                                .thenReturn(booking);
                    } else {
                        return Mono.error(new InvalidStatusException(
                                "There's already an active booking for this package"
                        ));
                    }
                })
                // continue with the new booking as usual if no existing booking is found
                .switchIfEmpty(Mono.just(booking));
    }

    Mono<Tuple2<Booking, UserResponseModel>> fetchUserFromAuth0(Booking booking) {
        return userService.syncUserWithAuth0(booking.getUserId())
                .switchIfEmpty(Mono.error(
                        new NotFoundException("User not found: " + booking.getUserId())
                ))
                .map(activeUser -> Tuples.of(booking, activeUser));
    }

    Mono<Tuple3<Booking, UserResponseModel, List<TravelerResponseModel>>>
    gatherExistingTravelerDetails(Booking booking, UserResponseModel activeUser) {

        List<String> existingTravelerIds = new ArrayList<>();
        if (activeUser.getTravelerIds() != null) {
            existingTravelerIds.addAll(activeUser.getTravelerIds());
        }

        return Flux.fromIterable(existingTravelerIds)
                .flatMap(travelerService::getTravelerByTravelerId)
                .collectList()
                .map(existingTravelers -> Tuples.of(booking, activeUser, existingTravelers));
    }

    Mono<Tuple4<Booking, UserResponseModel, List<TravelerResponseModel>, List<String>>>
    compareTravelersAndCreateNew(Booking booking,
                                 UserResponseModel activeUser,
                                 List<TravelerResponseModel> existingTravelers,
                                 BookingRequestModel bookingRequestModel) {

        return Flux.fromIterable(bookingRequestModel.getTravelers())
                .flatMap(requestedTraveler -> {
                    // Find a match in existing travelers
                    TravelerResponseModel match = existingTravelers.stream()
                            .filter(et -> et.getFirstName().equalsIgnoreCase(requestedTraveler.getFirstName()) &&
                                    et.getLastName().equalsIgnoreCase(requestedTraveler.getLastName()))
                            .findFirst()
                            .orElse(null);

                    if (match != null) {
                        // Already exists
                        booking.getTravelerIds().add(match.getTravelerId());
                        return Mono.just(match.getTravelerId());
                    } else {
                        // Create a new traveler
                        TravelerRequestModel newTraveler = TravelerRequestModel.builder()
                                .firstName(requestedTraveler.getFirstName())
                                .lastName(requestedTraveler.getLastName())
                                .addressLine1(requestedTraveler.getAddressLine1())
                                .email(requestedTraveler.getEmail())
                                .build();

                        return travelerService.createTraveler(newTraveler)
                                .map(created -> {
                                    booking.getTravelerIds().add(created.getTravelerId());
                                    return created.getTravelerId();
                                });
                    }
                })
                .collectList()
                .map(newlyLinkedIds -> Tuples.of(booking, activeUser, existingTravelers, newlyLinkedIds));
    }

    Mono<Booking> updateUserTravelerIds(Booking booking,
                                        UserResponseModel activeUser,
                                        List<TravelerResponseModel> existingTravelers,
                                        List<String> newlyLinkedIds) {

        Set<String> allTravelerIds = new HashSet<>();
        if (activeUser.getTravelerIds() != null) {
            allTravelerIds.addAll(activeUser.getTravelerIds());
        }
        if (activeUser.getTravelerId() != null) {
            allTravelerIds.add(activeUser.getTravelerId());
        }
        // Add new traveler IDs
        allTravelerIds.addAll(newlyLinkedIds);

        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .travelerIds(new ArrayList<>(allTravelerIds))
                .build();

        return userService.updateUserProfile(activeUser.getUserId(), updateRequest)
                .thenReturn(booking);
    }

    private Mono<Void> ensureAtLeastOneTravelerPresent(BookingRequestModel bookingRequestModel, UserResponseModel activeUser) {
        // Extract travelers from request
        List<TravelerRequestModel> requestedTravelers = bookingRequestModel.getTravelers();

        // Check if the request provides any travelers
        boolean requestProvidesTraveler = requestedTravelers != null && !requestedTravelers.isEmpty();

        // If neither condition is true, throw an exception
        if (!requestProvidesTraveler) {
            return Mono.error(new NoTravelerException("At least one traveler must be provided or linked to the user's account."));
        }

        return Mono.empty();
    }

    private void schedulePostTripEmail(Booking booking, PackageResponseModel tourPackage) {
        if (tourPackage.getEndDate() == null) {
            log.warn("Skipping review email scheduling: Package {} has no end date.", tourPackage.getPackageId());
            return;
        }

        /* Uncomment after demo
        Duration delayDuration = parseDelay(emailReviewDelay);
        Instant sendTime = tourPackage.getEndDate().atStartOfDay().toInstant(ZoneOffset.UTC).plus(delayDuration);
        */

        Instant sendTime = Instant.now().plusSeconds(5);

        if (sendTime.isAfter(Instant.now())) {
            taskScheduler.schedule(() -> {
                userService.getUser(booking.getUserId()).flatMap(user ->
                        notificationService.sendPostTourReviewEmail(
                                user.getEmail(),
                                user.getFirstName(),
                                tourPackage.getName(),
                                tourPackage.getDescription(),
                                tourPackage.getStartDate().toString(),
                                tourPackage.getEndDate().toString(),
                                baseUrl + "/packages/" + tourPackage.getPackageId()
                        )
                ).subscribe();
            }, sendTime);

            log.info("Scheduled post-tour review email for booking {} at {}", booking.getBookingId(), sendTime);
        } else {
            log.warn("Skipping review email scheduling: Package {} end date has already passed.", tourPackage.getPackageId());
        }
    }

    private Duration parseDelay(String delay) {
        return switch (delay.toLowerCase()) {
            case "30s" -> Duration.ofSeconds(30);
            case "24h", "1d" -> Duration.ofDays(1);
            default -> Duration.ofHours(24);

        };
    }
}
