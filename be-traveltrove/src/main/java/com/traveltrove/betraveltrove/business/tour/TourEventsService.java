package com.traveltrove.betraveltrove.business.tour;

import com.traveltrove.betraveltrove.dataaccess.tour.TourEvents;
import com.traveltrove.betraveltrove.presentation.tour.TourEventsRequestModel;
import com.traveltrove.betraveltrove.presentation.tour.TourEventsResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TourEventsService {
    Flux<TourEventsResponseModel> getAllTourEvents();
    Mono<TourEventsResponseModel> getTourEventsByTourId(String tourId); // New Method
    Mono<TourEventsResponseModel> addTourEvent(TourEvents tourEvents);

    Mono<TourEventsResponseModel> updateTourEvent(String toursEventId, TourEventsRequestModel request);
    Mono<Void> deleteTourEvent(String toursEventId);
}
