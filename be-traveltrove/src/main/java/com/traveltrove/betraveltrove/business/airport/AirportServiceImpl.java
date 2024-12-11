package com.traveltrove.betraveltrove.business.airport;

import com.traveltrove.betraveltrove.dataaccess.airport.AirportRepository;
import com.traveltrove.betraveltrove.presentation.airport.AirportRequestModel;
import com.traveltrove.betraveltrove.presentation.airport.AirportResponseModel;
import com.traveltrove.betraveltrove.utils.EntityModelUtil;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AirportServiceImpl implements AirportService {
    private final AirportRepository airportRepository;

    public AirportServiceImpl(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    @Override
    public Flux<AirportResponseModel> getAllAirports() {
        return airportRepository.findAll()
                .map(EntityModelUtil::toAirportResponseModel);
    }

    @Override
    public Mono<AirportResponseModel> getAirportById(String airportId) {
        return airportRepository.findAirportByAirportId(airportId)
                .switchIfEmpty(Mono.error(new NotFoundException("Airport id not found: " + airportId)))
                .map(EntityModelUtil::toAirportResponseModel);
    }

    @Override
    public Mono<AirportResponseModel> addAirport(AirportRequestModel airportRequestModel) {
        return airportRepository.save(EntityModelUtil.toAirportEntity(airportRequestModel))
                .doOnSuccess(savedAirport -> log.info("Added new airport: {}", savedAirport))
                .map(EntityModelUtil::toAirportResponseModel);
    }

    @Override
    public Mono<AirportResponseModel> updateAirport(String airportId, AirportRequestModel airportRequestModel) {
        return airportRepository.findAirportByAirportId(airportId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Airport id not found: " + airportId))))
                .flatMap(foundAirport -> {
                    foundAirport.setName(airportRequestModel.getName());
                    foundAirport.setCityId(airportRequestModel.getCityId());
                    return airportRepository.save(foundAirport);
                })
                .doOnSuccess(updatedCountry -> log.info("Updated country: {}", updatedCountry))
                .map(EntityModelUtil::toAirportResponseModel);


    }

    @Override
    public Mono<Void> deleteAirport(String airportId) {
        return airportRepository.findAirportByAirportId(airportId)
                .switchIfEmpty(Mono.error(new NotFoundException("Airport id not found: " + airportId)))
                .flatMap(airportRepository::delete)
                .doOnSuccess(unused -> log.info("Deleted airport with id: {}", airportId));
    }
}
