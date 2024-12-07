package com.traveltrove.betraveltrove.business.event;

import com.traveltrove.betraveltrove.dataaccess.events.Event;
import com.traveltrove.betraveltrove.dataaccess.events.EventRepository;
import com.traveltrove.betraveltrove.presentation.events.EventRequestModel;
import com.traveltrove.betraveltrove.presentation.events.EventResponseModel;
import com.traveltrove.betraveltrove.utils.EventEntityModel;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Mono<EventResponseModel> getEventByEventId(String eventId) {
        return eventRepository.findEventByEventId(eventId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Event id not found: " + eventId))))
                .map(EventEntityModel::toEventResponseModel);
    }

    @Override
    public Flux<EventResponseModel> getEvents(String cityId, String countryId) {
        // Handle filtering logic based on query parameters
        if (cityId != null && !cityId.isEmpty()) {
            return eventRepository.findAllByCityId(cityId)
                    .map(EventEntityModel::toEventResponseModel);
        } else if (countryId != null && !countryId.isEmpty()) {
            return eventRepository.findByCountryId(countryId)
                    .map(EventEntityModel::toEventResponseModel);
        }
        // Return all events if no filters are provided
        return eventRepository.findAll()
                .map(EventEntityModel::toEventResponseModel);
    }

    @Override
    public Mono<EventResponseModel> createEvent(Mono<EventRequestModel> eventRequestModel) {
        return eventRequestModel
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Event request model is empty"))))
                .map(EventEntityModel::toEventEntity)
                .flatMap(eventRepository::save)
                .map(EventEntityModel::toEventResponseModel);
    }

    @Override
    public Mono<EventResponseModel> updateEvent(String eventId, Mono<EventRequestModel> eventRequestModel) {
        return eventRepository.findEventByEventId(eventId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Event id not found: " + eventId))))
                .flatMap(foundEvent -> eventRequestModel
                        .map(EventEntityModel::toEventEntity)
                        .doOnNext(event -> event.setEventId(foundEvent.getEventId()))
                        .doOnNext(event -> event.setId(foundEvent.getId()))
                        .doOnNext(event -> event.setCountryId(foundEvent.getCountryId()))
                        .doOnNext(event -> event.setCityId(foundEvent.getCityId())))
                .flatMap(eventRepository::save)
                .map(EventEntityModel::toEventResponseModel);
    }

    @Override
    public Mono<EventResponseModel> deleteEvent(String eventId) {
        return eventRepository.findEventByEventId(eventId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Event id not found: " + eventId))))
                .flatMap(event -> eventRepository.delete(event).thenReturn(event))
                .map(EventEntityModel::toEventResponseModel);
    }
}
