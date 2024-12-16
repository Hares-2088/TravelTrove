package com.traveltrove.betraveltrove.dataaccess.hotel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

@DataMongoTest
@ActiveProfiles("test")
public class HotelRepositoryUnitTest {

    @Autowired
    private HotelRepository hotelRepository;

    private final String EXISTING_HOTEL_ID = "H01";
    private final String NON_EXISTING_HOTEL_ID = "non-existing-hotel-id";

    private final String EXISTING_CITY_ID = "2702f60a-cf9e-46cf-a971-d76895b904e6";
    private final String NON_EXISTING_CITY_ID = "non-existing-city-id";

    private final Hotel hotel1 = Hotel.builder()
            .hotelId("H01")
            .name("Sample Hotel")
            .url("http://sample-hotel.com")
            .cityId(EXISTING_CITY_ID)
            .build();

    private final Hotel hotel2 = Hotel.builder()
            .hotelId("H02")
            .name("Another Hotel")
            .url("http://another-hotel.com")
            .cityId(EXISTING_CITY_ID)
            .build();

    @BeforeEach
    void setUp() {
        StepVerifier
                .create(hotelRepository.deleteAll())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void whenFindHotelByHotelId_withExistingId_thenReturnHotel() {
        StepVerifier
                .create(hotelRepository.save(hotel1))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier
                .create(hotelRepository.findHotelByHotelId(EXISTING_HOTEL_ID))
                .expectNextMatches(hotel ->
                        hotel.getHotelId().equals(EXISTING_HOTEL_ID) &&
                        hotel.getCityId().equals(EXISTING_CITY_ID) &&
                        hotel.getName().equals("Sample Hotel") &&
                        hotel.getUrl().equals("http://sample-hotel.com"))
                .verifyComplete();
    }

    @Test
    void whenFindHotelByHotelId_withNonExistingId_thenReturnEmpty() {
        StepVerifier
                .create(hotelRepository.findHotelByHotelId(NON_EXISTING_HOTEL_ID))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void whenFindAllByCityId_withExistingCityId_thenReturnHotels() {
        StepVerifier
                .create(hotelRepository.save(hotel1))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier
                .create(hotelRepository.save(hotel2))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier
                .create(hotelRepository.findAllByCityId(EXISTING_CITY_ID))
                .expectNextMatches(hotel ->
                        hotel.getCityId().equals(EXISTING_CITY_ID) &&
                                hotel.getHotelId().equals(EXISTING_HOTEL_ID) &&
                                hotel.getName().equals("Sample Hotel") &&
                                hotel.getUrl().equals("http://sample-hotel.com"))
                .expectNextMatches(hotel ->
                        hotel.getCityId().equals(EXISTING_CITY_ID) &&
                                hotel.getHotelId().equals("H02") &&
                                hotel.getName().equals("Another Hotel") &&
                                hotel.getUrl().equals("http://another-hotel.com"))
                .verifyComplete();
    }

    @Test
    void whenFindAllByCityId_withNonExistingCityId_thenReturnEmpty() {
        StepVerifier
                .create(hotelRepository.findAllByCityId(NON_EXISTING_CITY_ID))
                .expectNextCount(0)
                .verifyComplete();
    }

}
