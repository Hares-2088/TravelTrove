package com.traveltrove.betraveltrove.dataaccess.traveler;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TravelerRepository extends ReactiveMongoRepository<Traveler, String> {

    public Mono<Traveler> findTravelerByTravelerId(String travelerId);
    public Flux<Traveler> findTravelerByFirstName(String firstName);
}
