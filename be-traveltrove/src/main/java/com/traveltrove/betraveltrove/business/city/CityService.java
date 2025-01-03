package com.traveltrove.betraveltrove.business.city;

import com.traveltrove.betraveltrove.presentation.city.CityRequestModel;
import com.traveltrove.betraveltrove.presentation.city.CityResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CityService {
    public Mono<CityResponseModel> addCity(CityRequestModel cityRequestModel);
    public Mono<CityResponseModel> getCityById(String cityId);
    public Flux<CityResponseModel> getAllCities();
    public Mono<CityResponseModel> updateCity(String cityId, CityRequestModel cityRequestModel);
    public Mono<Void> deleteCityByCityId(String cityId);
    public Flux<CityResponseModel> getAllCitiesByCountryId(String countryId);

    public  Mono<Boolean> isCityExistsById(String cityId);
}
