package com.traveltrove.betraveltrove.presentation.hotel;

import com.traveltrove.betraveltrove.business.hotel.HotelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/hotels")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    // Get all hotels or filter by cityId or countryId
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<HotelResponseModel> getHotels(
            @RequestParam(required = false) String cityId) {
        log.info("Fetching hotels with filters: cityId={}", cityId);
        return hotelService.getHotels(cityId);
    }

    // Get a hotel by hotel ID
    @GetMapping(value = "/{hotelId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<HotelResponseModel>> getHotelByHotelId(@PathVariable String hotelId) {
        log.info("Fetching hotel by hotel ID: {}", hotelId);
        return hotelService.getHotelByHotelId(hotelId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    // Create a new hotel
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<HotelResponseModel>> createHotel(@RequestBody Mono<HotelRequestModel> hotelRequestModel) {
        log.info("Creating new hotel");
        return hotelService.createHotel(hotelRequestModel)
                .map(hotelResponseModel -> ResponseEntity.status(201).body(hotelResponseModel))
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
    }

    // Update an existing hotel
    @PutMapping(value = "/{hotelId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<HotelResponseModel>> updateHotel(
            @PathVariable String hotelId,
            @RequestBody Mono<HotelRequestModel> hotelRequestModel) {
        log.info("Updating hotel with hotel ID: {}", hotelId);
        return hotelService.updateHotel(hotelId, hotelRequestModel)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    // Delete a hotel by hotel ID
    @DeleteMapping(value = "/{hotelId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> deleteHotel(@PathVariable String hotelId) {
        log.info("Deleting hotel with hotel ID: {}", hotelId);
        return hotelService.deleteHotel(hotelId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
