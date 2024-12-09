package com.traveltrove.betraveltrove.business.tour;

import com.traveltrove.betraveltrove.dataaccess.tour.TourEvent;
import com.traveltrove.betraveltrove.presentation.tour.TourEventRequestModel;
import com.traveltrove.betraveltrove.presentation.tour.TourEventResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TourEventService {
    Flux<TourEventResponseModel> getAllTourEvents();
    Flux<TourEventResponseModel> getTourEventsByTourId(String tourId);
    Mono<TourEventResponseModel> getTourEventByTourEventId(String tourEventId);
    Mono<TourEventResponseModel> addTourEvent(TourEvent tourEvent);
    Mono<TourEventResponseModel> updateTourEvent(String tourEventId, TourEventRequestModel request);
    Mono<Void> deleteTourEvent(String tourEventId);
}
