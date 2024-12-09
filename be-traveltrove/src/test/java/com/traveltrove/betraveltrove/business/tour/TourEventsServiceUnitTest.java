package com.traveltrove.betraveltrove.business.tour;

import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import com.traveltrove.betraveltrove.dataaccess.tour.TourEvents;
import com.traveltrove.betraveltrove.dataaccess.tour.TourEventsRepository;
import com.traveltrove.betraveltrove.presentation.tour.TourEventsRequestModel;
import com.traveltrove.betraveltrove.presentation.tour.TourRequestModel;
import com.traveltrove.betraveltrove.presentation.tour.TourResponseModel;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)

class TourEventsServiceUnitTest {

    @Mock
    private TourEventsRepository tourEventsRepository;

    @InjectMocks
    private TourEventsServiceImpl tourEventsServiceImpl;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenGetAllTourEvents_thenReturnAllTourEvents() {
        String tourEventId1 = "1";
        String tourEventId2 = "2";

        // Create and initialize tour events
        TourEvents tourEvent1 = new TourEvents();
        tourEvent1.setToursEventId(tourEventId1);
        tourEvent1.setSeq(1);
        tourEvent1.setSeqDesc("First");
        tourEvent1.setTourId("1");
        tourEvent1.setEvents("Event 1");

        TourEvents tourEvent2 = new TourEvents();
        tourEvent2.setToursEventId(tourEventId2);
        tourEvent2.setSeq(2);
        tourEvent2.setSeqDesc("Second");
        tourEvent2.setTourId("2");
        tourEvent2.setEvents("Event 2");

        // Mock repository behavior
        when(tourEventsRepository.findAll()).thenReturn(Flux.just(tourEvent1, tourEvent2));

        // Verify behavior
        StepVerifier.create(tourEventsServiceImpl.getAllTourEvents())
                .expectNextMatches(response ->
                        response.getToursEventId().equals(tourEventId1) &&
                                response.getTourId().equals("1") &&
                                response.getEvents().equals("Event 1")
                )
                .expectNextMatches(response ->
                        response.getToursEventId().equals(tourEventId2) &&
                                response.getTourId().equals("2") &&
                                response.getEvents().equals("Event 2")
                )
                .verifyComplete();
    }

    @Test
    void whenGetTourEventsByTourId_withExistingTourId_thenReturnTourEvents() {
        String tourId = "1";
        String tourEventId = "1";

        // Create and initialize tour events
        TourEvents tourEvent = new TourEvents();
        tourEvent.setToursEventId(tourEventId);
        tourEvent.setSeq(1);
        tourEvent.setSeqDesc("First");
        tourEvent.setTourId(tourId);
        tourEvent.setEvents("Event 1");

        // Mock repository behavior
        when(tourEventsRepository.findByTourId(tourId)).thenReturn(Mono.just(tourEvent));

        // Verify behavior
        StepVerifier.create(tourEventsServiceImpl.getTourEventsByTourId(tourId))
                .expectNextMatches(response ->
                        response.getToursEventId().equals(tourEventId) &&
                                response.getTourId().equals(tourId) &&
                                response.getEvents().equals("Event 1")
                )
                .verifyComplete();
    }


    @Test
    void whenGetTourEventsByTourId_withNonExistingTourId_thenReturnNotFound() {
        String tourId = "1";

        // Mock repository behavior
        when(tourEventsRepository.findByTourId(tourId)).thenReturn(Mono.empty());

        // Verify behavior
        StepVerifier.create(tourEventsServiceImpl.getTourEventsByTourId(tourId))
                .verifyComplete();
    }



    @Test
    void whenAddTourEvent_thenReturnTourEvent() {
        String tourEventId = "1";

        // Create and initialize tour events
        TourEvents tourEvent = new TourEvents();
        tourEvent.setToursEventId(tourEventId);
        tourEvent.setSeq(1);
        tourEvent.setSeqDesc("First");
        tourEvent.setTourId("1");
        tourEvent.setEvents("Event 1");

        // Mock repository behavior
        when(tourEventsRepository.save(tourEvent)).thenReturn(Mono.just(tourEvent));

        // Verify behavior
        StepVerifier.create(tourEventsServiceImpl.addTourEvent(tourEvent))
                .expectNextMatches(response ->
                        response.getToursEventId().equals(tourEventId) &&
                                response.getTourId().equals("1") &&
                                response.getEvents().equals("Event 1")
                )
                .verifyComplete();
    }


    @Test
    void whenUpdateTourEvent_withExistingTourEventId_thenReturnUpdatedTourEvent() {
        String tourEventId = "1";

        // Create and initialize tour events
        TourEvents tourEvent = new TourEvents();
        tourEvent.setToursEventId(tourEventId);
        tourEvent.setSeq(1);
        tourEvent.setSeqDesc("First");
        tourEvent.setTourId("1");
        tourEvent.setEvents("Event 1");

        // Create and initialize request model
        TourEventsRequestModel request = new TourEventsRequestModel();
        request.setSeq(2);
        request.setSeqDesc("Second");
        request.setTourId("1");
        request.setEvents("Event 2");

        // Mock repository behavior
        when(tourEventsRepository.findByToursEventId(tourEventId)).thenReturn(Mono.just(tourEvent));
        when(tourEventsRepository.save(tourEvent)).thenReturn(Mono.just(tourEvent));

        // Verify behavior
        StepVerifier.create(tourEventsServiceImpl.updateTourEvent(tourEventId, request))
                .expectNextMatches(response ->
                        response.getToursEventId().equals(tourEventId) &&
                                response.getTourId().equals("1") &&
                                response.getEvents().equals("Event 2")
                )
                .verifyComplete();
    }

}
