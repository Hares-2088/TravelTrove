package com.traveltrove.betraveltrove.dataaccess.booking;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingRepository extends ReactiveMongoRepository<Booking, String> {

    Flux<Booking> findBookingsByPackageId(String packageId);
    Flux<Booking> findBookingsByUserId(String userId);
    Flux<Booking> findBookingsByStatus(BookingStatus status);

    Mono<Booking> findBookingByBookingId(String bookingId);
    Mono<Booking> findBookingByPackageIdAndUserId(String packageId,String userId);

}
