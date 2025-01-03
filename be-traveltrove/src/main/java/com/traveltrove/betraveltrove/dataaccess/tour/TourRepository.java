package com.traveltrove.betraveltrove.dataaccess.tour;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TourRepository extends ReactiveMongoRepository<Tour, String> {

    Mono<Tour> findTourByTourId(String tourId);

}
