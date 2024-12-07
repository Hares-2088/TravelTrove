package com.traveltrove.betraveltrove.business.tour;

import com.traveltrove.betraveltrove.presentation.*;
import com.traveltrove.betraveltrove.presentation.tour.TourResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TourService {

    Flux<TourResponseModel> getTours();
    Mono<TourResponseModel> getTourByTourId(String tourId);
    Mono<TourResponseModel> addTour(Mono<TourRequestModel> tourRequestModel);
    Mono<CityResponseModel> addCityToTour(String tourId, Mono<CityRequestModel> cityRequestModel);
    Mono<EventResponseModel> addEventToCity(String tourId, String cityId, Mono<EventRequestModel> eventRequestModel);
}
