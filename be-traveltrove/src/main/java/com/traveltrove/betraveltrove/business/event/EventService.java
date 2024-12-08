package com.traveltrove.betraveltrove.business.event;

import com.traveltrove.betraveltrove.presentation.events.EventRequestModel;
import com.traveltrove.betraveltrove.presentation.events.EventResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventService {

    Mono<EventResponseModel> getEventByEventId(String eventId);

    /**
     * Fetch all events or filter by cityId and countryId if provided.
     *
     * @param cityId optional city ID to filter events
     * @param countryId optional country ID to filter events
     * @return a Flux of EventResponseModel matching the criteria
     */
    Flux<EventResponseModel> getEvents(String cityId, String countryId);

    Mono<EventResponseModel> createEvent(Mono<EventRequestModel> event);

    Mono<EventResponseModel> updateEvent(String eventId, Mono<EventRequestModel> event);

    Mono<EventResponseModel> deleteEvent(String eventId);
}
