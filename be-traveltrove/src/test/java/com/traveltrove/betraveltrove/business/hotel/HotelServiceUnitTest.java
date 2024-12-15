package com.traveltrove.betraveltrove.business.hotel;

import com.traveltrove.betraveltrove.dataaccess.hotel.Hotel;
import com.traveltrove.betraveltrove.dataaccess.hotel.HotelRepository;
import com.traveltrove.betraveltrove.presentation.hotel.HotelRequestModel;
import com.traveltrove.betraveltrove.presentation.hotel.HotelResponseModel;
import com.traveltrove.betraveltrove.utils.HotelEntityModel;
import com.traveltrove.betraveltrove.utils.exceptions.InvalidInputException;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HotelServiceUnitTest {

    @InjectMocks
    private HotelServiceImpl hotelService;

    @Mock
    private HotelRepository hotelRepository;

    Hotel hotel1 = Hotel.builder()
            .hotelId("1")
            .name("Sample Hotel")
            .url("http://samplehotel.com")
            .cityId("1")
            .build();

    Hotel hotel2 = Hotel.builder()
            .hotelId("2")
            .name("Another Hotel")
            .url("http://anotherhotel.com")
            .cityId("1")
            .build();

    Hotel hotel3 = Hotel.builder()
            .hotelId("3")
            .name("Third Hotel")
            .url("http://thirdhotel.com")
            .cityId("2")
            .build();

    @Test
    void whenGetHotelByHotelId_withExistingId_thenReturnHotel() {
        String hotelId = "1";

        when(hotelRepository.findHotelByHotelId(hotelId))
                .thenReturn(Mono.just(hotel1));

        Mono<HotelResponseModel> result = hotelService.getHotelByHotelId(hotelId);

        StepVerifier.create(result)
                .expectNextMatches(hotelResponseModel -> hotelResponseModel.getHotelId().equals(hotelId))
                .verifyComplete();
    }

    @Test
    void whenGetHotelByHotelId_withNonExistingId_thenReturnNotFound() {
        String hotelId = "1";

        when(hotelRepository.findHotelByHotelId(hotelId))
                .thenReturn(Mono.empty());

        Mono<HotelResponseModel> result = hotelService.getHotelByHotelId(hotelId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException)
                .verify();
    }

    @Test
    void whenGetHotels_withCityId_thenReturnHotels() {
        String cityId = "2";

        when(hotelRepository.findAllByCityId(cityId))
                .thenReturn(Flux.just(hotel1));

        Flux<HotelResponseModel> result = hotelService.getHotels(cityId);

        StepVerifier.create(result)
                .expectNextMatches(hotelResponseModel -> hotelResponseModel.getHotelId().equals("1"))
                .verifyComplete();
    }

    @Test
    void whenGetHotels_withNoCityId_thenReturnAllHotels() {
        when(hotelRepository.findAll())
                .thenReturn(Flux.just(hotel1, hotel2, hotel3));

        Flux<HotelResponseModel> result = hotelService.getHotels(null);

        StepVerifier.create(result)
                .expectNextMatches(hotelResponseModel -> hotelResponseModel.getHotelId().equals("1"))
                .expectNextMatches(hotelResponseModel -> hotelResponseModel.getHotelId().equals("2"))
                .expectNextMatches(hotelResponseModel -> hotelResponseModel.getHotelId().equals("3"))
                .verifyComplete();
    }

    @Test
    void whenCreateHotel_withValidRequest_thenReturnCreatedHotel() {
        HotelRequestModel requestModel = HotelRequestModel.builder()
                .name("New Hotel")
                .url("http://newhotel.com")
                .cityId("1")
                .build();

        when(hotelRepository.save(any(Hotel.class)))
                .thenAnswer(invocation -> {
                    Hotel hotel = invocation.getArgument(0);
                    hotel.setHotelId("4");
                    return Mono.just(hotel);
                });

        Mono<HotelResponseModel> result = hotelService.createHotel(Mono.just(requestModel));

        StepVerifier.create(result)
                .expectNextMatches(hotelResponseModel -> hotelResponseModel.getHotelId().equals("4"))
                .verifyComplete();
    }

    @Test
    void whenCreateHotel_withInvalidRequest_thenReturnError() {
        Mono<HotelResponseModel> result = hotelService.createHotel(Mono.empty());

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof InvalidInputException)
                .verify();
    }

    @Test
    void whenCreateHotel_withMissingFields_thenReturnError() {
        HotelRequestModel requestModel = HotelRequestModel.builder()
                .name("")
                .url("http://newhotel.com")
                .cityId("")
                .build();

        Mono<HotelResponseModel> result = hotelService.createHotel(Mono.just(requestModel));

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof InvalidInputException)
                .verify();
    }

    @Test
    void whenUpdateHotel_withValidRequest_thenReturnUpdatedHotel() {
        String hotelId = "1";
        HotelRequestModel requestModel = HotelRequestModel.builder()
                .name("Updated Hotel")
                .url("http://updatedhotel.com")
                .cityId("2")
                .build();

        when(hotelRepository.findHotelByHotelId(hotelId))
                .thenReturn(Mono.just(hotel1));

        when(hotelRepository.save(any(Hotel.class)))
                .thenAnswer(invocation -> {
                    Hotel hotel = invocation.getArgument(0);
                    hotel.setHotelId(hotelId);
                    return Mono.just(hotel);
                });

        Mono<HotelResponseModel> result = hotelService.updateHotel(hotelId, Mono.just(requestModel));

        StepVerifier.create(result)
                .expectNextMatches(hotelResponseModel -> hotelResponseModel.getHotelId().equals(hotelId))
                .verifyComplete();
    }

    @Test
    void whenUpdateHotel_withNonExistingId_thenReturnNotFound() {
        String hotelId = "1";
        HotelRequestModel requestModel = HotelRequestModel.builder()
                .name("Updated Hotel")
                .url("http://updatedhotel.com")
                .cityId("2")
                .build();

        when(hotelRepository.findHotelByHotelId(hotelId))
                .thenReturn(Mono.empty());

        Mono<HotelResponseModel> result = hotelService.updateHotel(hotelId, Mono.just(requestModel));

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException)
                .verify();
    }

    @Test
    void whenDeleteHotel_withExistingId_thenReturnDeletedHotel() {
        String hotelId = "1";

        when(hotelRepository.findHotelByHotelId(hotelId))
                .thenReturn(Mono.just(hotel1));

        when(hotelRepository.delete(hotel1))
                .thenReturn(Mono.empty());

        Mono<Void> result = hotelService.deleteHotel(hotelId);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void whenDeleteHotel_withNonExistingId_thenReturnNotFound() {
        String hotelId = "non-existing-id";

        when(hotelRepository.findHotelByHotelId(hotelId))
                .thenReturn(Mono.empty());

        Mono<Void> result = hotelService.deleteHotel(hotelId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException)
                .verify();
    }

}
