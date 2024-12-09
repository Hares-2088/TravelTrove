package com.traveltrove.betraveltrove.dataaccess.tour;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TourEventsRepository extends ReactiveMongoRepository<TourEvents, String> {
    Mono<TourEvents> findByTourId(String tourId);
    Mono<TourEvents> findByToursEventId(String toursEventId);

}
