package com.traveltrove.betraveltrove.utils.entitymodels;

import com.traveltrove.betraveltrove.presentation.events.EventRequestModel;
import com.traveltrove.betraveltrove.presentation.events.EventResponseModel;
import com.traveltrove.betraveltrove.dataaccess.events.Event;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

public class EventEntityModel {

    // Method to convert an Event entity to an EventResponseModel
    public static EventResponseModel toEventResponseModel(Event event) {
        EventResponseModel eventResponseModel = new EventResponseModel();
        BeanUtils.copyProperties(event, eventResponseModel);

        if (event.getEventId() != null) {
            eventResponseModel.setEventId(event.getEventId());
        }
        if (event.getCityId() != null) {
            eventResponseModel.setCityId(event.getCityId());
        }
        if (event.getCountryId() != null) {
            eventResponseModel.setCountryId(event.getCountryId());
        }

        return eventResponseModel;
    }

    // Method to map an EventRequestModel to an Event entity
    public static Event toEventEntity(EventRequestModel eventRequestModel) {
        return Event.builder()
                .eventId(generateUUIDString()) // Generate a unique eventId
                .cityId(eventRequestModel.getCityId())
                .countryId(eventRequestModel.getCountryId())
                .name(eventRequestModel.getName())
                .description(eventRequestModel.getDescription())
                .image(eventRequestModel.getImage())
                .build();
    }

    // Utility method to generate a UUID string
    private static String generateUUIDString() {
        return UUID.randomUUID().toString();
    }
}
