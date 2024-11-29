package com.traveltrove.betraveltrove.utils;

import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import com.traveltrove.betraveltrove.presentation.TourRequestModel;
import com.traveltrove.betraveltrove.presentation.TourResponseModel;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

public class EntityModelUtil {

    // This method is to convert a Tour object to a TourResponseModel object
    public static TourResponseModel toTourResponseModel(Tour tour) {
        TourResponseModel tourResponseModel = new TourResponseModel();
        BeanUtils.copyProperties(tour, tourResponseModel);
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
