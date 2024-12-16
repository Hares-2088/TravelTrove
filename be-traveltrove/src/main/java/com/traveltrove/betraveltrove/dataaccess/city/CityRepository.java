package com.traveltrove.betraveltrove.dataaccess.city;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CityRepository extends ReactiveMongoRepository<City, String> {
    public Mono<City> findCityByCityId(String cityId);
    public Flux<City> findAllCitiesByCountryId(String countryId);
}
