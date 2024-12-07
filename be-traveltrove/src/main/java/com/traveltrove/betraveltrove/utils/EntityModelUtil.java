package com.traveltrove.betraveltrove.utils;

import com.traveltrove.betraveltrove.dataaccess.country.Country;
import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import com.traveltrove.betraveltrove.presentation.CityResponseModel;
import com.traveltrove.betraveltrove.presentation.events.EventResponseModel;
import com.traveltrove.betraveltrove.presentation.tour.TourRequestModel;
import com.traveltrove.betraveltrove.presentation.tour.TourResponseModel;
import com.traveltrove.betraveltrove.presentation.country.CountryRequestModel;
import com.traveltrove.betraveltrove.presentation.country.CountryResponseModel;
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

    // Method to map a Country entity to a CountryResponseModel
    public static CountryResponseModel toCountryResponseModel(Country country) {
        CountryResponseModel countryResponseModel = new CountryResponseModel();
        BeanUtils.copyProperties(country, countryResponseModel);
        return countryResponseModel;
    }

    // Method to map a CountryRequestModel to a Country entity
    public static Country toCountryEntity(CountryRequestModel countryRequestModel) {
        return Country.builder()
                .countryId(generateUUIDString())
                .name(countryRequestModel.getName())
                .image(countryRequestModel.getImage())
                .build();
    }

    public static String generateUUIDString() {
        return UUID.randomUUID().toString();
    }

}
