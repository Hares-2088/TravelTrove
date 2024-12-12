package com.traveltrove.betraveltrove.dataaccess.hotel;

import com.traveltrove.betraveltrove.dataaccess.country.Hotel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface HotelRepository extends ReactiveMongoRepository<Hotel, String> {
    Mono<Hotel> findHotelByHotelId(String hotelId);
}
