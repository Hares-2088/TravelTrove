package com.traveltrove.betraveltrove.business.airport;

import com.traveltrove.betraveltrove.dataaccess.airport.Airport;
import com.traveltrove.betraveltrove.dataaccess.airport.AirportRepository;
import com.traveltrove.betraveltrove.presentation.airport.AirportRequestModel;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

import java.util.UUID;


@ExtendWith(MockitoExtension.class)
public class AirportServiceUnitTest {
    @Mock
    private AirportRepository airportRepository;

    @InjectMocks
    private AirportServiceImpl airportServiceImpl;


    @Test
    public void whenGetAllAirports_ReturnAllAirports() {
        String airportId1 = UUID.randomUUID().toString();
        String airportId2 = UUID.randomUUID().toString();
        String airportId3 = UUID.randomUUID().toString();

        Airport airport1 = new Airport("1", airportId1, "JFK", "123");
        Airport airport2 = new Airport("2", airportId2, "LAX", "456");
        Airport airport3 = new Airport("3", airportId3, "SFO", "789");

        when(airportRepository.findAll()).thenReturn(Flux.just(airport1, airport2, airport3));

        StepVerifier
                .create(airportServiceImpl.getAllAirports())
                .expectNextMatches(response ->
                        response.getAirportId().equals(airportId1) &&
                                response.getName().equals("JFK") &&
                                response.getCityId().equals("123")
                )
                .expectNextMatches(response ->
                        response.getAirportId().equals(airportId2) &&
                                response.getName().equals("LAX") &&
                                response.getCityId().equals("456")
                )
                .expectNextMatches(response ->
                        response.getAirportId().equals(airportId3) &&
                                response.getName().equals("SFO") &&
                                response.getCityId().equals("789")
                )
                .verifyComplete();

    }
    @Test
    public void whenGetAllAirports_withNoAirports_ReturnEmpty() {
        when(airportRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier
                .create(airportServiceImpl.getAllAirports())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void whenGetAirportById_ReturnAirport() {
        String airportId = UUID.randomUUID().toString();
        Airport airport = new Airport("1", airportId, "JFK", "123");

        when(airportRepository.findAirportByAirportId(airportId)).thenReturn(Mono.just(airport));

        StepVerifier
                .create(airportServiceImpl.getAirportById(airportId))
                .expectNextMatches(response ->
                        response.getAirportId().equals(airportId) &&
                                response.getName().equals("JFK") &&
                                response.getCityId().equals("123")
                )
                .verifyComplete();
    }
    @Test
    public void whenGetAirportById_withInvalidAirportId_ReturnNotFound(){
        String airportId = "invalidID";

        when(airportRepository.findAirportByAirportId(airportId)).thenReturn(Mono.empty());

        StepVerifier
                .create(airportServiceImpl.getAirportById(airportId))
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Airport id not found: " + airportId))
                .verify();
    }

    @Test
    public void whenAddAirport_thenReturnCreatedAirport() {
        Airport newAirport = new Airport("1", UUID.randomUUID().toString(), "JFK", "123");
        AirportRequestModel airportRequestModel = new AirportRequestModel("JFK", "123");


//        when(airportRepository.save(newAirport)).thenReturn(Mono.just(newAirport));
        when(airportRepository.save(any(Airport.class))).thenReturn(Mono.just(newAirport));

        StepVerifier
                .create(airportServiceImpl.addAirport(airportRequestModel))
                .expectNextMatches(response ->
                        response.getName().equals(newAirport.getName()) &&
                                response.getCityId().equals(newAirport.getCityId())
                )
                .verifyComplete();
    }

    @Test
    public void whenAddAirport_withInvalidCityId_thenReturnNotFound(){
        AirportRequestModel airportRequestModel = new AirportRequestModel("JFK", "invalidCityId");

        when(airportRepository.save(any(Airport.class))).thenReturn(Mono.error(new NotFoundException("City id not found: " + airportRequestModel.getCityId())));

        StepVerifier
                .create(airportServiceImpl.addAirport(airportRequestModel))
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("City id not found: " + airportRequestModel.getCityId()))
                .verify();
    }
    @Test
    public void whenUpdateAirport_thenReturnUpdatedAirport() {
        String airportId = UUID.randomUUID().toString();
        Airport updatedAirport = new Airport("1", airportId, "JFK", "123");
        AirportRequestModel airportRequestModel = new AirportRequestModel("JFK", "123");

        when(airportRepository.findAirportByAirportId(airportId)).thenReturn(Mono.just(updatedAirport));
        when(airportRepository.save(updatedAirport)).thenReturn(Mono.just(updatedAirport));

        StepVerifier
                .create(airportServiceImpl.updateAirport(airportId, airportRequestModel))
                .expectNextMatches(response ->
                        response.getName().equals(updatedAirport.getName()) &&
                                response.getCityId().equals(updatedAirport.getCityId())
                )
                .verifyComplete();
    }

    @Test
    public void whenUpdateAirport_withInvalidId_thenReturnNotFound() {
        String airportId = "InvalidId";
        AirportRequestModel airportRequestModel = new AirportRequestModel("JFK", "123");

        when(airportRepository.findAirportByAirportId(airportId)).thenReturn(Mono.empty());

        StepVerifier
                .create(airportServiceImpl.updateAirport(airportId, airportRequestModel))
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Airport id not found: " + airportId))
                .verify();
    }

    @Test
    public void whenDeleteAirport_thenReturnDeletedAirport() {
        String airportId = UUID.randomUUID().toString();
        Airport deletedAirport = new Airport("1", airportId, "JFK", "123");

        when(airportRepository.findAirportByAirportId(airportId)).thenReturn(Mono.just(deletedAirport));
        when(airportRepository.delete(deletedAirport)).thenReturn(Mono.empty());

        StepVerifier
                .create(airportServiceImpl.deleteAirport(airportId))
                .expectNextCount(0)
                .verifyComplete();
    }
    @Test
    void whenDeleteAirport_withInvalidId_thenReturnNotFound() {
        String airportId = UUID.randomUUID().toString();

        when(airportRepository.findAirportByAirportId(airportId)).thenReturn(Mono.empty());

        StepVerifier
                .create(airportServiceImpl.deleteAirport(airportId))
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException
                && throwable.getMessage().equals("Airport id not found: " + airportId))
                .verify();
    }


}