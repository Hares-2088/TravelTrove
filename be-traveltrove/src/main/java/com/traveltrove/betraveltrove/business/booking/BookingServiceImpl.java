package com.traveltrove.betraveltrove.business.booking;

import com.traveltrove.betraveltrove.business.tourpackage.PackageService;
import com.traveltrove.betraveltrove.business.traveler.TravelerService;
import com.traveltrove.betraveltrove.business.user.UserService;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingRepository;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import com.traveltrove.betraveltrove.presentation.booking.BookingRequestModel;
import com.traveltrove.betraveltrove.presentation.booking.BookingResponseModel;
import com.traveltrove.betraveltrove.utils.entitymodelyutils.BookingEntityModelUtil;
import com.traveltrove.betraveltrove.utils.exceptions.InvalidStatusException;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import com.traveltrove.betraveltrove.utils.exceptions.SameStatusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.EnumSet;

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
                        userExistsReactive(booking.getUserId())
                                .then(packageExistsReactive(booking.getPackageId()))
                                .then(bookingRepository.findBookingByPackageIdAndUserId(booking.getPackageId(), booking.getUserId())
                                        .hasElement()
                                        .flatMap(exists -> {
                                            if (exists) {
                                                return Mono.error(new InvalidStatusException("User already has a booking for this package"));
                                            }
                                            if (booking.getTravelerIds() != null && !booking.getTravelerIds().isEmpty()) {
                                                // get the user to access the list of traveler IDs
                                                userService.syncUserWithAuth0(booking.getUserId())

                                                if ()
                                            }
                                            return bookingRepository.save(booking);
                                        })
                                )
                )
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
