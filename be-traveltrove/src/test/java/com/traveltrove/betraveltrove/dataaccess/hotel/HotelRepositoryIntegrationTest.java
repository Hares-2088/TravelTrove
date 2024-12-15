package com.traveltrove.betraveltrove.dataaccess.hotel;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

@DataMongoTest
@ActiveProfiles("test")
public class HotelRepositoryIntegrationTest {

    @Autowired
    private HotelRepository hotelRepository;

    private final String EXISTING_HOTEL_ID = "H01";
    private final String NON_EXISTING_HOTEL_ID = "non-existing-hotel-id";

    private final String EXISTING_CITY_ID = "2702f60a-cf9e-46cf-a971-d76895b904e6";
    private final String NON_EXISTING_CITY_ID = "non-existing-city-id";

    @BeforeEach
    void setUp() {
        Hotel hotel = Hotel.builder()
                .id("1")
                .hotelId(EXISTING_HOTEL_ID)
                .name("Test Hotel")
                .url("http://test-hotel.com")
                .cityId(EXISTING_CITY_ID)
                .build();

        StepVerifier.create(hotelRepository.save(hotel))
                .expectNextMatches(savedHotel -> savedHotel.getHotelId().equals(EXISTING_HOTEL_ID))
                .verifyComplete();
    }

    @AfterEach
    void cleanUp() {
        StepVerifier.create(hotelRepository.deleteAll())
                .verifyComplete();
    }

    @Test
    void whenFindHotelByHotelId_withExistingId_thenReturnExistingHotel() {
        StepVerifier.create(hotelRepository.findHotelByHotelId(EXISTING_HOTEL_ID))
                .expectNextMatches(hotel ->
                        hotel.getHotelId().equals(EXISTING_HOTEL_ID) &&
                        hotel.getCityId().equals(EXISTING_CITY_ID) &&
                        hotel.getName().equals("Test Hotel") &&
                        hotel.getUrl().equals("http://test-hotel.com"))
                .verifyComplete();
    }

    @Test
    void whenFindHotelByHotelId_withNonExistingId_thenReturnEmpty() {
        StepVerifier.create(hotelRepository.findHotelByHotelId(NON_EXISTING_HOTEL_ID))
                .verifyComplete();
    }

    @Test
    void whenFindAllByCityId_withExistingCityId_thenReturnHotels() {
        StepVerifier.create(hotelRepository.findAllByCityId(EXISTING_CITY_ID))
                .expectNextMatches(hotel ->
                        hotel.getCityId().equals(EXISTING_CITY_ID) &&
                        hotel.getHotelId().equals(EXISTING_HOTEL_ID) &&
                        hotel.getName().equals("Test Hotel") &&
                        hotel.getUrl().equals("http://test-hotel.com")
                )
                .verifyComplete();
    }

    @Test
    void whenFindAllByCityId_withNonExistingCityId_thenReturnEmpty() {
        StepVerifier.create(hotelRepository.findAllByCityId(NON_EXISTING_CITY_ID))
                .verifyComplete();
    }

}
