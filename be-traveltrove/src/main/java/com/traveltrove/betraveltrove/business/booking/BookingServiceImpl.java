package com.traveltrove.betraveltrove.business.booking;

import com.traveltrove.betraveltrove.business.tourpackage.PackageService;
import com.traveltrove.betraveltrove.business.traveler.TravelerService;
import com.traveltrove.betraveltrove.business.user.UserService;
import com.traveltrove.betraveltrove.dataaccess.booking.Booking;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingRepository;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import com.traveltrove.betraveltrove.presentation.booking.BookingRequestModel;
import com.traveltrove.betraveltrove.presentation.booking.BookingResponseModel;
import com.traveltrove.betraveltrove.presentation.traveler.TravelerRequestModel;
import com.traveltrove.betraveltrove.presentation.traveler.TravelerResponseModel;
import com.traveltrove.betraveltrove.presentation.user.UserResponseModel;
import com.traveltrove.betraveltrove.presentation.user.UserUpdateRequest;
import com.traveltrove.betraveltrove.utils.entitymodelyutils.BookingEntityModelUtil;
import com.traveltrove.betraveltrove.utils.exceptions.InvalidStatusException;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import com.traveltrove.betraveltrove.utils.exceptions.SameStatusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final PackageService packageService;
    private final UserService userService;
    private final TravelerService travelerService;

    public BookingServiceImpl(BookingRepository bookingRepository, PackageService packageService, UserService userService, TravelerService travelerService) {
        this.bookingRepository = bookingRepository;
        this.packageService = packageService;
        this.userService = userService;
        this.travelerService = travelerService;
    }

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

        return Mono.just(bookingRequestModel)
                .map(BookingEntityModelUtil::toBookingEntity)
                .flatMap(booking ->
                        // Validate user/package
                        userExistsReactive(booking.getUserId())
                                .then(packageExistsReactive(booking.getPackageId()))

                                // Check if booking exists
                                .then(bookingRepository.findBookingByPackageIdAndUserId(
                                        booking.getPackageId(), booking.getUserId()
                                ).hasElement())
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new InvalidStatusException(
                                                "User already has a booking for this package"));
                                    }
                                    return Mono.just(booking);
                                })
                )

                // Fetch user from Auth0
                .flatMap(booking -> userService.syncUserWithAuth0(booking.getUserId())
                        .switchIfEmpty(Mono.error(
                                new NotFoundException("User not found: " + booking.getUserId()))
                        )
                        .map(activeUser -> Tuples.of(booking, activeUser))
                )

                // Gather existing traveler details
                .flatMap(tuple -> {
                    Booking booking = tuple.getT1();
                    UserResponseModel activeUser = tuple.getT2();

                    List<String> existingTravelerIds = new ArrayList<>();
                    if (activeUser.getTravelerIds() != null) {
                        existingTravelerIds.addAll(activeUser.getTravelerIds());
                    }
                    if (activeUser.getTravelerId() != null) {
                        existingTravelerIds.add(activeUser.getTravelerId());
                    }

                    return Flux.fromIterable(existingTravelerIds)
                            .flatMap(travelerId -> travelerService.getTravelerByTravelerId(travelerId))
                            .collectList()
                            .map(existingTravelers -> Tuples.of(booking, activeUser, existingTravelers));
                })

                // Compare travelers & create new ones
                .flatMap(tuple -> {
                    Booking booking = tuple.getT1();
                    UserResponseModel activeUser = tuple.getT2();
                    List<TravelerResponseModel> existingTravelers = tuple.getT3();

                    return Flux.fromIterable(bookingRequestModel.getTravelers())
                            .flatMap(requestedTraveler -> {
                                // does it match an existing traveler?
                                TravelerResponseModel match = existingTravelers.stream()
                                        .filter(et -> et.getFirstName().equalsIgnoreCase(requestedTraveler.getFirstName()) &&
                                                et.getLastName().equalsIgnoreCase(requestedTraveler.getLastName()))
                                        .findFirst()
                                        .orElse(null);

                                if (match != null) {
                                    booking.getTravelerIds().add(match.getTravelerId());
                                    // Return existing ID
                                    return Mono.just(match.getTravelerId());
                                } else {
                                    // create a new traveler
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
                            .collectList() // gather all traveler IDs
                            .map(newlyLinkedIds -> Tuples.of(booking, activeUser, existingTravelers, newlyLinkedIds));
                })

                // Update the user’s travelerIds set
                .flatMap(tuple -> {
                    Booking booking = tuple.getT1();
                    UserResponseModel activeUser = tuple.getT2();
                    List<TravelerResponseModel> existingTravelers = tuple.getT3();
                    List<String> newlyLinkedIds = tuple.getT4();

                    // create a list of already existing traveler IDs and the newly linked ones
                    Set<String> allTravelerIds = new HashSet<>();
                    if (activeUser.getTravelerIds() != null) {
                        allTravelerIds.addAll(activeUser.getTravelerIds());
                    }
                    if (activeUser.getTravelerId() != null) {
                        allTravelerIds.add(activeUser.getTravelerId());
                    }
                    allTravelerIds.addAll(newlyLinkedIds);

                    UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                            .travelerIds(new ArrayList<>(allTravelerIds))
                            .build();

                    // update user
                    return userService.updateUserProfile(activeUser.getUserId(), updateRequest)
                            .thenReturn(booking); // pass booking forward
                })

                // Save the Booking in Mongo
                .flatMap(booking -> bookingRepository.save(booking))
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    @Override
    public Mono<BookingResponseModel> updateBookingStatus(String bookingId, BookingStatus newStatus) {
        return bookingRepository.findBookingByBookingId(bookingId)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found with ID: " + bookingId)))
                .flatMap(booking -> {
                    if (booking.getStatus() == newStatus) {
                        return Mono.error(new SameStatusException("Booking is already in the status: " + newStatus));
                    }
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

    // methods for validation -> userExists, packageExists, isValidStatus
    private Mono<Void> userExistsReactive(String userId) {
        return userService.syncUserWithAuth0(userId)
                .hasElement()
                .flatMap(exists -> exists ? Mono.empty() : Mono.error(new NotFoundException("User not found with ID: " + userId)));
    }

    private Mono<Void> packageExistsReactive(String packageId) {
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
}
