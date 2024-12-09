package com.traveltrove.betraveltrove.business.event;

import com.traveltrove.betraveltrove.dataaccess.events.Event;
import com.traveltrove.betraveltrove.dataaccess.events.EventRepository;
import com.traveltrove.betraveltrove.presentation.events.EventRequestModel;
import com.traveltrove.betraveltrove.presentation.events.EventResponseModel;
import com.traveltrove.betraveltrove.utils.EventEntityModel;
import com.traveltrove.betraveltrove.utils.exceptions.InvalidInputException;
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
            return eventRepository.findAllByCountryId(countryId)
                    .map(EventEntityModel::toEventResponseModel);
        }
        // Return all events if no filters are provided
        return eventRepository.findAll()
                .map(EventEntityModel::toEventResponseModel);
    }

    @Override
    public Mono<EventResponseModel> createEvent(Mono<EventRequestModel> eventRequestModel) {
        return eventRequestModel
                .switchIfEmpty(Mono.defer(() -> Mono.error(new InvalidInputException("Event request model is empty"))))
                .flatMap(requestModel -> {
                    if (requestModel.getName() == null || requestModel.getName().isBlank()) {
                        return Mono.error(new InvalidInputException("Event name is required"));
                    }
                    if (requestModel.getDescription() == null || requestModel.getDescription().isBlank()) {
                        return Mono.error(new InvalidInputException("Event description is required"));
                    }
                    // cityId and countryId are optional, so they are not validated here
                    return Mono.just(EventEntityModel.toEventEntity(requestModel));
                })
                .flatMap(eventRepository::save)
                .map(EventEntityModel::toEventResponseModel);
    }


<<<<<<< HEAD
    @Override
    public Mono<EventResponseModel> updateEvent(String eventId, Mono<EventRequestModel> eventRequestModel) {
        return eventRepository.findEventByEventId(eventId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Event id not found: " + eventId))))
                .flatMap(foundEvent -> eventRequestModel
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new InvalidInputException("Event request model is empty"))))
                        .flatMap(requestModel -> {
                            if (requestModel.getName() == null || requestModel.getName().isBlank()) {
                                return Mono.error(new InvalidInputException("Event name is required"));
                            }
                            if (requestModel.getDescription() == null || requestModel.getDescription().isBlank()) {
                                return Mono.error(new InvalidInputException("Event description is required"));
                            }
                            if (requestModel.getCityId() == null || requestModel.getCityId().isBlank()) {
                                return Mono.error(new InvalidInputException("City ID is required"));
                            }
                            if (requestModel.getCountryId() == null || requestModel.getCountryId().isBlank()) {
                                return Mono.error(new InvalidInputException("Country ID is required"));
                            }
                            return Mono.just(EventEntityModel.toEventEntity(requestModel))
                                    .doOnNext(event -> event.setEventId(foundEvent.getEventId()))
                                    .doOnNext(event -> event.setId(foundEvent.getId()));
                        }))
                .flatMap(eventRepository::save)
                .map(EventEntityModel::toEventResponseModel);
    }
=======
@Override
public Mono<EventResponseModel> updateEvent(String eventId, Mono<EventRequestModel> eventRequestModel) {
    return eventRepository.findEventByEventId(eventId)
            .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Event id not found: " + eventId))))
            .flatMap(foundEvent -> eventRequestModel
                    .switchIfEmpty(Mono.defer(() -> Mono.error(new InvalidInputException("Event request model is empty"))))
                    .flatMap(requestModel -> {
                        if (requestModel.getName() == null || requestModel.getName().isBlank()) {
                            return Mono.error(new InvalidInputException("Event name is required"));
                        }
                        if (requestModel.getDescription() == null || requestModel.getDescription().isBlank()) {
                            return Mono.error(new InvalidInputException("Event description is required"));
                        }
                        if (requestModel.getCityId() == null || requestModel.getCityId().isBlank()) {
                            return Mono.error(new InvalidInputException("City ID is required"));
                        }
                        if (requestModel.getCountryId() == null || requestModel.getCountryId().isBlank()) {
                            return Mono.error(new InvalidInputException("Country ID is required"));
                        }
                        return Mono.just(EventEntityModel.toEventEntity(requestModel))
                                .doOnNext(event -> event.setEventId(foundEvent.getEventId()))
                                .doOnNext(event -> event.setId(foundEvent.getId()));
                    }))
            .flatMap(eventRepository::save)
            .map(EventEntityModel::toEventResponseModel);
}
>>>>>>> f576ce1a2f8d1ab68f7872032570f00efd806719

    @Override
    public Mono<EventResponseModel> deleteEvent(String eventId) {
        return eventRepository.findEventByEventId(eventId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Event id not found: " + eventId))))
                .flatMap(event -> eventRepository.delete(event).thenReturn(event))
                .map(EventEntityModel::toEventResponseModel);
    }
}