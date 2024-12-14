package com.traveltrove.betraveltrove.business.traveler;

import com.traveltrove.betraveltrove.presentation.travaler.TravelerRequestModel;
import com.traveltrove.betraveltrove.presentation.travaler.TravelerResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TravelerService {

    public Flux<TravelerResponseModel> getAllTravelers(String firstName);

    public Mono<TravelerResponseModel> getTravelerByTravelerId(String travelerId);

    public Mono<TravelerResponseModel> createTraveler(TravelerRequestModel travelerRequestModel);

    public Mono<TravelerResponseModel> updateTraveler(String travelerId, TravelerRequestModel travelerRequestModel);

    public Mono<TravelerResponseModel> deleteTraveler(String travelerId);
}
