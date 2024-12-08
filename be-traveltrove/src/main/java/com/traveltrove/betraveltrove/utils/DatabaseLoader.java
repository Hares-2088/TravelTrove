package com.traveltrove.betraveltrove.utils;

import com.traveltrove.betraveltrove.dataaccess.country.Country;
import com.traveltrove.betraveltrove.dataaccess.country.CountryRepository;
import com.traveltrove.betraveltrove.dataaccess.events.Event;
import com.traveltrove.betraveltrove.dataaccess.events.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.traveltrove.betraveltrove.utils.EntityModelUtil.generateUUIDString;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseLoader {

    // Countries :)
    private final CountryRepository countryRepository;

    @PostConstruct
    public void loadData() {
        List<Country> sampleCountries = List.of(
                new Country(null, "ad633b50-83d4-41f3-866a-26452bdd6f33", "United States", "usa.png"),
                new Country(null, "b1db23af-4f2d-4138-b623-e906f5287e90", "Canada", "canada.png"),
                new Country(null, "118495e2-e1ce-4987-bb09-a95f93515bda", "France", "france.png"),
                new Country(null, "cf09374e-6d51-47ec-91e1-2b69a788457f", "Germany", "germany.png"),
                new Country(null, "dde33653-fc58-457c-9d33-a322a2a82835", "Italy", "italy.png"),
                new Country(null, "f05f8f2c-f86d-4cf5-8a9b-19e70f1b96d5", "Japan", "japan.png"),
                new Country(null, "b511c7ee-c0f3-4902-9bfa-d50f59ef2b3c", "Brazil", "brazil.png"),
                new Country(null, "d5d4e5e2-f960-4cf4-beed-c38104e5d5b4", "Australia", "australia.png"),
                new Country(null, "3cd2ad86-26cc-42ad-8b20-8b0b6e6d2a2e", "India", "india.png"),
                new Country(null, "877ec1c0-ffab-449e-a2ec-08f95db58f55", "China", "china.png")
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

    // Events :)
    private final EventRepository eventRepository;

    @PostConstruct
    public void loadEvents() {
        List<Event> sampleEvents = List.of(
                Event.builder()
                        .id(null)
                        .eventId("EV01")
                        .cityId(null)
                        .countryId("b1db23af-4f2d-4138-b623-e906f5287e90")
                        .name("Spring Festival")
                        .image("spring_festival.png")
                        .description("A vibrant festival celebrating the arrival of spring.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("EV02")
                        .cityId(generateUUIDString())
                        .countryId("118495e2-e1ce-4987-bb09-a95f93515bda")
                        .name("Tech Expo 2024")
                        .image("tech_expo.png")
                        .description("Showcasing the latest advancements in technology.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("EV03")
                        .cityId(null)
                        .countryId("cf09374e-6d51-47ec-91e1-2b69a788457f")
                        .name("Wine Tasting Retreat")
                        .image("wine_tasting.png")
                        .description("A relaxing getaway featuring fine wines and scenic views.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("EV04")
                        .cityId(generateUUIDString())
                        .countryId("dde33653-fc58-457c-9d33-a322a2a82835")
                        .name("City Marathon")
                        .image("city_marathon.png")
                        .description("An exciting marathon through the heart of the city.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("EV05")
                        .cityId(generateUUIDString())
                        .countryId("f05f8f2c-f86d-4cf5-8a9b-19e70f1b96d5")
                        .name("Music Gala Night")
                        .image("music_gala.png")
                        .description("A spectacular evening of music and entertainment.")
                        .build()
        );

        eventRepository.deleteAll()
                .thenMany(Flux.fromIterable(sampleEvents))
                .flatMap(eventRepository::save)
                .doOnNext(event -> log.info("Preloaded event: {}", event))
                .subscribe(
                        success -> log.info("Events preloaded successfully."),
                        error -> log.error("Error preloading events: {}", error.getMessage())
                );
    }

}
