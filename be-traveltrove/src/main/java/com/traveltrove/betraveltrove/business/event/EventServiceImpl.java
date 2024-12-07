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
    public Flux<EventResponseModel> getEvents() {
        return eventRepository.findAll()
                .map(EventEntityModel::toEventResponseModel);
    }

    @Override
    public Flux<EventResponseModel> getEventsByCityId(String cityId) {
        return eventRepository.findAllByCityId(cityId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("City id not found: " + cityId))))
                .map(EventEntityModel::toEventResponseModel);
    }

    @Override
    public Flux<EventResponseModel> getEventsByCountryId(String countryId) {
        return eventRepository.findByCountryId(countryId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Country id not found: " + countryId))))
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
                        //the country id and city id are nullable
                        .doOnNext(event -> event.setEventId(foundEvent.getEventId()))
                        .doOnNext(event -> event.setId(foundEvent.getId()))
                        .doOnNext(event -> event.setCountryId(foundEvent.getCountryId()))
                        .doOnNext(event -> event.setCityId(foundEvent.getCityId())))
                .flatMap(eventRepository::save)
                .map(EventEntityModel::toEventResponseModel);
    }

    @Override
    public Mono<Void> deleteEvent(String eventId) {
        return eventRepository.findEventByEventId(eventId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Event id not found: " + eventId))))
                .flatMap(eventRepository::delete);
    }
}
