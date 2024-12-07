package com.traveltrove.betraveltrove.business.tour;

import com.traveltrove.betraveltrove.dataaccess.city.CityRepository;
import com.traveltrove.betraveltrove.dataaccess.tour.EventRepository;
import com.traveltrove.betraveltrove.dataaccess.tour.TourRepository;
import com.traveltrove.betraveltrove.presentation.tour.TourResponseModel;
import com.traveltrove.betraveltrove.presentation.*;
import com.traveltrove.betraveltrove.presentation.tour.TourResponseModel;
import com.traveltrove.betraveltrove.presentation.*;
import com.traveltrove.betraveltrove.utils.EntityModelUtil;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;

    public TourServiceImpl(TourRepository tourRepo) {
        this.tourRepository = tourRepo;
    }

    @Override
    public Flux<TourResponseModel> getTours() {
        return tourRepository.findAll()
                .map(EntityModelUtil::toTourResponseModel);
    }

    @Override
    public Mono<TourResponseModel> getTourByTourId(String tourId) {
        return tourRepository.findTourByTourId(tourId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Tour id not found: " + tourId))))
                .map(EntityModelUtil::toTourResponseModel);
    }

}
