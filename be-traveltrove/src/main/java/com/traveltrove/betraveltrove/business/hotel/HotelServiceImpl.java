package com.traveltrove.betraveltrove.business.hotel;

import com.traveltrove.betraveltrove.dataaccess.hotel.HotelRepository;
import com.traveltrove.betraveltrove.presentation.hotel.HotelRequestModel;
import com.traveltrove.betraveltrove.presentation.hotel.HotelResponseModel;
import com.traveltrove.betraveltrove.utils.HotelEntityModel;
import com.traveltrove.betraveltrove.utils.exceptions.InvalidInputException;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public Mono<HotelResponseModel> getHotelByHotelId(String hotelId) {
        return hotelRepository.findHotelByHotelId(hotelId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Hotel id not found: " + hotelId))))
                .map(HotelEntityModel::toHotelResponseModel);
    }

    @Override
    public Flux<HotelResponseModel> getHotels(String cityId) {
        // if cityId is provided, filter by cityId
        if (cityId != null && !cityId.isEmpty()) {
            return hotelRepository.findAllByCityId(cityId)
                    .map(HotelEntityModel::toHotelResponseModel);
        }

        // Return all hotels if no filter is provided
        return hotelRepository.findAll()
                .map(HotelEntityModel::toHotelResponseModel);
    }

    @Override
    public Mono<HotelResponseModel> createHotel(Mono<HotelRequestModel> hotelRequestModel) {
        return hotelRequestModel
                .switchIfEmpty(Mono.defer(() -> Mono.error(new InvalidInputException("Hotel request model is empty"))))
                .flatMap(requestModel -> {
                    if (requestModel.getName() == null || requestModel.getName().isBlank()) {
                        return Mono.error(new InvalidInputException("Hotel name is required"));
                    }
                    if (requestModel.getUrl() == null || requestModel.getUrl().isBlank()) {
                        return Mono.error(new InvalidInputException("Hotel URL is required"));
                    }
                    if (requestModel.getCityId() == null || requestModel.getCityId().isBlank()) {
                        return Mono.error(new InvalidInputException("Hotel City Id is required"));
                    }
                    return Mono.just(HotelEntityModel.toHotelEntity(requestModel));
                })
                .flatMap(hotelRepository::save)
                .map(HotelEntityModel::toHotelResponseModel);
    }

    @Override
    public Mono<HotelResponseModel> updateHotel(String hotelId, Mono<HotelRequestModel> hotelRequestModel) {
        return hotelRepository.findHotelByHotelId(hotelId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Hotel id not found: " + hotelId))))
                .flatMap(hotel -> hotelRequestModel
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new InvalidInputException("Hotel request model is empty"))))
                        .flatMap(requestModel -> {
                            if (requestModel.getName() == null || requestModel.getName().isBlank()) {
                                return Mono.error(new InvalidInputException("Hotel name is required"));
                            }
                            if (requestModel.getUrl() == null || requestModel.getUrl().isBlank()) {
                                return Mono.error(new InvalidInputException("Hotel URL is required"));
                            }
                            if (requestModel.getCityId() == null || requestModel.getCityId().isBlank()) {
                                return Mono.error(new InvalidInputException("Hotel City Id is required"));
                            }
                            return Mono.just(HotelEntityModel.toHotelEntity(requestModel))
                                    .doOnNext(hotelEntity -> hotelEntity.setHotelId(hotel.getHotelId()))
                                    .doOnNext(hotelEntity -> hotelEntity.setId(hotel.getId()));
                        }))
                .flatMap(hotelRepository::save)
                .map(HotelEntityModel::toHotelResponseModel);
    }

    @Override
    public Mono<Void> deleteHotel(String hotelId) {
        return hotelRepository.findHotelByHotelId(hotelId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Hotel id not found: " + hotelId))))
                .flatMap(hotelRepository::delete);
    }
}
