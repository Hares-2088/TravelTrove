package com.traveltrove.betraveltrove.presentation.booking;

import com.traveltrove.betraveltrove.business.booking.BookingService;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import com.traveltrove.betraveltrove.utils.exceptions.InvalidInputException;
import com.traveltrove.betraveltrove.utils.exceptions.InvalidStatusException;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import com.traveltrove.betraveltrove.utils.exceptions.SameStatusException;
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

    @GetMapping(value = "/booking", produces = MediaType.APPLICATION_JSON_VALUE)
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
                .onErrorResume(NotFoundException.class, e -> {
                    log.error("User or Package Not Found: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
                })
                .onErrorResume(InvalidStatusException.class, e -> {
                    log.error("Invalid Booking Status: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(null));
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("Unhandled Error: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                });
    }

    @PatchMapping("/{bookingId}")
    public Mono<ResponseEntity<BookingResponseModel>> updateBookingStatus(
            @PathVariable String bookingId,
            @RequestBody BookingStatusUpdateRequest statusUpdateRequest) {
        return bookingService.updateBookingStatus(bookingId, statusUpdateRequest.getStatus())
                .map(ResponseEntity::ok)
                .onErrorResume(NotFoundException.class, e -> {
                    log.error("Booking not found: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                })
                .onErrorResume(SameStatusException.class, e -> {
                    log.error("Invalid status transition: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("Unexpected error: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @DeleteMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasAuthority('write:bookings')")
    public Mono<Void> deleteBooking(@PathVariable String bookingId) {
        return bookingService.deleteBooking(bookingId).then();
    }
}
