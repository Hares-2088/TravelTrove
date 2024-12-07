package com.traveltrove.betraveltrove.business.event;

import com.traveltrove.betraveltrove.presentation.events.EventRequestModel;
import com.traveltrove.betraveltrove.presentation.events.EventResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventService {

    Mono<EventResponseModel> getEventByEventId(String eventId);

    Flux<EventResponseModel> getEvents();
    Flux<EventResponseModel> getEventsByCityId(String cityId);
    Flux<EventResponseModel> getEventsByCountryId(String countryId);

    Mono<EventResponseModel> createEvent(Mono<EventRequestModel> event);

    Mono<EventResponseModel> updateEvent(String eventId, Mono<EventRequestModel> event);

    Mono<Void> deleteEvent(String eventId);
}
