package com.traveltrove.betraveltrove.dataaccess.tour;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TourRepository extends ReactiveMongoRepository<Tour, String> {

    Mono<Tour> findTourByTourId(String tourId);

}
