package com.traveltrove.betraveltrove.business.country;

import com.traveltrove.betraveltrove.dataaccess.country.Country;
import com.traveltrove.betraveltrove.presentation.country.CountryRequestModel;
import com.traveltrove.betraveltrove.presentation.country.CountryResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CountryService {
    Flux<CountryResponseModel> getAllCountries();
    Mono<CountryResponseModel> getCountryById(String id);
    Mono<CountryResponseModel> addCountry(Country country);
    Mono<CountryResponseModel> updateCountry(String id, CountryRequestModel countryRequestModel);
    Mono<Void> deleteCountry(String id);
}