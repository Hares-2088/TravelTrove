package com.traveltrove.betraveltrove.business.city;

import com.traveltrove.betraveltrove.dataaccess.city.CityRepository;
import com.traveltrove.betraveltrove.presentation.city.CityRequestModel;
import com.traveltrove.betraveltrove.presentation.city.CityResponseModel;
import com.traveltrove.betraveltrove.utils.EntityModelUtil;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;

    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }


    @Override
    public Mono<CityResponseModel> addCity(CityRequestModel cityRequestModel) {
        return cityRepository.save(EntityModelUtil.toCityEntity(cityRequestModel))
                .map(EntityModelUtil::toCityResponseModel);
    }

    @Override
    public Mono<CityResponseModel> getCityById(String cityId) {
        return cityRepository.findCityByCityId(cityId)
                .switchIfEmpty(Mono.error(new NotFoundException("City id not found: " + cityId)))
                .map(EntityModelUtil::toCityResponseModel);
    }

    @Override
    public Flux<CityResponseModel> getAllCities() {
        return cityRepository.findAll()
                .map(EntityModelUtil::toCityResponseModel);
    }

    @Override
    public Mono<CityResponseModel> updateCity(String cityId, CityRequestModel cityRequestModel) {
        return cityRepository.findCityByCityId(cityId)
                .switchIfEmpty(Mono.error(new NotFoundException("City id not found: " + cityId)))
                .flatMap(existingCity -> {
                    existingCity.setName(cityRequestModel.getName());
                    existingCity.setCountryId(cityRequestModel.getCountryId());
                    return cityRepository.save(existingCity);
                })
                .map(EntityModelUtil::toCityResponseModel);
    }

    @Override
    public Mono<Void> deleteCityByCityId(String cityId) {
        return cityRepository.findCityByCityId(cityId)
                .switchIfEmpty(Mono.error(new NotFoundException("City id not found: " + cityId)))
                .flatMap(cityRepository::delete)
                .then();
    }

    @Override
    public Flux<CityResponseModel> getAllCitiesByCountryId(String countryId) {
        return cityRepository.findAllCitiesByCountryId(countryId)
                .map(EntityModelUtil::toCityResponseModel);
    }

    @Override
    public Mono<CityResponseModel> getCityByCityIdAndCountryId(String cityId, String countryId) {
        return cityRepository.findCityByCityIdAndCountryId(cityId, countryId)
                .switchIfEmpty(Mono.error(new NotFoundException("City id not found: " + cityId + " and country id not found: " + countryId)))
                .map(EntityModelUtil::toCityResponseModel);
    }
}
