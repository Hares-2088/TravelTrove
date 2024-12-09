package com.traveltrove.betraveltrove.business.tour;

import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import com.traveltrove.betraveltrove.dataaccess.tour.TourEvent;
import com.traveltrove.betraveltrove.dataaccess.tour.TourEventRepository;
import com.traveltrove.betraveltrove.dataaccess.tour.TourRepository;
import com.traveltrove.betraveltrove.presentation.tour.TourEventRequestModel;
import com.traveltrove.betraveltrove.presentation.tour.TourEventResponseModel;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

class TourEventServiceUnitTest {

    @Mock
    private TourEventRepository tourEventRepository;

    @InjectMocks
    private TourEventServiceImpl tourEventService;


    @Test
    public void whenGetTourEvents_thenReturnListOfTourEventResponseModel() {
        TourEvent tourEvent1 = new TourEvent();
        tourEvent1.setTourEventId("1");
        tourEvent1.setTourId("1");
        tourEvent1.setEventId("1");
        tourEvent1.setSeq(1);
        tourEvent1.setSeqDesc("First event");

        TourEvent tourEvent2 = new TourEvent();
        tourEvent2.setTourEventId("2");
        tourEvent2.setTourId("1");
        tourEvent2.setEventId("2");
        tourEvent2.setSeq(2);
        tourEvent2.setSeqDesc("Second event");

        when(tourEventRepository.findAll())
                .thenReturn(Mono.just(tourEvent1).concatWith(Mono.just(tourEvent2)));

        StepVerifier.create(tourEventService.getAllTourEvents())
                .expectNextMatches(tourEventResponseModel -> tourEventResponseModel.getTourEventId().equals("1"))
                .expectNextMatches(tourEventResponseModel -> tourEventResponseModel.getTourEventId().equals("2"))
                .verifyComplete();
    }

    @Test
    public void whenGetTourEventsByTourId_thenReturnListOfTourEventResponseModel() {
        String tourId = "1";
        TourEvent tourEvent1 = new TourEvent();
        tourEvent1.setTourEventId("1");
        tourEvent1.setTourId(tourId);
        tourEvent1.setEventId("1");
        tourEvent1.setSeq(1);
        tourEvent1.setSeqDesc("First event");

        TourEvent tourEvent2 = new TourEvent();
        tourEvent2.setTourEventId("2");
        tourEvent2.setTourId(tourId);
        tourEvent2.setEventId("2");
        tourEvent2.setSeq(2);
        tourEvent2.setSeqDesc("Second event");

        when(tourEventRepository.findAllByTourId(tourId))
                .thenReturn(Mono.just(tourEvent1).concatWith(Mono.just(tourEvent2)));

        StepVerifier.create(tourEventService.getTourEventsByTourId(tourId))
                .expectNextMatches(tourEventResponseModel -> tourEventResponseModel.getTourEventId().equals("1"))
                .expectNextMatches(tourEventResponseModel -> tourEventResponseModel.getTourEventId().equals("2"))
                .verifyComplete();
    }


   @Test
   public void whenGetTourEventByTourEventId_withExistingId_thenReturnTourEventResponseModel() {
       String tourEventId = "1";
       TourEvent tourEvent = new TourEvent();
       tourEvent.setTourEventId(tourEventId);
       tourEvent.setTourId("1");
       tourEvent.setEventId("1");
       tourEvent.setSeq(1);
       tourEvent.setSeqDesc("First event");

       when(tourEventRepository.findByTourEventId(tourEventId))
               .thenReturn(Mono.just(tourEvent));

       Mono<TourEventResponseModel> result = tourEventService.getTourEventByTourEventId(tourEventId);

       StepVerifier.create(result)
               .expectNextMatches(tourEventResponseModel -> tourEventResponseModel.getTourEventId().equals(tourEventId))
               .verifyComplete();

   }

    @Test
    public void whenAddTourEvent_withValidTourEvent_thenReturnTourEventResponseModel() {
        TourEvent tourEvent = new TourEvent();
        tourEvent.setTourEventId("1");
        tourEvent.setTourId("1");
        tourEvent.setEventId("1");
        tourEvent.setSeq(1);
        tourEvent.setSeqDesc("First event");

        when(tourEventRepository.save(tourEvent)).thenReturn(Mono.just(tourEvent));

        Mono<TourEventResponseModel> result = tourEventService.addTourEvent(tourEvent);

        StepVerifier.create(result)
                .expectNextMatches(tourEventResponseModel -> tourEventResponseModel.getTourEventId().equals("1"))
                .verifyComplete();
    }

    @Test
    public void whenAddTourEvent_withInvalidTourEvent_thenReturnBadRequest() {
        TourEvent tourEvent = new TourEvent();
        tourEvent.setTourEventId("1");
        tourEvent.setTourId("1");
        tourEvent.setEventId("1");
        tourEvent.setSeq(1);
        tourEvent.setSeqDesc("First event");

        when(tourEventRepository.save(tourEvent)).thenReturn(Mono.error(new IllegalArgumentException("Invalid tour event")));

        Mono<TourEventResponseModel> result = tourEventService.addTourEvent(tourEvent);

        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof IllegalArgumentException && error.getMessage().equals("Invalid tour event"))
                .verify();
    }

    @Test
    void whenUpdateTourEvent_withExistingId_thenReturnUpdatedTourEventResponseModel() {
        String tourEventId = "1";
        TourEvent existingTourEvent = new TourEvent();
        existingTourEvent.setTourEventId(tourEventId);
        existingTourEvent.setTourId("1");
        existingTourEvent.setEventId("1");
        existingTourEvent.setSeq(1);
        existingTourEvent.setSeqDesc("First event");

        TourEventRequestModel tourEventRequestModel = new TourEventRequestModel();
        tourEventRequestModel.setTourId("Updated Tour");

        when(tourEventRepository.findByTourEventId(tourEventId)).thenReturn(Mono.just(existingTourEvent));
        when(tourEventRepository.save(existingTourEvent)).thenReturn(Mono.just(existingTourEvent));

        Mono<TourEventResponseModel> result = tourEventService.updateTourEvent(tourEventId, tourEventRequestModel);

        StepVerifier.create(result)
                .expectNextMatches(tourEventResponseModel -> tourEventResponseModel.getTourEventId().equals("1"))
                .verifyComplete();
    }

    @Test
    void whenDeleteTourEvent_withExistingId_thenReturnVoid() {
        String tourEventId = "1";
        TourEvent existingTourEvent = new TourEvent();
        existingTourEvent.setTourEventId(tourEventId);
        existingTourEvent.setTourId("1");
        existingTourEvent.setEventId("1");
        existingTourEvent.setSeq(1);
        existingTourEvent.setSeqDesc("First event");

        when(tourEventRepository.findByTourEventId(tourEventId)).thenReturn(Mono.just(existingTourEvent));
        when(tourEventRepository.deleteByTourEventId(tourEventId)).thenReturn(Mono.empty());

        Mono<Void> result = tourEventService.deleteTourEvent(tourEventId);

        StepVerifier.create(result)
                .verifyComplete();
    }


}