package com.traveltrove.betraveltrove.utils;

import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import com.traveltrove.betraveltrove.presentation.CityResponseModel;
import com.traveltrove.betraveltrove.presentation.EventResponseModel;
import com.traveltrove.betraveltrove.presentation.TourRequestModel;
import com.traveltrove.betraveltrove.presentation.TourResponseModel;
import org.springframework.beans.BeanUtils;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class EntityModelUtil {

    // This method is to convert a Tour object to a TourResponseModel object
    public static TourResponseModel toTourResponseModel(Tour tour) {
        TourResponseModel tourResponseModel = new TourResponseModel();
        BeanUtils.copyProperties(tour, tourResponseModel);

        // Convert LocalDate to String
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        if (tour.getStartDate() != null) {
            tourResponseModel.setStartDate(tour.getStartDate().format(formatter));
        }
        if (tour.getEndDate() != null) {
            tourResponseModel.setEndDate(tour.getEndDate().format(formatter));
        }

        // Map cities
        if (tour.getCities() != null) {
            tourResponseModel.setCities(
                    tour.getCities().stream()
                            .map(city -> CityResponseModel.builder()
                                    .cityId(city.getCityId())
                                    .name(city.getName())
                                    .description(city.getDescription())
                                    .image(city.getImage())
                                    .startDate(city.getStartDate())
                                    .hotel(city.getHotel())
                                    .events(city.getEvents() != null ? city.getEvents().stream()
                                            .map(event -> EventResponseModel.builder()
                                                    .eventId(event.getEventId())
                                                    .name(event.getName())
                                                    .description(event.getDescription())
                                                    .image(event.getImage())
                                                    .startDate(event.getStartDate())
                                                    .gatheringTime(event.getGatheringTime())
                                                    .departureTime(event.getDepartureTime())
                                                    .endTime(event.getEndTime())
                                                    .build())
                                            .toList() : null)
                                    .build())
                            .toList()
            );
        }

        return tourResponseModel;
    }


    // This method is to convert a request model to a Tour entity
    public static Tour toTourEntity(TourRequestModel tourRequestModel) {
        return Tour.builder()
                .tourId(generateUUIDString())
                .name(tourRequestModel.getName())
                .startDate(tourRequestModel.getStartDate())
                .endDate(tourRequestModel.getEndDate())
                .overallDescription(tourRequestModel.getOverallDescription())
                .price(tourRequestModel.getPrice())
                .spotsAvailable(tourRequestModel.getSpotsAvailable())
                .image(tourRequestModel.getImage())
                .itineraryPicture(tourRequestModel.getItineraryPicture())
                .build();
    }

    public static String generateUUIDString() {
        return UUID.randomUUID().toString();
    }

}
