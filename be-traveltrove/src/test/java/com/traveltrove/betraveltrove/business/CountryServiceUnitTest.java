package com.traveltrove.betraveltrove.business;

import com.traveltrove.betraveltrove.business.country.CountryServiceImpl;
import com.traveltrove.betraveltrove.dataaccess.country.Country;
import com.traveltrove.betraveltrove.dataaccess.country.CountryRepository;
import com.traveltrove.betraveltrove.presentation.country.CountryRequestModel;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CountryServiceUnitTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryServiceImpl countryService;

    @Test
    public void whenGetAllCountries_thenReturnAllCountries() {
        String countryId1 = UUID.randomUUID().toString();
        String countryId2 = UUID.randomUUID().toString();
        String countryId3 = UUID.randomUUID().toString();

        Country country1 = new Country("1", countryId1, "United States", "us-image.jpg");
        Country country2 = new Country("2", countryId2, "Canada", "ca-image.jpg");
        Country country3 = new Country("3", countryId3, "Mexico", "mx-image.jpg");

        when(countryRepository.findAll()).thenReturn(Flux.just(country1, country2, country3));

        StepVerifier.create(countryService.getAllCountries())
                .expectNextMatches(response ->
                        response.getCountryId().equals(countryId1) &&
                                response.getName().equals("United States") &&
                                response.getImage().equals("us-image.jpg")
                )
                .expectNextMatches(response ->
                        response.getCountryId().equals(countryId2) &&
                                response.getName().equals("Canada") &&
                                response.getImage().equals("ca-image.jpg")
                )
                .expectNextMatches(response ->
                        response.getCountryId().equals(countryId3) &&
                                response.getName().equals("Mexico") &&
                                response.getImage().equals("mx-image.jpg")
                )
                .verifyComplete();
    }

    @Test
    public void whenGetCountryById_withExistingId_thenReturnExistingCountry() {
        String countryId = UUID.randomUUID().toString();
        Country country = new Country("1", countryId, "United States", "us-image.jpg");

        when(countryRepository.findCountryByCountryId(countryId)).thenReturn(Mono.just(country));

        StepVerifier.create(countryService.getCountryById(countryId))
                .expectNextMatches(response -> response.getCountryId().equals(countryId))
                .verifyComplete();
    }

    @Test
    public void whenGetCountryById_withNonExistingId_thenReturnNotFound() {
        String countryId = UUID.randomUUID().toString();

        when(countryRepository.findCountryByCountryId(countryId)).thenReturn(Mono.empty());

        StepVerifier.create(countryService.getCountryById(countryId))
                .expectErrorMatches(error -> error instanceof NotFoundException &&
                        error.getMessage().equals("Country id not found: " + countryId))
                .verify();
    }

    @Test
    public void whenAddCountry_thenReturnSavedCountry() {
        String countryId = UUID.randomUUID().toString();
        Country country = new Country("1", countryId, "United States", "us-image.jpg");

        when(countryRepository.save(country)).thenReturn(Mono.just(country));

        StepVerifier.create(countryService.addCountry(country))
                .expectNextMatches(response -> response.getCountryId().equals(countryId))
                .verifyComplete();
    }

    @Test
    public void whenUpdateCountry_withExistingId_thenReturnUpdatedCountry() {
        String countryId = UUID.randomUUID().toString();
        Country existingCountry = new Country("1", countryId, "Old Name", "old-image.jpg");
        CountryRequestModel updatedRequest = new CountryRequestModel("Updated Name", "updated-image.jpg");
        Country updatedCountry = new Country("1", countryId, "Updated Name", "updated-image.jpg");

        when(countryRepository.findCountryByCountryId(countryId)).thenReturn(Mono.just(existingCountry));
        when(countryRepository.save(existingCountry)).thenReturn(Mono.just(updatedCountry));

        StepVerifier.create(countryService.updateCountry(countryId, updatedRequest))
                .expectNextMatches(response -> response.getName().equals("Updated Name"))
                .verifyComplete();
    }

    @Test
    public void whenUpdateCountry_withNonExistingId_thenReturnNotFound() {
        String countryId = UUID.randomUUID().toString();
        CountryRequestModel updatedRequest = new CountryRequestModel("Updated Name", "updated-image.jpg");

        when(countryRepository.findCountryByCountryId(countryId)).thenReturn(Mono.empty());

        StepVerifier.create(countryService.updateCountry(countryId, updatedRequest))
                .expectErrorMatches(error -> error instanceof NotFoundException &&
                        error.getMessage().equals("Country id not found: " + countryId))
                .verify();
    }

    @Test
    public void whenDeleteCountry_withExistingId_thenCompleteSuccessfully() {
        String countryId = UUID.randomUUID().toString();
        Country country = new Country("1", countryId, "United States", "us-image.jpg");

        when(countryRepository.findCountryByCountryId(countryId)).thenReturn(Mono.just(country));
        when(countryRepository.delete(country)).thenReturn(Mono.empty());

        StepVerifier.create(countryService.deleteCountry(countryId))
                .verifyComplete();
    }

    @Test
    public void whenDeleteCountry_withNonExistingId_thenReturnNotFound() {
        String countryId = UUID.randomUUID().toString();

        when(countryRepository.findCountryByCountryId(countryId)).thenReturn(Mono.empty());

        StepVerifier.create(countryService.deleteCountry(countryId))
                .expectErrorMatches(error -> error instanceof NotFoundException &&
                        error.getMessage().equals("Country id not found: " + countryId))
                .verify();
    }
}
