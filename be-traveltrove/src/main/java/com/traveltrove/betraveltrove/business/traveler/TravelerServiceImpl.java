package com.traveltrove.betraveltrove.business.traveler;

import com.traveltrove.betraveltrove.business.country.CountryService;
import com.traveltrove.betraveltrove.dataaccess.traveler.Traveler;
import com.traveltrove.betraveltrove.dataaccess.traveler.TravelerRepository;
import com.traveltrove.betraveltrove.presentation.traveler.TravelerRequestModel;
import com.traveltrove.betraveltrove.presentation.traveler.TravelerResponseModel;
import com.traveltrove.betraveltrove.presentation.traveler.TravelerWithIdRequestModel;
import com.traveltrove.betraveltrove.utils.EntityModelUtil;
import com.traveltrove.betraveltrove.utils.entitymodelyutils.TravelerEntityModelUtil;
import com.traveltrove.betraveltrove.utils.exceptions.InvalidInputException;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class TravelerServiceImpl implements TravelerService {

    public TravelerRepository travelerRepository;

    public CountryService countryService;

    public TravelerServiceImpl(TravelerRepository travelerRepository, CountryService countryService) {
        this.travelerRepository = travelerRepository;
        this.countryService = countryService;
    }
    @Override
    public Mono<TravelerResponseModel> getTravelerByTravelerId(String travelerId) {
        if (travelerId != null && !travelerId.isEmpty()) {
            return travelerRepository.findTravelerByTravelerId(travelerId)
                    .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Traveler id not found: " + travelerId))))
                    .map(TravelerEntityModelUtil::toTravelerResponseModel);
        } else {
            return Mono.error(new InvalidInputException("Traveler id must be provided"));
        }
    }

    @Override
    public Flux<TravelerResponseModel> getAllTravelers(String firstName) {
        if (firstName != null && !firstName.isEmpty()) {
            return travelerRepository.findTravelerByFirstName(firstName)
                    .map(TravelerEntityModelUtil::toTravelerResponseModel);
        } else {
            return travelerRepository.findAll()
                    .map(TravelerEntityModelUtil::toTravelerResponseModel);
        }
    }

    @Override
    public Mono<TravelerResponseModel> createTraveler(TravelerRequestModel travelerRequestModel) {
        return validateTravelerRequest(travelerRequestModel)
                .flatMap(validatedTraveler -> travelerRepository.save(validatedTraveler))
                .map(TravelerEntityModelUtil::toTravelerResponseModel);
    }

    @Override
    public Mono<TravelerResponseModel> createTravelerUser(TravelerWithIdRequestModel travelerWithIdRequestModel) {
        return travelerRepository.save(TravelerEntityModelUtil.toTravelerUserEntity(travelerWithIdRequestModel))
                .map(TravelerEntityModelUtil::toTravelerResponseModel);
    }

    @Override
    public Mono<TravelerResponseModel> updateTraveler(String travelerId, TravelerRequestModel travelerRequestModel) {
        return travelerRepository.findTravelerByTravelerId(travelerId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Traveler id not found: " + travelerId))))
                .flatMap(traveler -> validateTravelerRequest(travelerRequestModel)
                        .map(validatedTraveler -> {
                            validatedTraveler.setTravelerId(traveler.getTravelerId());
                            validatedTraveler.setId(traveler.getId());
                            return validatedTraveler;
                        }))
                .flatMap(travelerRepository::save)
                .map(TravelerEntityModelUtil::toTravelerResponseModel);
    }

    @Override
    public Mono<TravelerResponseModel> deleteTraveler(String travelerId) {
        return travelerRepository.findTravelerByTravelerId(travelerId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Traveler id not found: " + travelerId))))
                .flatMap(traveler -> travelerRepository.delete(traveler).thenReturn(traveler))
                .map(TravelerEntityModelUtil::toTravelerResponseModel);
    }

    public Mono<Traveler> validateTravelerRequest(TravelerRequestModel travelerRequestModel) {
        log.info("Validating traveler request with countryId: {}", travelerRequestModel.getCountryId());
        if (travelerRequestModel.getFirstName() == null || travelerRequestModel.getFirstName().isBlank()) {
            return Mono.error(new InvalidInputException("Traveler first name is required"));
        }
        if (travelerRequestModel.getLastName() == null || travelerRequestModel.getLastName().isBlank()) {
            return Mono.error(new InvalidInputException("Traveler last name is required"));
        }
        if (travelerRequestModel.getAddressLine1() == null || travelerRequestModel.getAddressLine1().isBlank()) {
            return Mono.error(new InvalidInputException("Traveler address line 1 is required"));
        }
//        if (travelerRequestModel.getCity() == null || travelerRequestModel.getCity().isBlank()) {
//            return Mono.error(new InvalidInputException("Traveler city is required"));
//        }
//        if (travelerRequestModel.getState() == null || travelerRequestModel.getState().isBlank()) {
//            return Mono.error(new InvalidInputException("Traveler state is required"));
//        }
//        if (travelerRequestModel.getCountryId() == null || travelerRequestModel.getCountryId().isBlank()) {
//            return Mono.error(new InvalidInputException("Traveler country id is required"));
//        }
        if (travelerRequestModel.getEmail() == null || travelerRequestModel.getEmail().isBlank()) {
            return Mono.error(new InvalidInputException("Traveler email is required"));
        }

        // return countryService.getCountryById(travelerRequestModel.getCountryId())
        //         .switchIfEmpty(Mono.error(new NotFoundException("Country id not found: " + travelerRequestModel.getCountryId())))
        //         .flatMap(country -> {
        //             log.info("Country validated: {}", country);
        //             return Mono.just(TravelerEntityModelUtil.toTravelerEntity(travelerRequestModel));
        //         });
        return Mono.just(TravelerEntityModelUtil.toTravelerEntity(travelerRequestModel));

    }
}
