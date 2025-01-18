package com.traveltrove.betraveltrove.dataaccess.airport;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AirportRepository extends ReactiveMongoRepository<Airport, String> {
    Mono<Airport> findAirportByAirportId(String airportId);
}
