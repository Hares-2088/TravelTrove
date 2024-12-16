package com.traveltrove.betraveltrove.business.tour;

import com.traveltrove.betraveltrove.dataaccess.tour.TourEvent;
import com.traveltrove.betraveltrove.dataaccess.tour.TourEventRepository;
import com.traveltrove.betraveltrove.presentation.tour.TourEventRequestModel;
import com.traveltrove.betraveltrove.presentation.tour.TourEventResponseModel;
import com.traveltrove.betraveltrove.utils.EntityModelUtil;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class TourEventServiceImpl implements TourEventService {

    private final TourEventRepository tourEventRepository;

    public TourEventServiceImpl(TourEventRepository tourEventRepository) {
        this.tourEventRepository = tourEventRepository;
    }

    @Override
    public Flux<TourEventResponseModel> getAllTourEvents() {
        return tourEventRepository.findAll()
                .map(EntityModelUtil::toTourEventResponseModel);
    }

    @Override
    public Flux<TourEventResponseModel> getTourEventsByTourId(String tourId) {
        return tourEventRepository.findAllByTourId(tourId)
                .map(EntityModelUtil::toTourEventResponseModel);
    }

    @Override
    public Mono<TourEventResponseModel> getTourEventByTourEventId(String tourEventId) {
        return tourEventRepository.findByTourEventId(tourEventId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Tour event id not found: " + tourEventId))))
                .map(EntityModelUtil::toTourEventResponseModel);
    }

    @Override
    public Mono<TourEventResponseModel> addTourEvent(TourEvent tourEvent) {
        return tourEventRepository.save(tourEvent)
                .doOnSuccess(savedEvent -> log.info("Added new tour event: {}", savedEvent))
                .map(EntityModelUtil::toTourEventResponseModel);
    }

    @Override
    public Mono<TourEventResponseModel> updateTourEvent(String tourEventId, TourEventRequestModel request) {
        return tourEventRepository.findByTourEventId(tourEventId)
                .switchIfEmpty(Mono.error(new NotFoundException("Tour event not found for tourEventId: " + tourEventId)))
                .flatMap(existingEvent -> {
                    existingEvent.setSeq(request.getSeq());
                    existingEvent.setSeqDesc(request.getSeqDesc());
                    existingEvent.setTourId(request.getTourId());
                    existingEvent.setEventId(request.getEventId());
                    existingEvent.setHotelId(request.getHotelId());
                    return tourEventRepository.save(existingEvent);
                })
                .doOnSuccess(updatedEvent -> log.info("Updated TourEvent: {}", updatedEvent))
                .map(EntityModelUtil::toTourEventResponseModel);
    }

    @Override
    public Mono<Void> deleteTourEvent(String tourEventId) {
        return tourEventRepository.findByTourEventId(tourEventId)
                .switchIfEmpty(Mono.error(new NotFoundException("Tour event not found for tourEventId: " + tourEventId)))
                .flatMap(event -> tourEventRepository.deleteByTourEventId(tourEventId)
                        .doOnSuccess(unused -> log.info("Deleted TourEvent with tourEventId: {}", tourEventId))
                );
    }

}
