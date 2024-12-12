package com.traveltrove.betraveltrove.business.hotel;

import com.traveltrove.betraveltrove.presentation.hotel.HotelRequestModel;
import com.traveltrove.betraveltrove.presentation.hotel.HotelResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HotelService {

    Mono<HotelResponseModel> getHotelByHotelId(String hotelId);

    /**
     * Fetch all hotels or filter by cityId if provided.
     *
     * @param cityId optional city ID to filter hotels
     * @return a Flux of HotelResponseModel matching the criteria
     */
    Flux<HotelResponseModel> getHotels(String cityId);

    Mono<HotelResponseModel> createHotel(Mono<HotelRequestModel> hotelRequestModel);

    Mono<HotelResponseModel> updateHotel(String hotelId, Mono<HotelRequestModel> hotelRequestModel);

    Mono<Void> deleteHotel(String hotelId);
}
