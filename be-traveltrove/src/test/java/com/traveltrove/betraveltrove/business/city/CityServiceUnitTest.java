package com.traveltrove.betraveltrove.business.city;

import com.traveltrove.betraveltrove.dataaccess.city.City;
import com.traveltrove.betraveltrove.dataaccess.city.CityRepository;
import com.traveltrove.betraveltrove.presentation.city.CityRequestModel;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
public class CityServiceUnitTest {

    @InjectMocks
    private CityServiceImpl cityService;

    @Mock
    private CityRepository cityRepository;

    @Test
    public void whenGetAllCities_thenReturnAllCities() {
        String cityId1 = UUID.randomUUID().toString();
        String cityId2 = UUID.randomUUID().toString();
        String cityId3 = UUID.randomUUID().toString();

        City city1 = new City("1", cityId1, "New York", "1");
        City city2 = new City("2", cityId2, "Toronto", "2");
        City city3 = new City("3", cityId3, "Paris", "3");

        Mockito.when(cityRepository.findAll()).thenReturn(Flux.just(city1, city2, city3));

        StepVerifier.create(cityService.getAllCities())
                .expectNextMatches(response ->
                        response.getCityId().equals(cityId1) &&
                                response.getName().equals("New York") &&
                                response.getCountryId().equals("1")
                )
                .expectNextMatches(response ->
                        response.getCityId().equals(cityId2) &&
                                response.getName().equals("Toronto") &&
                                response.getCountryId().equals("2")
                )
                .expectNextMatches(response ->
                        response.getCityId().equals(cityId3) &&
                                response.getName().equals("Paris") &&
                                response.getCountryId().equals("3")
                )
                .verifyComplete();
    }

    @Test
    public void whenGetCityById_withExistingId_thenReturnExistingCity() {
        String cityId = UUID.randomUUID().toString();
        City city = new City("1", cityId, "New York", "1");

        Mockito.when(cityRepository.findCityByCityId(cityId)).thenReturn(Mono.just(city));

        StepVerifier.create(cityService.getCityById(cityId))
                .expectNextMatches(response -> response.getCityId().equals(cityId))
                .verifyComplete();
    }

    @Test
    public void whenGetCityById_withNonExistingId_thenReturnNotFound() {
        String cityId = UUID.randomUUID().toString();

        Mockito.when(cityRepository.findCityByCityId(cityId)).thenReturn(Mono.empty());

        StepVerifier.create(cityService.getCityById(cityId))
                .expectErrorMatches(error -> error instanceof NotFoundException &&
                        error.getMessage().equals("City id not found: " + cityId))
                .verify();
    }

//    @Test
//    public void whenAddCity_thenReturnSavedCity() {
//        String cityId = "743de72e-d796-43ed-8451-34946a29d5f3";
//        City city = new City(null, cityId, "New York", "1");
//        CityRequestModel cityRequest = new CityRequestModel("New York", "1");
//
//        Mockito.when(cityRepository.save(city)).thenReturn(Mono.just(city));
//
//        StepVerifier.create(cityService.addCity(cityRequest))
//                .expectNextMatches(response -> response.getCityId().equals(cityId))
//                .verifyComplete();
//    }

    @Test
    public void whenUpdateCity_withExistingId_thenReturnUpdatedCity() {
        String cityId = UUID.randomUUID().toString();
        City existingCity = new City("1", cityId, "Old Name", "11");
        CityRequestModel updatedRequest = new CityRequestModel("Updated Name", "22");
        City updatedCity = new City("1", cityId, "Updated Name", "22");

        Mockito.when(cityRepository.findCityByCityId(cityId)).thenReturn(Mono.just(existingCity));
        Mockito.when(cityRepository.save(existingCity)).thenReturn(Mono.just(updatedCity));

        StepVerifier.create(cityService.updateCity(cityId, updatedRequest))
                .expectNextMatches(response -> response.getName().equals("Updated Name"))
                .verifyComplete();
    }

    @Test
    public void whenUpdateCity_withNonExistingId_thenReturnNotFound() {
        String cityId = UUID.randomUUID().toString();
        CityRequestModel updatedRequest = new CityRequestModel("Updated Name", "11");

        Mockito.when(cityRepository.findCityByCityId(cityId)).thenReturn(Mono.empty());

        StepVerifier.create(cityService.updateCity(cityId, updatedRequest))
                .expectErrorMatches(error -> error instanceof NotFoundException &&
                        error.getMessage().equals("City id not found: " + cityId))
                .verify();
    }

    @Test
    public void whenDeleteCity_withExistingId_thenCompleteSuccessfully() {
        String cityId = UUID.randomUUID().toString();
        City city = new City("1", cityId, "New York", "1");

        Mockito.when(cityRepository.findCityByCityId(cityId)).thenReturn(Mono.just(city));
        Mockito.when(cityRepository.delete(city)).thenReturn(Mono.empty());

        StepVerifier.create(cityService.deleteCityByCityId(cityId))
                .verifyComplete();
    }

    @Test
    public void whenDeleteCity_withNonExistingId_thenReturnNotFound() {
        String cityId = UUID.randomUUID().toString();

        Mockito.when(cityRepository.findCityByCityId(cityId)).thenReturn(Mono.empty());

        StepVerifier.create(cityService.deleteCityByCityId(cityId))
                .expectErrorMatches(error -> error instanceof NotFoundException &&
                        error.getMessage().equals("City id not found: " + cityId))
                .verify();
    }
    
}
