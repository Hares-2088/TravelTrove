package com.traveltrove.betraveltrove.business.tour;

import com.traveltrove.betraveltrove.dataaccess.tour.TourEvents;
import com.traveltrove.betraveltrove.dataaccess.tour.TourEventsRepository;
import com.traveltrove.betraveltrove.presentation.tour.TourEventsRequestModel;
import com.traveltrove.betraveltrove.presentation.tour.TourEventsResponseModel;
import com.traveltrove.betraveltrove.utils.EntityModelUtil;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class TourEventsServiceImpl implements TourEventsService {
    private final TourEventsRepository tourEventRepository;

    @Autowired
    public TourEventsServiceImpl(TourEventsRepository tourEventRepository) {
        this.tourEventRepository = tourEventRepository;
    }

    @Override
    public Flux<TourEventsResponseModel> getAllTourEvents() {
        return tourEventRepository.findAll()
                .map(EntityModelUtil::toTourEventsResponseModel);
    }

    @Override
    public Mono<TourEventsResponseModel> getTourEventsByTourId(String tourId) {
        return tourEventRepository.findByTourId(tourId) // Assuming findById returns Mono<TourEvents>
                .map(EntityModelUtil::toTourEventsResponseModel);
    }

    @Override
    public Mono<TourEventsResponseModel> addTourEvent(TourEvents tourEvents) {
        return tourEventRepository.save(tourEvents)
                .doOnSuccess(savedEvent -> log.info("Added new tour event: {}", savedEvent))
                .map(EntityModelUtil::toTourEventsResponseModel);
    }



    @Override
    public Mono<TourEventsResponseModel> updateTourEvent(String toursEventId, TourEventsRequestModel request) {
        return tourEventRepository.findByEvents(toursEventId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Tour Event ID not found: " + toursEventId))))
                .flatMap(existingEvent -> {
                    existingEvent.setSeq(request.getSeq());
                    existingEvent.setSeqDesc(request.getSeqDesc());
                    existingEvent.setTourId(request.getTourId());
                    existingEvent.setEvents(request.getEvents());
                    return tourEventRepository.save(existingEvent);
                })
                .doOnSuccess(updatedEvent -> log.info("Updated tour event: {}", updatedEvent))
                .map(EntityModelUtil::toTourEventsResponseModel);
    }

    @Override
    public Mono<Void> deleteTourEvent(String toursEventId) {
        return tourEventRepository.findByEvents(toursEventId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Tour Event ID not found: " + toursEventId))))
                .flatMap(tourEventRepository::delete)
                .doOnSuccess(unused -> log.info("Deleted tour event with ID: {}", toursEventId));
    }

}

