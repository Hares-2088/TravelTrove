package com.traveltrove.betraveltrove.dataaccess.events;

import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventRepository extends ReactiveMongoRepository<Event, String> {

        public Mono<Event> findEventByEventId(String eventId);
        public Flux<Event> findAllByCityId(String cityId);
        public Flux<Event> findAllByCountryId(String countryId);
}