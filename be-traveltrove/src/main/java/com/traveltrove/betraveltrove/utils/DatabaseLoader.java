package com.traveltrove.betraveltrove.utils;

import com.traveltrove.betraveltrove.dataaccess.country.Country;
import com.traveltrove.betraveltrove.dataaccess.country.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import jakarta.annotation.PostConstruct;
import java.util.List;

import static com.traveltrove.betraveltrove.utils.EntityModelUtil.generateUUIDString;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseLoader {

    private final CountryRepository countryRepository;

    @PostConstruct
    public void loadData() {
        List<Country> sampleCountries = List.of(
                new Country(null, generateUUIDString(), "United States", "usa.png"),
                new Country(null, generateUUIDString(), "Canada", "canada.png"),
                new Country(null, generateUUIDString(), "France", "france.png"),
                new Country(null, generateUUIDString(), "Germany", "germany.png"),
                new Country(null, generateUUIDString(), "Italy", "italy.png"),
                new Country(null, generateUUIDString(), "Japan", "japan.png"),
                new Country(null, generateUUIDString(), "Brazil", "brazil.png"),
                new Country(null, generateUUIDString(), "Australia", "australia.png"),
                new Country(null, generateUUIDString(), "India", "india.png"),
                new Country(null, generateUUIDString(), "China", "china.png")
        );

        countryRepository.deleteAll()
                .thenMany(Flux.fromIterable(sampleCountries))
                .flatMap(countryRepository::save)
                .doOnNext(country -> log.info("Preloaded country: {}", country))
                .subscribe(
                        success -> log.info("Database preloaded successfully."),
                        error -> log.error("Error preloading database: {}", error.getMessage())
                );
    }
}
