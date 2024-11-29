package com.traveltrove.betraveltrove.dataaccess.tour;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface TourRepository extends ReactiveMongoRepository<Tour, String> {

    public Mono<Tour> findTourByTourId(String tourId);
}
