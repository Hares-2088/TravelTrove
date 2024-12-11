package com.traveltrove.betraveltrove.business.airport;

import com.traveltrove.betraveltrove.dataaccess.airport.Airport;
import com.traveltrove.betraveltrove.presentation.airport.AirportRequestModel;
import com.traveltrove.betraveltrove.presentation.airport.AirportResponseModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



public interface AirportService {
    Flux<AirportResponseModel> getAllAirports();

    Mono<AirportResponseModel> getAirportById(String id);

    Mono<AirportResponseModel> addAirport(AirportRequestModel airportRequestModel);

    Mono<AirportResponseModel> updateAirport(String id, AirportRequestModel airportRequestModel);

    Mono<Void> deleteAirport(String id);
}
