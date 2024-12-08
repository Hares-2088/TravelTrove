package com.traveltrove.betraveltrove.business.country;

import com.traveltrove.betraveltrove.dataaccess.country.Country;
import com.traveltrove.betraveltrove.dataaccess.country.CountryRepository;
import com.traveltrove.betraveltrove.presentation.country.CountryRequestModel;
import com.traveltrove.betraveltrove.presentation.country.CountryResponseModel;
import com.traveltrove.betraveltrove.utils.EntityModelUtil;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public Flux<CountryResponseModel> getAllCountries() {
        return countryRepository.findAll()
                .map(EntityModelUtil::toCountryResponseModel);
    }

    @Override
    public Mono<CountryResponseModel> getCountryById(String countryId) {
        return countryRepository.findCountryByCountryId(countryId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Country id not found: " + countryId))))
                .map(EntityModelUtil::toCountryResponseModel);
    }

    @Override
    public Mono<CountryResponseModel> addCountry(Country country) {
        return countryRepository.save(country)
                .doOnSuccess(savedCountry -> log.info("Added new country: {}", savedCountry))
                .map(EntityModelUtil::toCountryResponseModel);
    }

    @Override
    public Mono<CountryResponseModel> updateCountry(String countryId, CountryRequestModel countryRequestModel) {
        return countryRepository.findCountryByCountryId(countryId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Country id not found: " + countryId))))
                .flatMap(existingCountry -> {
                    // Update fields on the existing country
                    existingCountry.setName(countryRequestModel.getName());
                    existingCountry.setImage(countryRequestModel.getImage());
                    return countryRepository.save(existingCountry);
                })
                .doOnSuccess(updatedCountry -> log.info("Updated country: {}", updatedCountry))
                .map(EntityModelUtil::toCountryResponseModel);
    }

    @Override
    public Mono<Void> deleteCountry(String countryId) {
        return countryRepository.findCountryByCountryId(countryId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Country id not found: " + countryId))))
                .flatMap(countryRepository::delete)
                .doOnSuccess(unused -> log.info("Deleted country with id: {}", countryId));
    }
}
