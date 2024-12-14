package com.traveltrove.betraveltrove.business.traveler;

import com.traveltrove.betraveltrove.dataaccess.traveler.Traveler;
import com.traveltrove.betraveltrove.dataaccess.traveler.TravelerRepository;
import com.traveltrove.betraveltrove.presentation.travaler.TravelerRequestModel;
import com.traveltrove.betraveltrove.presentation.travaler.TravelerResponseModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TravelerServiceIntegrationTest {

    @Mock
    private TravelerRepository travelerRepository;

    @InjectMocks
    private TravelerServiceImpl travelerService;

    @Test
    void whenGetAllTravelers_WithoutName_thenReturnAllTravelers() {

        Traveler traveler1 = Traveler.builder()
                .travelerId("1")
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@email.com")
                .addressLine1("123 Main St")
                .city("Anytown")
                .state("NY")
                .countryId("1")
                .build();

        Traveler traveler2 = Traveler.builder()
                .travelerId("2")
                .firstName("Jane")
                .lastName("Smith")
                .email("janesmith@gmail.com")
                .addressLine1("456 Elm St")
                .city("Othertown")
                .state("CA")
                .countryId("2")
                .build();

        when(travelerRepository.findAll()).thenReturn(Flux.just(traveler1, traveler2));

        StepVerifier.create(travelerService.getAllTravelers(null))
                .expectNextMatches(response ->
                        response.getTravelerId().equals("1") &&
                                response.getFirstName().equals("John") &&
                                response.getLastName().equals("Doe") &&
                                response.getEmail().equals("johndoe@email.com") &&
                                response.getAddressLine1().equals("123 Main St") &&
                                response.getCity().equals("Anytown") &&
                                response.getState().equals("NY") &&
                                response.getCountryId().equals("1")
                )
                .expectNextMatches(response ->
                        response.getTravelerId().equals("2") &&
                                response.getFirstName().equals("Jane") &&
                                response.getLastName().equals("Smith") &&
                                response.getEmail().equals("janesmith@gmail.com") &&
                                response.getAddressLine1().equals("456 Elm St") &&
                                response.getCity().equals("Othertown") &&
                                response.getState().equals("CA") &&
                                response.getCountryId().equals("2")
                )
                .verifyComplete();
    }

    @Test
    void whenGetAllTravelers_WithName_thenReturnMatchingTravelers() {

        Traveler traveler1 = Traveler.builder()
                .travelerId("1")
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@email.com")
                .addressLine1("123 Main St")
                .city("Anytown")
                .state("NY")
                .countryId("1")
                .build();

        Traveler traveler2 = Traveler.builder()
                .travelerId("2")
                .firstName("John")
                .lastName("Smith")
                .email("johnsmith@gmail.com")
                .addressLine1("456 Elm St")
                .city("Othertown")
                .state("CA")
                .countryId("2")
                .build();

        when(travelerRepository.findTravelerByFirstName("John")).thenReturn(Flux.just(traveler1, traveler2));

        StepVerifier.create(travelerService.getAllTravelers("John"))
                .expectNextMatches(response ->
                        response.getTravelerId().equals("1") &&
                                response.getFirstName().equals("John") &&
                                response.getLastName().equals("Doe") &&
                                response.getEmail().equals("johndoe@email.com") &&
                                response.getAddressLine1().equals("123 Main St") &&
                                response.getCity().equals("Anytown") &&
                                response.getState().equals("NY") &&
                                response.getCountryId().equals("1")
                )
                .expectNextMatches(response ->
                        response.getTravelerId().equals("2") &&
                                response.getFirstName().equals("John") &&
                                response.getLastName().equals("Smith") &&
                                response.getEmail().equals("johnsmith@gmail.com") &&
                                response.getAddressLine1().equals("456 Elm St") &&
                                response.getCity().equals("Othertown") &&
                                response.getState().equals("CA") &&
                                response.getCountryId().equals("2")
                )
                .verifyComplete();
    }

    @Test
    void whenCreateTraveler_thenReturnCreatedTraveler() {

        Traveler traveler = Traveler.builder()
                .travelerId("1")
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@gmail.com")
                .addressLine1("123 Main St")
                .city("Anytown")
                .state("NY")
                .countryId("1")
                .build();

        TravelerRequestModel travelerRequestModel = TravelerRequestModel.builder()
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@gmail.com")
                .addressLine1("123 Main St")
                .city("Anytown")
                .state("NY")
                .countryId("1")
                .build();

        when(travelerRepository.save(any(Traveler.class))).thenReturn(Mono.just(traveler));

        StepVerifier.create(travelerService.createTraveler(travelerRequestModel))
                .expectNextMatches(response ->
                        response.getTravelerId().equals("1") &&
                                response.getFirstName().equals("John") &&
                                response.getLastName().equals("Doe") &&
                                response.getEmail().equals("johndoe@gmail.com") &&
                                response.getAddressLine1().equals("123 Main St") &&
                                response.getCity().equals("Anytown") &&
                                response.getState().equals("NY") &&
                                response.getCountryId().equals("1")
                )
                .verifyComplete();
    }

    @Test
    void whenUpdateTraveler_thenReturnUpdatedTraveler() {

        Traveler traveler = Traveler.builder()
                .travelerId("1")
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@gmail.com")
                .addressLine1("123 Main St")
                .city("Anytown")
                .state("NY")
                .countryId("1")
                .build();

        Traveler traveler2 = Traveler.builder()
                .travelerId("2")
                .firstName("John")
                .lastName("Smith")
                .email("johnsmith@gmail.com")
                .addressLine1("456 Elm St")
                .city("Othertown")
                .state("CA")
                .countryId("2")
                .build();

        TravelerRequestModel travelerRequestModel = TravelerRequestModel.builder()
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@gmail.com")
                .addressLine1("123 Main St")
                .city("Anytown")
                .state("NY")
                .countryId("1")
                .build();

        when(travelerRepository.findTravelerByTravelerId("1")).thenReturn(Mono.just(traveler));

        when(travelerRepository.save(any(Traveler.class))).thenReturn(Mono.just(traveler));

        StepVerifier.create(travelerService.updateTraveler("1", travelerRequestModel))
                .expectNextMatches(response ->
                        response.getTravelerId().equals("1") &&
                                response.getFirstName().equals("John") &&
                                response.getLastName().equals("Doe") &&
                                response.getEmail().equals("johndoe@gmail.com") &&
                                response.getAddressLine1().equals("123 Main St") &&
                                response.getCity().equals("Anytown") &&
                                response.getState().equals("NY") &&
                                response.getCountryId().equals("1")
                )
                .verifyComplete();
    }

    @Test
    void whenDeleteTraveler_thenReturnDeletedTraveler() {

        Traveler traveler = Traveler.builder()
                .travelerId("1")
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@email.com")
                .addressLine1("123 Main St")
                .city("Anytown")
                .state("NY")
                .countryId("1")
                .build();

        TravelerResponseModel expectedResponse = TravelerResponseModel.builder()
                .travelerId("1")
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@email.com")
                .addressLine1("123 Main St")
                .city("Anytown")
                .state("NY")
                .countryId("1")
                .build();

        when(travelerRepository.findTravelerByTravelerId("1")).thenReturn(Mono.just(traveler));
        when(travelerRepository.delete(traveler)).thenReturn(Mono.empty());

        StepVerifier.create(travelerService.deleteTraveler("1"))
                .expectNextMatches(response ->
                        response.getTravelerId().equals(expectedResponse.getTravelerId()) &&
                        response.getFirstName().equals(expectedResponse.getFirstName()) &&
                        response.getLastName().equals(expectedResponse.getLastName()) &&
                        response.getEmail().equals(expectedResponse.getEmail()) &&
                        response.getAddressLine1().equals(expectedResponse.getAddressLine1()) &&
                        response.getCity().equals(expectedResponse.getCity()) &&
                        response.getState().equals(expectedResponse.getState()) &&
                        response.getCountryId().equals(expectedResponse.getCountryId())
                )
                .verifyComplete();
    }
}