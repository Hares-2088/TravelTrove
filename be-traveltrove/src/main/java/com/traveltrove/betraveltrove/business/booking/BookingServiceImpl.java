package com.traveltrove.betraveltrove.business.booking;

import com.traveltrove.betraveltrove.business.tourpackage.PackageService;
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

    public BookingServiceImpl(BookingRepository bookingRepository, PackageService packageService, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.packageService = packageService;
        this.userService = userService;
    }

    @Override
    public Flux<BookingResponseModel> getBookings() {
        return bookingRepository.findAll()
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    @Override
    public Flux<BookingResponseModel> getBookingsByUserId(String userId) {
        return bookingRepository.findBookingsByUserId(userId)
                .filter(booking -> userExists(booking.getUserId()))
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    @Override
    public Flux<BookingResponseModel> getBookingsByPackageId(String packageId) {
        return bookingRepository.findBookingsByPackageId(packageId)
                .filter(booking -> packageExists(booking.getPackageId()))
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
                .filter(booking -> userExists(booking.getUserId()))
                .filter(booking -> packageExists(booking.getPackageId()))
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

@Override
public Mono<BookingResponseModel> createBooking(BookingRequestModel bookingRequestModel) {
    return Mono.just(bookingRequestModel)
            .map(BookingEntityModelUtil::toBookingEntity)
            .filter(booking -> userExists(booking.getUserId()))
            .filter(booking -> packageExists(booking.getPackageId()))
            .flatMap(booking -> bookingRepository.findBookingByPackageIdAndUserId(booking.getPackageId(), booking.getUserId())
                    .hasElement()
                    .flatMap(exists -> {
                        if (exists) {
                            return Mono.error(new InvalidStatusException("User already has a booking for this package"));
                        } else {
                            return bookingRepository.save(booking);
                        }
                    }))
            .map(BookingEntityModelUtil::toBookingResponseModel);
}

    @Override
    public Mono<BookingResponseModel> confirmBooking(String bookingId) {
        return bookingRepository.findBookingByBookingId(bookingId)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found with ID: " + bookingId)))
                .flatMap(booking -> {
                    if (booking.getStatus() == BookingStatus.BOOKING_CONFIRMED) {
                        return Mono.error(new SameStatusException("Booking is already confirmed."));
                    }
                    booking.setStatus(BookingStatus.BOOKING_CONFIRMED);
                    return bookingRepository.save(booking);
                })
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    @Override
    public Mono<BookingResponseModel> paymentPending(String bookingId) {
        return bookingRepository.findBookingByBookingId(bookingId)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found with ID: " + bookingId)))
                .flatMap(booking -> {
                    if (booking.getStatus() == BookingStatus.PAYMENT_PENDING) {
                        return Mono.error(new SameStatusException("Booking is already in payment pending status."));
                    }
                    booking.setStatus(BookingStatus.PAYMENT_PENDING);
                    return bookingRepository.save(booking);
                })
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    @Override
    public Mono<BookingResponseModel> paymentTentative2(String bookingId) {
        return bookingRepository.findBookingByBookingId(bookingId)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found with ID: " + bookingId)))
                .flatMap(booking -> {
                    if (booking.getStatus() == BookingStatus.PAYMENT_TENTATIVE2_PENDING) {
                        return Mono.error(new SameStatusException("Booking is already in its tentative 2 pending status."));
                    }
                    booking.setStatus(BookingStatus.PAYMENT_TENTATIVE2_PENDING);
                    return bookingRepository.save(booking);
                })
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    @Override
    public Mono<BookingResponseModel> paymentTentative3(String bookingId) {
        return bookingRepository.findBookingByBookingId(bookingId)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found with ID: " + bookingId)))
                .flatMap(booking -> {
                    if (booking.getStatus() == BookingStatus.PAYMENT_TENTATIVE3_PENDING) {
                        return Mono.error(new SameStatusException("Booking is already in its tentative 3 pending status."));
                    }
                    booking.setStatus(BookingStatus.PAYMENT_TENTATIVE3_PENDING);
                    return bookingRepository.save(booking);
                })
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    @Override
    public Mono<BookingResponseModel> bookingFailed(String bookingId) {
        return bookingRepository.findBookingByBookingId(bookingId)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found with ID: " + bookingId)))
                .flatMap(booking -> {
                    if (booking.getStatus() == BookingStatus.BOOKING_FAILED) {
                        return Mono.error(new SameStatusException("Booking is already in failed status."));
                    }
                    booking.setStatus(BookingStatus.BOOKING_FAILED);
                    return bookingRepository.save(booking);
                })
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    @Override
    public Mono<BookingResponseModel> paymentSuccess(String bookingId) {
        return bookingRepository.findBookingByBookingId(bookingId)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found with ID: " + bookingId)))
                .flatMap(booking -> {
                    if (booking.getStatus() == BookingStatus.PAYMENT_SUCCESS) {
                        return Mono.error(new SameStatusException("Booking is already in payment success status."));
                    }
                    booking.setStatus(BookingStatus.PAYMENT_SUCCESS);
                    return bookingRepository.save(booking);
                })
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    @Override
    public Mono<BookingResponseModel> finalizeBooking(String bookingId) {
        return bookingRepository.findBookingByBookingId(bookingId)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found with ID: " + bookingId)))
                .flatMap(booking -> {
                    if (booking.getStatus() == BookingStatus.BOOKING_FINALIZED) {
                        return Mono.error(new SameStatusException("Booking is already in finalized status."));
                    }
                    booking.setStatus(BookingStatus.BOOKING_FINALIZED);
                    return bookingRepository.save(booking);
                })
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    @Override
    public Mono<BookingResponseModel> expireBooking(String bookingId) {
        return bookingRepository.findBookingByBookingId(bookingId)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found with ID: " + bookingId)))
                .flatMap(booking -> {
                    if (booking.getStatus() == BookingStatus.BOOKING_EXPIRED) {
                        return Mono.error(new SameStatusException("Booking is already in expired status."));
                    }
                    booking.setStatus(BookingStatus.BOOKING_EXPIRED);
                    return bookingRepository.save(booking);
                })
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    @Override
    public Mono<BookingResponseModel> refundBooking(String bookingId) {
        return bookingRepository.findBookingByBookingId(bookingId)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found with ID: " + bookingId)))
                .flatMap(booking -> {
                    if (booking.getStatus() == BookingStatus.REFUNDED) {
                        return Mono.error(new SameStatusException("Booking is already in refunded status."));
                    }
                    booking.setStatus(BookingStatus.REFUNDED);
                    return bookingRepository.save(booking);
                })
                .map(BookingEntityModelUtil::toBookingResponseModel);
    }

    @Override
    public Mono<BookingResponseModel> cancelBooking(String bookingId) {
        return bookingRepository.findBookingByBookingId(bookingId)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found with ID: " + bookingId)))
                .flatMap(booking -> {
                    if (booking.getStatus() == BookingStatus.BOOKING_CANCELLED) {
                        return Mono.error(new SameStatusException("Booking is already in canceled status."));
                    }
                    booking.setStatus(BookingStatus.BOOKING_CANCELLED);
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
    private boolean userExists(String userId) {
        return userService.syncUserWithAuth0(userId)
                .hasElement()
                .blockOptional()
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
    }

    private boolean packageExists(String packageId) {
        return packageService.getPackageByPackageId(packageId)
                .hasElement()
                .blockOptional()
                .orElseThrow(() -> new NotFoundException("Package not found with ID: " + packageId));
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
