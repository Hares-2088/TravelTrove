package com.traveltrove.betraveltrove.business;

import com.traveltrove.betraveltrove.dataaccess.tour.TourRepository;
import com.traveltrove.betraveltrove.presentation.TourResponseModel;
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
    private final CityRepository cityRepository;
    private final EventRepository eventRepository;

    public TourServiceImpl(TourRepository tourRepo, CityRepository? cityRepo, EventRepository? eventRepo) {
        this.tourRepository = tourRepo;
        if (cityRepository != null) {
            this.cityRepository = cityRepo;
        }
        if (eventRepository != null) {
            this.eventRepository = eventRepo;
        }
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

    @Override
    public Mono<TourResponseModel> addTour(Mono<TourRequestModel> tourRequestModel) {
        return tourRequestModel
                .map(EntityModelUtil::toTourEntity)
                .doOnNext(tour -> {
                    tour.setTourId(UUID.randomUUID().toString());
                })
                .flatMap(tourRepository::insert)
                .map(EntityModelUtil::toTourResponseModel);
    }

    @Override
    public Mono<CityResponseModel> addCityToTour(String tourId, Mono<CityRequestModel> cityRequestModel) {
        return tourRepository.findTourByTourId(tourId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Tour with ID " + tourId + " does not exist")))
                .then(cityRequestModel
                        .map(EntityModelUtil::toCityEntity)
                        .doOnNext(city -> {
                            city.setCityId(UUID.randomUUID().toString());
                            city.setTourId(tourId);
                        })
                        .flatMap(cityRepository::insert)
                        .map(EntityModelUtil::toCityResponseModel));
    }


    @Override
    Mono<EventResponseModel> addEventToCity(String tourId, String cityId, Mono<EventRequestModel> eventRequestModel) {
        return tourRepository.findTourByTourId(tourId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Tour with ID " + tourId + " does not exist")))
                .then(cityRepository.findCityByCityIdAndTourId(cityId, tourId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("City with ID " + cityId + " does not exist in tour with ID " + tourId)))
                .then(eventRequestModel
                        .map(EntityModelUtil::toEventEntity)
                        .doOnNext(event -> {
                            event.setEventId(UUID.randomUUID().toString());
                            event.setCityId(cityId);
                        })
                        .flatMap(eventRepository::insert)
                        .map(EntityModelUtil::toEventResponseModel));
    }

}
