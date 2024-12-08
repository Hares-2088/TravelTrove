package com.traveltrove.betraveltrove.business.tour;

import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import com.traveltrove.betraveltrove.dataaccess.tour.TourRepository;
import com.traveltrove.betraveltrove.presentation.tour.TourRequestModel;
import com.traveltrove.betraveltrove.presentation.tour.TourResponseModel;
import com.traveltrove.betraveltrove.presentation.*;
import com.traveltrove.betraveltrove.utils.EntityModelUtil;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
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
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Tour Id not found: " + tourId))))
                .map(EntityModelUtil::toTourResponseModel);
    }

    @Override
    public Mono<TourResponseModel> addTour(Tour tour) {
        return tourRepository.save(tour)
                .doOnSuccess(savedTour -> log.info("Added new tour {}: ", savedTour))
                .map(EntityModelUtil::toTourResponseModel);
    }


    @Override
    public Mono<TourResponseModel> updateTour(@PathVariable String tourId, TourRequestModel tourRequestModel) {
        return tourRepository.findTourByTourId(tourId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Tour Id not found: " + tourId))))
                .flatMap(existingTour -> {
                    existingTour.setName(tourRequestModel.getName());
                    existingTour.setDescription(tourRequestModel.getDescription());
                    return tourRepository.save(existingTour);
                })
                .doOnSuccess(updatedTour -> log.info("Updated Tour {}: ", updatedTour))
                .map(EntityModelUtil::toTourResponseModel);
    }

    @Override
    public Mono<Void> deleteTourByTourId(String tourId) {
        return tourRepository.findTourByTourId(tourId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Tour Id not found: " + tourId))))
                .flatMap(tourRepository::delete)
                .doOnSuccess(unused -> log.info("Deleted country with id {}: ", tourId));
    }

}
