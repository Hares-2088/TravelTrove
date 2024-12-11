package com.traveltrove.betraveltrove.dataaccess.airport;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AirportRepository  extends ReactiveMongoRepository<Airport, String> {
    public Mono<Airport> findAirportByAirportId(String airportId);
//    public Flux<Airport> findAllAirportsByCityId(String cityId);
    public Mono<Void> deleteAirportByAirportId(String airportId);
}
