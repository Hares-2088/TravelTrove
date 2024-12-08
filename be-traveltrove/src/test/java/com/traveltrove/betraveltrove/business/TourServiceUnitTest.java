package com.traveltrove.betraveltrove.business;

import com.traveltrove.betraveltrove.business.tour.TourServiceImpl;
import com.traveltrove.betraveltrove.dataaccess.city.City;
import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import com.traveltrove.betraveltrove.dataaccess.tour.TourRepository;
import com.traveltrove.betraveltrove.presentation.tour.TourRequestModel;
import com.traveltrove.betraveltrove.presentation.tour.TourResponseModel;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TourServiceUnitTest {

    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private TourServiceImpl tourService;

    @Test
    public void whenGetTours_thenReturnListOfTourResponseModel() {
        Tour tour1 = new Tour();
        tour1.setTourId("1");
        tour1.setName("Sample Tour 1");
        tour1.setDescription("A sample tour description");

        Tour tour2 = new Tour();
        tour2.setTourId("2");
        tour2.setName("Sample Tour 2");
        tour2.setDescription("A sample tour description");

        when(tourRepository.findAll())
                .thenReturn(Mono.just(tour1).concatWith(Mono.just(tour2)));

        StepVerifier.create(tourService.getTours())
                .expectNextMatches(tourResponseModel -> tourResponseModel.getTourId().equals("1"))
                .expectNextMatches(tourResponseModel -> tourResponseModel.getTourId().equals("2"))
                .verifyComplete();
    }


    @Test
    public void whenGetTourByTourId_withExistingId_thenReturnTourResponseModel() {
        String tourId = "1";
        Tour tour = new Tour();
        tour.setTourId(tourId);
        tour.setName("Sample Tour");
        tour.setDescription("A sample tour description");

        //tour.setCities(Collections.singletonList(new City("city1", "Sample City", "A sample city description", "city-image.jpg", LocalDate.of(2023, 1, 2), null, "Sample Hotel")));

        when(tourRepository.findTourByTourId(tourId))
                .thenReturn(Mono.just(tour));

        Mono<TourResponseModel> result = tourService.getTourByTourId(tourId);

        StepVerifier.create(result)
                .expectNextMatches(tourResponseModel -> tourResponseModel.getTourId().equals(tourId))
                .verifyComplete();
    }

    @Test
    public void whenGetTourByTourId_withNonExistingId_thenReturnNotFound() {
        String tourId = "1";
        when(tourRepository.findTourByTourId(tourId)).thenReturn(Mono.empty());

        Mono<TourResponseModel> result = tourService.getTourByTourId(tourId);

        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof NotFoundException && error.getMessage().equals("Tour Id not found: " + tourId))
                .verify();
    }

    @Test
    void whenAddTour_withValidTour_thenReturnTourResponseModel() {
        Tour tour = new Tour();
        tour.setTourId("1");
        tour.setName("New Tour");
        tour.setDescription("A new tour description");

        when(tourRepository.save(tour)).thenReturn(Mono.just(tour));

        Mono<TourResponseModel> result = tourService.addTour(tour);

        StepVerifier.create(result)
                .expectNextMatches(tourResponseModel -> tourResponseModel.getTourId().equals("1"))
                .verifyComplete();
    }

    @Test
    void whenAddTour_withInvalidTour_thenReturnBadRequest() {
        Tour tour = new Tour();
        tour.setTourId("1");
        tour.setName("New Tour");
        tour.setDescription("A new tour description");

        when(tourRepository.save(tour)).thenReturn(Mono.error(new IllegalArgumentException("Invalid tour")));

        Mono<TourResponseModel> result = tourService.addTour(tour);

        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof IllegalArgumentException && error.getMessage().equals("Invalid tour"))
                .verify();
    }

    @Test
    void whenUpdateTour_withExistingId_thenReturnUpdatedTourResponseModel() {
        String tourId = "1";
        Tour existingTour = new Tour();
        existingTour.setTourId(tourId);
        existingTour.setName("Existing Tour");
        existingTour.setDescription("Existing description");

        TourRequestModel tourRequestModel = new TourRequestModel();
        tourRequestModel.setName("Updated Tour");
        tourRequestModel.setDescription("Updated description");

        when(tourRepository.findTourByTourId(tourId)).thenReturn(Mono.just(existingTour));
        when(tourRepository.save(existingTour)).thenReturn(Mono.just(existingTour));

        Mono<TourResponseModel> result = tourService.updateTour(tourId, tourRequestModel);

        StepVerifier.create(result)
                .expectNextMatches(tourResponseModel -> tourResponseModel.getName().equals("Updated Tour"))
                .verifyComplete();
    }

    @Test
    void whenUpdateTour_withNonExistingId_thenReturnNotFound() {
        String tourId = "1";
        TourRequestModel tourRequestModel = new TourRequestModel();
        tourRequestModel.setName("Updated Tour");
        tourRequestModel.setDescription("Updated description");

        when(tourRepository.findTourByTourId(tourId)).thenReturn(Mono.empty());

        Mono<TourResponseModel> result = tourService.updateTour(tourId, tourRequestModel);

        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof NotFoundException && error.getMessage().equals("Tour Id not found: " + tourId))
                .verify();
    }

    @Test
    void whenDeleteTourByTourId_withExistingId_thenDeleteTour() {
        String tourId = "1";
        Tour tour = new Tour();
        tour.setTourId(tourId);

        when(tourRepository.findTourByTourId(tourId)).thenReturn(Mono.just(tour));
        when(tourRepository.delete(tour)).thenReturn(Mono.empty());

        Mono<Void> result = tourService.deleteTourByTourId(tourId);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void whenDeleteTourByTourId_withNonExistingId_thenReturnNotFound() {
        String tourId = "1";

        when(tourRepository.findTourByTourId(tourId)).thenReturn(Mono.empty());

        Mono<Void> result = tourService.deleteTourByTourId(tourId);

        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof NotFoundException && error.getMessage().equals("Tour Id not found: " + tourId))
                .verify();
    }

}
