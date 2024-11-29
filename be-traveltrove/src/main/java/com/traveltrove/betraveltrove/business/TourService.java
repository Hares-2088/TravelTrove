package com.traveltrove.betraveltrove.business;

import com.traveltrove.betraveltrove.presentation.TourResponseModel;
import reactor.core.publisher.Flux;

public interface TourService {

    Flux<TourResponseModel> getTours();
}
