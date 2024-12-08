package com.traveltrove.betraveltrove.utils;

import com.traveltrove.betraveltrove.dataaccess.country.Country;
import com.traveltrove.betraveltrove.dataaccess.city.City;
import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import com.traveltrove.betraveltrove.dataaccess.tour.TourEvent;
import com.traveltrove.betraveltrove.dataaccess.tour.TourEventRepository;
import com.traveltrove.betraveltrove.presentation.city.CityRequestModel;
import com.traveltrove.betraveltrove.presentation.city.CityResponseModel;
import com.traveltrove.betraveltrove.presentation.tour.TourEventRequestModel;
import com.traveltrove.betraveltrove.presentation.tour.TourEventResponseModel;
import com.traveltrove.betraveltrove.presentation.tour.TourRequestModel;
import com.traveltrove.betraveltrove.presentation.tour.TourResponseModel;
import com.traveltrove.betraveltrove.presentation.country.CountryRequestModel;
import com.traveltrove.betraveltrove.presentation.country.CountryResponseModel;
import org.springframework.beans.BeanUtils;
import reactor.core.publisher.Mono;

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
                .description(tourRequestModel.getDescription())
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

    public static CityResponseModel toCityResponseModel(City city) {
        CityResponseModel cityResponseModel = new CityResponseModel();
        BeanUtils.copyProperties(city, cityResponseModel);
        return cityResponseModel;
    }

    public static City toCityEntity(CityRequestModel cityRequestModel) {
        return City.builder()
                .cityId(generateUUIDString())
                .name(cityRequestModel.getName())
                .countryId(cityRequestModel.getCountryId())
                .build();
    }

    public static TourEvent toTourEventEntity(TourEventRequestModel tourEventRequestModel) {
        return TourEvent.builder()
                .tourEventId(generateUUIDString())
                .seq(tourEventRequestModel.getSeq())
                .seqDesc(tourEventRequestModel.getSeqDesc())
                .tourId(tourEventRequestModel.getTourId())
                .eventId(tourEventRequestModel.getEventId())
                .build();
    }

    public static TourEventResponseModel toTourEventResponseModel(TourEvent tourEvent) {
        TourEventResponseModel tourEventResponseModel = new TourEventResponseModel();
        BeanUtils.copyProperties(tourEvent, tourEventResponseModel);
        return tourEventResponseModel;
    }

    public static String generateUUIDString() {
        return UUID.randomUUID().toString();
    }

}
