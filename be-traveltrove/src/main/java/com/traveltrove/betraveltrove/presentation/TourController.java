package com.traveltrove.betraveltrove.presentation;

import com.traveltrove.betraveltrove.business.TourService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/tours")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class TourController {

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<TourResponseModel> getAllTours() {
        return tourService.getTours()
                .doOnNext(tour -> log.info("Tour: {}", tour));
    }
}
