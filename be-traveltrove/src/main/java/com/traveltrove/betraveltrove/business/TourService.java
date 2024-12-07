package com.traveltrove.betraveltrove.business;

import com.traveltrove.betraveltrove.presentation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TourService {

    Flux<TourResponseModel> getTours();
    Mono<TourResponseModel> getTourByTourId(String tourId);
}
