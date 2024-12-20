package com.traveltrove.betraveltrove.presentation.booking;

import com.traveltrove.betraveltrove.business.booking.BookingService;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import com.traveltrove.betraveltrove.utils.exceptions.InvalidInputException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/bookings")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BookingResponseModel> getBookings(@RequestParam(required = false) String userId, @RequestParam(required = false) String PackageId, @RequestParam(required = false) String status) {
        if (status != null && !status.isEmpty()) {
            return bookingService.getBookingsByStatus(BookingStatus.valueOf(status));
        } else if (userId != null && !userId.isEmpty()) {
            return bookingService.getBookingsByUserId(userId);
        } else if (PackageId != null && !PackageId.isEmpty()) {
            return bookingService.getBookingsByPackageId(PackageId);
        }
        return bookingService.getBookings();
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<BookingResponseModel>> getBookingByPackageIdAndUserIdOrBookingId(
            @RequestParam(required = false) String packageId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String bookingId) {
        if (packageId != null && !packageId.isEmpty() && userId != null && !userId.isEmpty()) {
            return bookingService.getBookingByPackageIdAndUserId(packageId, userId)
                    .map(ResponseEntity::ok)
                    .defaultIfEmpty(ResponseEntity.notFound().build());
        } else if (bookingId != null && !bookingId.isEmpty()) {
            return bookingService.getBooking(bookingId)
                    .map(ResponseEntity::ok)
                    .defaultIfEmpty(ResponseEntity.notFound().build());
        } else {
            // Throwing InvalidInputException for invalid requests
            throw new InvalidInputException("Invalid request parameters. You must provide either packageId and userId together, or bookingId.");
        }
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<BookingResponseModel>> createBooking(@RequestBody BookingRequestModel bookingRequestModel) {
        return bookingService.createBooking(bookingRequestModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PatchMapping("/{bookingId}/confirm")
    public Mono<ResponseEntity<BookingResponseModel>> confirmBooking(@PathVariable String bookingId) {
        return bookingService.confirmBooking(bookingId)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{bookingId}/payment/pending")
    public Mono<ResponseEntity<BookingResponseModel>> paymentPending(@PathVariable String bookingId) {
        return bookingService.paymentPending(bookingId)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{bookingId}/payment/tentative2")
    public Mono<ResponseEntity<BookingResponseModel>> paymentTentative2(@PathVariable String bookingId) {
        return bookingService.paymentTentative2(bookingId)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{bookingId}/payment/tentative3")
    public Mono<ResponseEntity<BookingResponseModel>> paymentTentative3(@PathVariable String bookingId) {
        return bookingService.paymentTentative3(bookingId)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{bookingId}/fail")
    public Mono<ResponseEntity<BookingResponseModel>> bookingFailed(@PathVariable String bookingId) {
        return bookingService.bookingFailed(bookingId)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{bookingId}/payment/success")
    public Mono<ResponseEntity<BookingResponseModel>> paymentSuccess(@PathVariable String bookingId) {
        return bookingService.paymentSuccess(bookingId)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{bookingId}/finalize")
    public Mono<ResponseEntity<BookingResponseModel>> finalizeBooking(@PathVariable String bookingId) {
        return bookingService.finalizeBooking(bookingId)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{bookingId}/expire")
    public Mono<ResponseEntity<BookingResponseModel>> expireBooking(@PathVariable String bookingId) {
        return bookingService.expireBooking(bookingId)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{bookingId}/refund")
    public Mono<ResponseEntity<BookingResponseModel>> refundBooking(@PathVariable String bookingId) {
        return bookingService.refundBooking(bookingId)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{bookingId}/cancel")
    public Mono<ResponseEntity<BookingResponseModel>> cancelBooking(@PathVariable String bookingId) {
        return bookingService.cancelBooking(bookingId)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasAuthority('write:bookings')")
    public Mono<Void> deleteBooking(@PathVariable String bookingId) {
        return bookingService.deleteBooking(bookingId).then();
    }
}
