package com.traveltrove.betraveltrove.dataaccess.tour;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventRepository extends ReactiveMongoRepository<Event, String> {
    public Mono<Event> findEventByEventId(String eventId);
    public Mono<Event> findEventByEventIdAndCityId(String eventId, String cityId);
    public Flux<Event> findAllEventsByCityId(String cityId);
    public Mono<Event> findEventByEventIdAndCityIdAndTourId(String eventId, String cityId, String tourId);
    public Flux<Event> findAllEventsByCityIdAndTourId(String cityId, String tourId);
}
