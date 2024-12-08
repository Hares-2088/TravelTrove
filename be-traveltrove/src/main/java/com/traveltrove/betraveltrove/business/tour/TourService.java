package com.traveltrove.betraveltrove.business.tour;

import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import com.traveltrove.betraveltrove.presentation.tour.TourRequestModel;
import com.traveltrove.betraveltrove.presentation.tour.TourResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TourService {

    Flux<TourResponseModel> getTours();
    Mono<TourResponseModel> getTourByTourId(String tourId);
    Mono<TourResponseModel> addTour(Tour tour);
    Mono<TourResponseModel> updateTour(String tourId, TourRequestModel tourRequestModel);
    Mono<Void> deleteTourByTourId(String tourId);
}
