package com.traveltrove.betraveltrove.business;

import com.traveltrove.betraveltrove.business.tour.TourServiceImpl;
import com.traveltrove.betraveltrove.dataaccess.city.City;
import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import com.traveltrove.betraveltrove.dataaccess.tour.TourRepository;
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
    public void whenGetTourByTourId_withExistingId_thenReturnTourResponseModel() {
        String tourId = "1";
        Tour tour = new Tour();
        tour.setTourId(tourId);
        tour.setName("Sample Tour");
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
                .expectErrorMatches(error -> error instanceof NotFoundException && error.getMessage().equals("Tour id not found: " + tourId))
                .verify();
    }
}
