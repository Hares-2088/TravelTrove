package com.traveltrove.betraveltrove.dataaccess.tour;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TourEventRepository extends ReactiveMongoRepository<TourEvent, String> {
    Flux<TourEvent> findAllByTourId(String tourId);
    Mono<TourEvent> findByTourEventId(String tourEventId);
    Mono<Void> deleteByTourEventId(String tourEventId);
}
