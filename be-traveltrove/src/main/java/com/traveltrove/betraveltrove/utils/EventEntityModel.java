package com.traveltrove.betraveltrove.utils;

import com.traveltrove.betraveltrove.presentation.events.EventRequestModel;
import com.traveltrove.betraveltrove.presentation.events.EventResponseModel;
import org.springframework.beans.BeanUtils;
import com.traveltrove.betraveltrove.dataaccess.events.Event;

public class EventEntityModel {

    //this method is to convert an event entity to an event response model object
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

        if (event.getStartDate() != null) {
            eventResponseModel.setStartDate(event.getStartDate());
        }
        if (event.getGatheringTime() != null) {
            eventResponseModel.setGatheringTime(event.getGatheringTime());
        }
        if (event.getDepartureTime() != null) {
            eventResponseModel.setDepartureTime(event.getDepartureTime());
        }
        if (event.getEndTime() != null) {
            eventResponseModel.setEndTime(event.getEndTime());
        }

        return eventResponseModel;
    }

    //this method is to convert an event response model object to an event entity
    public static Event toEventEntity(EventRequestModel eventRequestModel) {
        Event event = new Event();
        BeanUtils.copyProperties(eventRequestModel, event);

        if (eventRequestModel.getCityId() != null) {
            event.setCityId(eventRequestModel.getCityId());
        }

        if (eventRequestModel.getCountryId() != null) {
            event.setCountryId(eventRequestModel.getCountryId());
        }

        if (eventRequestModel.getStartDate() != null) {
            event.setStartDate(eventRequestModel.getStartDate());
        }
        if (eventRequestModel.getGatheringTime() != null) {
            event.setGatheringTime(eventRequestModel.getGatheringTime());
        }
        if (eventRequestModel.getDepartureTime() != null) {
            event.setDepartureTime(eventRequestModel.getDepartureTime());
        }
        if (eventRequestModel.getEndTime() != null) {
            event.setEndTime(eventRequestModel.getEndTime());
        }

        return event;
    }
}
