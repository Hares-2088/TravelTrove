package com.traveltrove.betraveltrove.utils;

import com.traveltrove.betraveltrove.dataaccess.city.City;
import com.traveltrove.betraveltrove.dataaccess.city.CityRepository;
import com.traveltrove.betraveltrove.dataaccess.country.Country;
import com.traveltrove.betraveltrove.dataaccess.country.CountryRepository;
import com.traveltrove.betraveltrove.dataaccess.events.Event;
import com.traveltrove.betraveltrove.dataaccess.events.EventRepository;
import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import com.traveltrove.betraveltrove.dataaccess.tour.TourEvent;
import com.traveltrove.betraveltrove.dataaccess.tour.TourEventRepository;
import com.traveltrove.betraveltrove.dataaccess.tour.TourRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseLoader {

    // Countries :)
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;

    @PostConstruct
    public void loadCountriesAndCities() {
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
        List<City> sampleCities = List.of(
                new City(null, "b713c09a-9c3e-4b30-872a-4d89089badd0", "New York", sampleCountries.get(0).getCountryId()),
                new City(null, "11a85d86-3fc8-4504-b1e9-25fd87eba3cf", "Toronto", sampleCountries.get(1).getCountryId()),
                new City(null, "f5ad630f-830d-47a8-a26c-76b87163a7e4", "Paris", sampleCountries.get(2).getCountryId()),
                new City(null, "000f3f3a-8ee2-4690-be4d-a5bd38a5f06f", "Berlin", sampleCountries.get(3).getCountryId()),
                new City(null, "7f15fafc-85f4-4ba5-822b-27b7ddce6c37", "Rome", sampleCountries.get(4).getCountryId()),
                new City(null, "92537e75-3fc2-42af-b105-b6150395acbb", "Tokyo", sampleCountries.get(5).getCountryId()),
                new City(null, "affc7cc1-b3d8-4146-bc39-6e9ff1e66704", "Rio de Janeiro", sampleCountries.get(6).getCountryId()),
                new City(null, "f978f76c-abfc-4b25-ba32-9e1b085b5ab0", "Sydney", sampleCountries.get(7).getCountryId()),
                new City(null, "0361c975-fe28-4817-be51-6864b8a2bf38", "Mumbai", sampleCountries.get(8).getCountryId()),
                new City(null, "2fce64d8-dfa6-4abe-9a84-af7aaac1293f", "Beijing", sampleCountries.get(9).getCountryId())
        );

        countryRepository.deleteAll()
                .thenMany(Flux.fromIterable(sampleCountries))
                .flatMap(countryRepository::save)
                .doOnNext(country -> log.info("Preloaded country: {}", country))
                .subscribe(
                        success -> log.info("Database preloaded successfully."),
                        error -> log.error("Error preloading database: {}", error.getMessage())
                );

        cityRepository.deleteAll()
                .thenMany(Flux.fromIterable(sampleCities))
                .flatMap(cityRepository::save)
                .doOnNext(city -> log.info("Preloaded city: {}", city))
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
                        .eventId("91c940b1-24e8-463f-96ef-f54f7e4aaf1d")
                        .cityId("b713c09a-9c3e-4b30-872a-4d89089badd0") // New York
                        .countryId("ad633b50-83d4-41f3-866a-26452bdd6f33") // United States
                        .name("New York Summer Carnival")
                        .image("ny_summer_carnival.png")
                        .description("Experience the thrill of summer with live music, street parades, and food festivals across New York City.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("87b3b478-031c-49d2-b64e-b54a49578d8c")
                        .cityId("11a85d86-3fc8-4504-b1e9-25fd87eba3cf") // Toronto
                        .countryId("b1db23af-4f2d-4138-b623-e906f5287e90") // Canada
                        .name("Toronto Winter Lights Festival")
                        .image("toronto_winter_lights.png")
                        .description("Enjoy dazzling light displays and magical winter-themed experiences throughout Toronto’s iconic landmarks.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("e48b9f26-fec7-4ce0-8f98-010ee0e6bd6c")
                        .cityId("f5ad630f-830d-47a8-a26c-76b87163a7e4") // Paris
                        .countryId("118495e2-e1ce-4987-bb09-a95f93515bda") // France
                        .name("Paris Fashion Week")
                        .image("paris_fashion_week.png")
                        .description("Discover the world of high fashion and luxury with runway shows, designer exhibitions, and celebrity sightings.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761950")
                        .cityId("000f3f3a-8ee2-4690-be4d-a5bd38a5f06f") // Berlin
                        .countryId("cf09374e-6d51-47ec-91e1-2b69a788457f") // Germany
                        .name("Berlin Tech Conference")
                        .image("berlin_tech_conference.png")
                        .description("Explore groundbreaking innovations and network with industry leaders at Europe’s largest tech conference.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("44475ce1-6358-4d2f-a497-4ca3032c495c")
                        .cityId("7f15fafc-85f4-4ba5-822b-27b7ddce6c37") // Rome
                        .countryId("dde33653-fc58-457c-9d33-a322a2a82835") // Italy
                        .name("Rome Food & Wine Festival")
                        .image("rome_food_wine_festival.png")
                        .description("Indulge in authentic Italian cuisine, world-class wines, and cultural performances in the heart of Rome.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("3ea4a674-3463-4fa2-a3cf-d882485963b6")
                        .cityId("92537e75-3fc2-42af-b105-b6150395acbb") // Tokyo
                        .countryId("f05f8f2c-f86d-4cf5-8a9b-19e70f1b96d5") // Japan
                        .name("Tokyo Cherry Blossom Festival")
                        .image("tokyo_cherry_blossom.png")
                        .description("Celebrate the arrival of spring with breathtaking cherry blossoms, cultural exhibits, and traditional performances.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("528398a9-91a1-4833-bdd1-d88b732b9be6")
                        .cityId("affc7cc1-b3d8-4146-bc39-6e9ff1e66704") // Rio de Janeiro
                        .countryId("b511c7ee-c0f3-4902-9bfa-d50f59ef2b3c") // Brazil
                        .name("Rio Carnival")
                        .image("rio_carnival.png")
                        .description("Join the world's biggest street party with samba parades, dazzling costumes, and endless celebrations in Rio.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("72927c9c-750f-4b37-9799-16d3b7a59725")
                        .cityId("f978f76c-abfc-4b25-ba32-9e1b085b5ab0") // Sydney
                        .countryId("d5d4e5e2-f960-4cf4-beed-c38104e5d5b4") // Australia
                        .name("Sydney New Year’s Eve Fireworks")
                        .image("sydney_fireworks.png")
                        .description("Ring in the new year with one of the world's most spectacular fireworks displays over Sydney Harbour.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("82c07322-37da-4635-bcae-3ae241036cf8")
                        .cityId("0361c975-fe28-4817-be51-6864b8a2bf38") // Mumbai
                        .countryId("3cd2ad86-26cc-42ad-8b20-8b0b6e6d2a2e") // India
                        .name("Mumbai Film Festival")
                        .image("mumbai_film_festival.png")
                        .description("Celebrate the art of cinema with film screenings, celebrity interviews, and cultural showcases in Mumbai.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("4e5bf569-27cb-494b-b7e5-9f9bf0ca66fe")
                        .cityId("2fce64d8-dfa6-4abe-9a84-af7aaac1293f") // Beijing
                        .countryId("877ec1c0-ffab-449e-a2ec-08f95db58f55") // China
                        .name("Beijing Dragon Boat Festival")
                        .image("beijing_dragon_boat.png")
                        .description("Experience the thrilling dragon boat races and traditional festivities along Beijing’s scenic waterways.")
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

    // Tours
    private final TourRepository tourRepository;

    @PostConstruct
    public void loadTours() {
        List<Tour> sampleTours = List.of(
                Tour.builder()
                        .id(null)
                        .tourId("7f54a45d-8c1d-432f-a5c8-1f93b89bfe29")
                        .name("Grand American Adventure")
                        .description("Explore the vast landscapes of the United States, from the Grand Canyon to the streets of New York City.")
                        .build(),
                Tour.builder()
                        .id(null)
                        .tourId("90af5e42-b8a7-4b59-939f-8fa2c4e384d1")
                        .name("Canadian Rockies Expedition")
                        .description("Experience Canada’s majestic Rockies with breathtaking views, wildlife safaris, and serene mountain resorts.")
                        .build(),
                Tour.builder()
                        .id(null)
                        .tourId("f1f4ac8e-4e24-4bb3-97d3-e5b7b5b7f912")
                        .name("Parisian Art & Culture Tour")
                        .description("Discover France’s iconic landmarks, including the Eiffel Tower, the Louvre Museum, and charming Parisian streets.")
                        .build(),
                Tour.builder()
                        .id(null)
                        .tourId("83e6f7ba-0567-426a-b43f-456f947c576b")
                        .name("German Castle Escape")
                        .description("Journey through Germany’s historic castles, scenic villages, and stunning landscapes along the Romantic Road.")
                        .build(),
                Tour.builder()
                        .id(null)
                        .tourId("d12a7bf7-dc94-4953-9189-68f6a70f7844")
                        .name("Italian Culinary Retreat")
                        .description("Savor Italy’s finest cuisine with wine tastings in Tuscany, pasta-making workshops, and cultural explorations in Rome.")
                        .build(),
                Tour.builder()
                        .id(null)
                        .tourId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e39")
                        .name("Japan Cherry Blossom Tour")
                        .description("Witness the stunning beauty of Japan’s cherry blossoms while visiting Kyoto's temples, Mt. Fuji, and Tokyo’s bustling streets.")
                        .build(),
                Tour.builder()
                        .id(null)
                        .tourId("42cf8561-6f83-4f2f-becb-e3c72a1bb572")
                        .name("Brazilian Amazon Adventure")
                        .description("Embark on an unforgettable journey into the Amazon rainforest with guided eco-tours and immersive wildlife experiences.")
                        .build(),
                Tour.builder()
                        .id(null)
                        .tourId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec5")
                        .name("Australian Outback Expedition")
                        .description("Discover Australia’s rugged outback, from the stunning Uluru rock formation to unique wildlife encounters.")
                        .build(),
                Tour.builder()
                        .id(null)
                        .tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                        .name("Indian Heritage Journey")
                        .description("Explore India’s cultural treasures, including the Taj Mahal, Jaipur’s palaces, and spiritual experiences along the Ganges.")
                        .build(),
                Tour.builder()
                        .id(null)
                        .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                        .name("Chinese Silk Road Adventure")
                        .description("Travel along China’s historic Silk Road, visiting ancient cities, the Great Wall, and vibrant cultural heritage sites.")
                        .build()
        );

        tourRepository.deleteAll()
                .thenMany(Flux.fromIterable(sampleTours))
                .flatMap(tourRepository::save)
                .doOnNext(tour -> log.info("Preloaded tour: {}", tour))
                .subscribe(
                        success -> log.info("Tours preloaded successfully."),
                        error -> log.error("Error preloading tours: {}", error.getMessage())
                );
    }

    private final TourEventRepository tourEventRepository;

    @PostConstruct
    public void loadTourEvents() {
        List<TourEvent> sampleTourEvents = List.of(
                // Grand American Adventure
                TourEvent.builder()
                        .Id(null)
                        .seq(1)
                        .seqDesc("Start at New York Summer Carnival")
                        .tourId("7f54a45d-8c1d-432f-a5c8-1f93b89bfe29")
                        .eventId("91c940b1-24e8-463f-96ef-f54f7e4aaf1d")
                        .build(),

                // Canadian Rockies Expedition
                TourEvent.builder()
                        .Id(null)
                        .seq(1)
                        .seqDesc("Explore Toronto's Winter Lights Festival")
                        .tourId("90af5e42-b8a7-4b59-939f-8fa2c4e384d1")
                        .eventId("87b3b478-031c-49d2-b64e-b54a49578d8c")
                        .build(),

                // Parisian Art & Culture Tour
                TourEvent.builder()
                        .Id(null)
                        .seq(1)
                        .seqDesc("Visit Paris Fashion Week")
                        .tourId("f1f4ac8e-4e24-4bb3-97d3-e5b7b5b7f912")
                        .eventId("e48b9f26-fec7-4ce0-8f98-010ee0e6bd6c")
                        .build(),

                // German Castle Escape
                TourEvent.builder()
                        .Id(null)
                        .seq(1)
                        .seqDesc("Attend Berlin Tech Conference")
                        .tourId("83e6f7ba-0567-426a-b43f-456f947c576b")
                        .eventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761950")
                        .build(),

                // Italian Culinary Retreat
                TourEvent.builder()
                        .Id(null)
                        .seq(1)
                        .seqDesc("Enjoy Rome Food & Wine Festival")
                        .tourId("d12a7bf7-dc94-4953-9189-68f6a70f7844")
                        .eventId("44475ce1-6358-4d2f-a497-4ca3032c495c")
                        .build(),

                // Japan Cherry Blossom Tour
                TourEvent.builder()
                        .Id(null)
                        .seq(1)
                        .seqDesc("Join Tokyo Cherry Blossom Festival")
                        .tourId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e39")
                        .eventId("3ea4a674-3463-4fa2-a3cf-d882485963b6")
                        .build(),

                // Brazilian Amazon Adventure
                TourEvent.builder()
                        .Id(null)
                        .seq(1)
                        .seqDesc("Experience Rio Carnival")
                        .tourId("42cf8561-6f83-4f2f-becb-e3c72a1bb572")
                        .eventId("528398a9-91a1-4833-bdd1-d88b732b9be6")
                        .build(),

                // Australian Outback Expedition
                TourEvent.builder()
                        .Id(null)
                        .seq(1)
                        .seqDesc("Enjoy Sydney Fireworks")
                        .tourId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec5")
                        .eventId("72927c9c-750f-4b37-9799-16d3b7a59725")
                        .build(),

                // Indian Heritage Journey
                TourEvent.builder()
                        .Id(null)
                        .seq(1)
                        .seqDesc("Attend Mumbai Film Festival")
                        .tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                        .eventId("82c07322-37da-4635-bcae-3ae241036cf8")
                        .build(),

                // Chinese Silk Road Adventure
                TourEvent.builder()
                        .Id(null)
                        .seq(1)
                        .seqDesc("Experience Beijing Dragon Boat Festival")
                        .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                        .eventId("4e5bf569-27cb-494b-b7e5-9f9bf0ca66fe")
                        .build()
        );

        tourEventRepository.deleteAll()
                .thenMany(Flux.fromIterable(sampleTourEvents))
                .flatMap(tourEventRepository::save)
                .doOnNext(event -> log.info("Preloaded tour event: {}", event))
                .subscribe(
                        success -> log.info("Tour events preloaded successfully."),
                        error -> log.error("Error preloading tour events: {}", error.getMessage())
                );
    }

}