package com.traveltrove.betraveltrove.utils;

import com.traveltrove.betraveltrove.dataaccess.airport.Airport;
import com.traveltrove.betraveltrove.dataaccess.airport.AirportRepository;
import com.traveltrove.betraveltrove.dataaccess.booking.Booking;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingRepository;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import com.traveltrove.betraveltrove.dataaccess.city.City;
import com.traveltrove.betraveltrove.dataaccess.city.CityRepository;
import com.traveltrove.betraveltrove.dataaccess.country.Country;
import com.traveltrove.betraveltrove.dataaccess.country.CountryRepository;
import com.traveltrove.betraveltrove.dataaccess.events.Event;
import com.traveltrove.betraveltrove.dataaccess.events.EventRepository;
import com.traveltrove.betraveltrove.dataaccess.hotel.Hotel;
import com.traveltrove.betraveltrove.dataaccess.hotel.HotelRepository;
import com.traveltrove.betraveltrove.dataaccess.notification.Notification;
import com.traveltrove.betraveltrove.dataaccess.notification.NotificationRepository;
import com.traveltrove.betraveltrove.dataaccess.payment.Payment;
import com.traveltrove.betraveltrove.dataaccess.payment.PaymentRepository;
import com.traveltrove.betraveltrove.dataaccess.review.Review;
import com.traveltrove.betraveltrove.dataaccess.review.ReviewRepository;
import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import com.traveltrove.betraveltrove.dataaccess.tour.TourEvent;
import com.traveltrove.betraveltrove.dataaccess.tour.TourEventRepository;
import com.traveltrove.betraveltrove.dataaccess.tour.TourRepository;
import com.traveltrove.betraveltrove.dataaccess.tourpackage.PackageRepository;
import com.traveltrove.betraveltrove.dataaccess.tourpackage.PackageStatus;
import com.traveltrove.betraveltrove.dataaccess.traveler.Traveler;
import com.traveltrove.betraveltrove.dataaccess.traveler.TravelerRepository;
import com.traveltrove.betraveltrove.dataaccess.user.User;
import com.traveltrove.betraveltrove.dataaccess.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
                        .eventImageUrl("ny_summer_carnival.png")
                        .description("Experience the thrill of summer with live music, street parades, and food festivals across New York City.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("87b3b478-031c-49d2-b64e-b54a49578d8c")
                        .cityId("11a85d86-3fc8-4504-b1e9-25fd87eba3cf") // Toronto
                        .countryId("b1db23af-4f2d-4138-b623-e906f5287e90") // Canada
                        .name("Toronto Winter Lights Festival")
                        .eventImageUrl("toronto_winter_lights.png")
                        .description("Enjoy dazzling light displays and magical winter-themed experiences throughout Toronto’s iconic landmarks.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("e48b9f26-fec7-4ce0-8f98-010ee0e6bd6c")
                        .cityId("f5ad630f-830d-47a8-a26c-76b87163a7e4") // Paris
                        .countryId("118495e2-e1ce-4987-bb09-a95f93515bda") // France
                        .name("Paris Fashion Week")
                        .eventImageUrl("paris_fashion_week.png")
                        .description("Discover the world of high fashion and luxury with runway shows, designer exhibitions, and celebrity sightings.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761950")
                        .cityId("000f3f3a-8ee2-4690-be4d-a5bd38a5f06f") // Berlin
                        .countryId("cf09374e-6d51-47ec-91e1-2b69a788457f") // Germany
                        .name("Berlin Tech Conference")
                        .eventImageUrl("berlin_tech_conference.png")
                        .description("Explore groundbreaking innovations and network with industry leaders at Europe’s largest tech conference.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("44475ce1-6358-4d2f-a497-4ca3032c495c")
                        .cityId("7f15fafc-85f4-4ba5-822b-27b7ddce6c37") // Rome
                        .countryId("dde33653-fc58-457c-9d33-a322a2a82835") // Italy
                        .name("Rome Food & Wine Festival")
                        .eventImageUrl("rome_food_wine_festival.png")
                        .description("Indulge in authentic Italian cuisine, world-class wines, and cultural performances in the heart of Rome.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("3ea4a674-3463-4fa2-a3cf-d882485963b6")
                        .cityId("92537e75-3fc2-42af-b105-b6150395acbb") // Tokyo
                        .countryId("f05f8f2c-f86d-4cf5-8a9b-19e70f1b96d5") // Japan
                        .name("Tokyo Cherry Blossom Festival")
                        .eventImageUrl("tokyo_cherry_blossom.png")
                        .description("Celebrate the arrival of spring with breathtaking cherry blossoms, cultural exhibits, and traditional performances.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("528398a9-91a1-4833-bdd1-d88b732b9be6")
                        .cityId("affc7cc1-b3d8-4146-bc39-6e9ff1e66704") // Rio de Janeiro
                        .countryId("b511c7ee-c0f3-4902-9bfa-d50f59ef2b3c") // Brazil
                        .name("Rio Carnival")
                        .eventImageUrl("rio_carnival.png")
                        .description("Join the world's biggest street party with samba parades, dazzling costumes, and endless celebrations in Rio.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("72927c9c-750f-4b37-9799-16d3b7a59725")
                        .cityId("f978f76c-abfc-4b25-ba32-9e1b085b5ab0") // Sydney
                        .countryId("d5d4e5e2-f960-4cf4-beed-c38104e5d5b4") // Australia
                        .name("Sydney New Year’s Eve Fireworks")
                        .eventImageUrl("sydney_fireworks.png")
                        .description("Ring in the new year with one of the world's most spectacular fireworks displays over Sydney Harbour.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("82c07322-37da-4635-bcae-3ae241036cf8")
                        .cityId("0361c975-fe28-4817-be51-6864b8a2bf38") // Mumbai
                        .countryId("3cd2ad86-26cc-42ad-8b20-8b0b6e6d2a2e") // India
                        .name("Mumbai Film Festival")
                        .eventImageUrl("mumbai_film_festival.png")
                        .description("Celebrate the art of cinema with film screenings, celebrity interviews, and cultural showcases in Mumbai.")
                        .build(),
                Event.builder()
                        .id(null)
                        .eventId("4e5bf569-27cb-494b-b7e5-9f9bf0ca66fe")
                        .cityId("2fce64d8-dfa6-4abe-9a84-af7aaac1293f") // Beijing
                        .countryId("877ec1c0-ffab-449e-a2ec-08f95db58f55") // China
                        .name("Beijing Dragon Boat Festival")
                        .eventImageUrl("beijing_dragon_boat.png")
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
                        .tourEventId("06f7bca2-4cf2-4d4f-a6b4-493c90f7b3dq")
                        .seq(1)
                        .seqDesc("Start at New York Summer Carnival")
                        .tourId("7f54a45d-8c1d-432f-a5c8-1f93b89bfe29")
                        .eventId("91c940b1-24e8-463f-96ef-f54f7e4aaf1d")
                        .hotelId("d1e91f6c-723e-43b9-812f-2f3d3bfb4081")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("06f7bca2-4cf2-4d4f-a6b4-493c90f7b3dw")
                        .seq(2)
                        .seqDesc("Start at New York Summer Carnival")
                        .tourId("7f54a45d-8c1d-432f-a5c8-1f93b89bfe29")
                        .eventId("91c940b1-24e8-463f-96ef-f54f7e4aaf1d")
                        .hotelId("d1e91f6c-723e-43b9-812f-2f3d3bfb4081")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("06f7bca2-4cf2-4d4f-a6b4-493c90f7b3de")
                        .seq(3)
                        .seqDesc("Start at New York Summer Carnival")
                        .tourId("7f54a45d-8c1d-432f-a5c8-1f93b89bfe29")
                        .eventId("91c940b1-24e8-463f-96ef-f54f7e4aaf1d")
                        .hotelId("d1e91f6c-723e-43b9-812f-2f3d3bfb4081")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("06f7bca2-4cf2-4d4f-a6b4-493c90f7b3dr")
                        .seq(4)
                        .seqDesc("Start at New York Summer Carnival")
                        .tourId("7f54a45d-8c1d-432f-a5c8-1f93b89bfe29")
                        .eventId("91c940b1-24e8-463f-96ef-f54f7e4aaf1d")
                        .hotelId("d1e91f6c-723e-43b9-812f-2f3d3bfb4081")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("06f7bca2-4cf2-4d4f-a6b4-493c90f7b3dt")
                        .seq(5)
                        .seqDesc("Start at New York Summer Carnival")
                        .tourId("7f54a45d-8c1d-432f-a5c8-1f93b89bfe29")
                        .eventId("91c940b1-24e8-463f-96ef-f54f7e4aaf1d")
                        .hotelId("d1e91f6c-723e-43b9-812f-2f3d3bfb4081")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("06f7bca2-4cf2-4d4f-a6b4-493c90f7b3dy")
                        .seq(6)
                        .seqDesc("Start at New York Summer Carnival")
                        .tourId("7f54a45d-8c1d-432f-a5c8-1f93b89bfe29")
                        .eventId("91c940b1-24e8-463f-96ef-f54f7e4aaf1d")
                        .hotelId("d1e91f6c-723e-43b9-812f-2f3d3bfb4081")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("06f7bca2-4cf2-4d4f-a6b4-493c90f7b3du")
                        .seq(7)
                        .seqDesc("Start at New York Summer Carnival")
                        .tourId("7f54a45d-8c1d-432f-a5c8-1f93b89bfe29")
                        .eventId("91c940b1-24e8-463f-96ef-f54f7e4aaf1d")
                        .hotelId("d1e91f6c-723e-43b9-812f-2f3d3bfb4081")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("06f7bca2-4cf2-4d4f-a6b4-493c90f7b3di")
                        .seq(8)
                        .seqDesc("Start at New York Summer Carnival")
                        .tourId("7f54a45d-8c1d-432f-a5c8-1f93b89bfe29")
                        .eventId("91c940b1-24e8-463f-96ef-f54f7e4aaf1d")
                        .hotelId("d1e91f6c-723e-43b9-812f-2f3d3bfb4081")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("06f7bca2-4cf2-4d4f-a6b4-493c90f7b3do")
                        .seq(9)
                        .seqDesc("Start at New York Summer Carnival")
                        .tourId("7f54a45d-8c1d-432f-a5c8-1f93b89bfe29")
                        .eventId("91c940b1-24e8-463f-96ef-f54f7e4aaf1d")
                        .hotelId("d1e91f6c-723e-43b9-812f-2f3d3bfb4081")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("06f7bca2-4cf2-4d4f-a6b4-493c90f7b3da")
                        .seq(10)
                        .seqDesc("Start at New York Summer Carnival")
                        .tourId("7f54a45d-8c1d-432f-a5c8-1f93b89bfe29")
                        .eventId("91c940b1-24e8-463f-96ef-f54f7e4aaf1d")
                        .hotelId("d1e91f6c-723e-43b9-812f-2f3d3bfb4081")
                        .build(),

                // Canadian Rockies Expedition
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("9d2b17f6-4a3c-46c2-b9f1-8d5e3a1d2c68")
                        .seq(1)
                        .seqDesc("Explore Toronto's Winter Lights Festival")
                        .tourId("90af5e42-b8a7-4b59-939f-8fa2c4e384d1")
                        .eventId("87b3b478-031c-49d2-b64e-b54a49578d8c")
                        .hotelId("f7a7e8e4-cc49-4027-8c4d-881e6179e6d2")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("9d2b17f6-4a3c-46c2-b9f1-8d5e3a1d2c67")
                        .seq(2)
                        .seqDesc("Explore Toronto's Winter Lights Festival")
                        .tourId("90af5e42-b8a7-4b59-939f-8fa2c4e384d1")
                        .eventId("87b3b478-031c-49d2-b64e-b54a49578d8c")
                        .hotelId("f7a7e8e4-cc49-4027-8c4d-881e6179e6d2")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("9d2b17f6-4a3c-46c2-b9f1-8d5e3a1d2c66")
                        .seq(3)
                        .seqDesc("Explore Toronto's Winter Lights Festival")
                        .tourId("90af5e42-b8a7-4b59-939f-8fa2c4e384d1")
                        .eventId("87b3b478-031c-49d2-b64e-b54a49578d8c")
                        .hotelId("f7a7e8e4-cc49-4027-8c4d-881e6179e6d2")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("9d2b17f6-4a3c-46c2-b9f1-8d5e3a1d2c65")
                        .seq(4)
                        .seqDesc("Explore Toronto's Winter Lights Festival")
                        .tourId("90af5e42-b8a7-4b59-939f-8fa2c4e384d1")
                        .eventId("87b3b478-031c-49d2-b64e-b54a49578d8c")
                        .hotelId("f7a7e8e4-cc49-4027-8c4d-881e6179e6d2")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("9d2b17f6-4a3c-46c2-b9f1-8d5e3a1d2c64")
                        .seq(5)
                        .seqDesc("Explore Toronto's Winter Lights Festival")
                        .tourId("90af5e42-b8a7-4b59-939f-8fa2c4e384d1")
                        .eventId("87b3b478-031c-49d2-b64e-b54a49578d8c")
                        .hotelId("f7a7e8e4-cc49-4027-8c4d-881e6179e6d2")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("9d2b17f6-4a3c-46c2-b9f1-8d5e3a1d2c63")
                        .seq(6)
                        .seqDesc("Explore Toronto's Winter Lights Festival")
                        .tourId("90af5e42-b8a7-4b59-939f-8fa2c4e384d1")
                        .eventId("87b3b478-031c-49d2-b64e-b54a49578d8c")
                        .hotelId("f7a7e8e4-cc49-4027-8c4d-881e6179e6d2")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("9d2b17f6-4a3c-46c2-b9f1-8d5e3a1d2c62")
                        .seq(7)
                        .seqDesc("Explore Toronto's Winter Lights Festival")
                        .tourId("90af5e42-b8a7-4b59-939f-8fa2c4e384d1")
                        .eventId("87b3b478-031c-49d2-b64e-b54a49578d8c")
                        .hotelId("f7a7e8e4-cc49-4027-8c4d-881e6179e6d2")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("9d2b17f6-4a3c-46c2-b9f1-8d5e3a1d2c61")
                        .seq(8)
                        .seqDesc("Explore Toronto's Winter Lights Festival")
                        .tourId("90af5e42-b8a7-4b59-939f-8fa2c4e384d1")
                        .eventId("87b3b478-031c-49d2-b64e-b54a49578d8c")
                        .hotelId("f7a7e8e4-cc49-4027-8c4d-881e6179e6d2")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("9d2b17f6-4a3c-46c2-b9f1-8d5e3a1d2c69")
                        .seq(9)
                        .seqDesc("Explore Toronto's Winter Lights Festival")
                        .tourId("90af5e42-b8a7-4b59-939f-8fa2c4e384d1")
                        .eventId("87b3b478-031c-49d2-b64e-b54a49578d8c")
                        .hotelId("f7a7e8e4-cc49-4027-8c4d-881e6179e6d2")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("9d2b17f6-4a3c-46c2-b9f1-8d5e3a1d2c60")
                        .seq(10)
                        .seqDesc("Explore Toronto's Winter Lights Festival")
                        .tourId("90af5e42-b8a7-4b59-939f-8fa2c4e384d1")
                        .eventId("87b3b478-031c-49d2-b64e-b54a49578d8c")
                        .hotelId("f7a7e8e4-cc49-4027-8c4d-881e6179e6d2")
                        .build(),

                // Parisian Art & Culture Tour
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("e48a5f2e-fc72-4ce0-8f98-010ee0e6bd6c")
                        .seq(1)
                        .seqDesc("Visit Paris Fashion Week")
                        .tourId("f1f4ac8e-4e24-4bb3-97d3-e5b7b5b7f912")
                        .eventId("e48b9f26-fec7-4ce0-8f98-010ee0e6bd6c")
                        .hotelId("b2c4f9ac-0fd6-4327-8e3a-f3c68bd3d62a")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("e48a5f2e-fc72-4ce0-8f98-010ee0e6bd6a")
                        .seq(2)
                        .seqDesc("Visit Paris Fashion Week")
                        .tourId("f1f4ac8e-4e24-4bb3-97d3-e5b7b5b7f912")
                        .eventId("e48b9f26-fec7-4ce0-8f98-010ee0e6bd6c")
                        .hotelId("b2c4f9ac-0fd6-4327-8e3a-f3c68bd3d62a")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("e48a5f2e-fc72-4ce0-8f98-010ee0e6bd6b")
                        .seq(3)
                        .seqDesc("Visit Paris Fashion Week")
                        .tourId("f1f4ac8e-4e24-4bb3-97d3-e5b7b5b7f912")
                        .eventId("e48b9f26-fec7-4ce0-8f98-010ee0e6bd6c")
                        .hotelId("b2c4f9ac-0fd6-4327-8e3a-f3c68bd3d62a")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("e48a5f2e-fc72-4ce0-8f98-010ee0e6bd6d")
                        .seq(4)
                        .seqDesc("Visit Paris Fashion Week")
                        .tourId("f1f4ac8e-4e24-4bb3-97d3-e5b7b5b7f912")
                        .eventId("e48b9f26-fec7-4ce0-8f98-010ee0e6bd6c")
                        .hotelId("b2c4f9ac-0fd6-4327-8e3a-f3c68bd3d62a")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("e48a5f2e-fc72-4ce0-8f98-010ee0e6bd6e")
                        .seq(5)
                        .seqDesc("Visit Paris Fashion Week")
                        .tourId("f1f4ac8e-4e24-4bb3-97d3-e5b7b5b7f912")
                        .eventId("e48b9f26-fec7-4ce0-8f98-010ee0e6bd6c")
                        .hotelId("b2c4f9ac-0fd6-4327-8e3a-f3c68bd3d62a")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("e48a5f2e-fc72-4ce0-8f98-010ee0e6bd6f")
                        .seq(6)
                        .seqDesc("Visit Paris Fashion Week")
                        .tourId("f1f4ac8e-4e24-4bb3-97d3-e5b7b5b7f912")
                        .eventId("e48b9f26-fec7-4ce0-8f98-010ee0e6bd6c")
                        .hotelId("b2c4f9ac-0fd6-4327-8e3a-f3c68bd3d62a")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("e48a5f2e-fc72-4ce0-8f98-010ee0e6bd6t")
                        .seq(7)
                        .seqDesc("Visit Paris Fashion Week")
                        .tourId("f1f4ac8e-4e24-4bb3-97d3-e5b7b5b7f912")
                        .eventId("e48b9f26-fec7-4ce0-8f98-010ee0e6bd6c")
                        .hotelId("b2c4f9ac-0fd6-4327-8e3a-f3c68bd3d62a")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("e48a5f2e-fc72-4ce0-8f98-010ee0e6bd6y")
                        .seq(8)
                        .seqDesc("Visit Paris Fashion Week")
                        .tourId("f1f4ac8e-4e24-4bb3-97d3-e5b7b5b7f912")
                        .eventId("e48b9f26-fec7-4ce0-8f98-010ee0e6bd6c")
                        .hotelId("b2c4f9ac-0fd6-4327-8e3a-f3c68bd3d62a")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("e48a5f2e-fc72-4ce0-8f98-010ee0e6bd6u")
                        .seq(9)
                        .seqDesc("Visit Paris Fashion Week")
                        .tourId("f1f4ac8e-4e24-4bb3-97d3-e5b7b5b7f912")
                        .eventId("e48b9f26-fec7-4ce0-8f98-010ee0e6bd6c")
                        .hotelId("b2c4f9ac-0fd6-4327-8e3a-f3c68bd3d62a")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("e48a5f2e-fc72-4ce0-8f98-010ee0e6bd6q")
                        .seq(10)
                        .seqDesc("Visit Paris Fashion Week")
                        .tourId("f1f4ac8e-4e24-4bb3-97d3-e5b7b5b7f912")
                        .eventId("e48b9f26-fec7-4ce0-8f98-010ee0e6bd6c")
                        .hotelId("b2c4f9ac-0fd6-4327-8e3a-f3c68bd3d62a")
                        .build(),


                // German Castle Escape
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761950")
                        .seq(1)
                        .seqDesc("Attend Berlin Tech Conference")
                        .tourId("83e6f7ba-0567-426a-b43f-456f947c576b")
                        .eventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761950")
                        .hotelId("6e3b9f27-7cbe-47aa-b6e9-34f2cd5b5171")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761951")
                        .seq(2)
                        .seqDesc("Attend Berlin Tech Conference")
                        .tourId("83e6f7ba-0567-426a-b43f-456f947c576b")
                        .eventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761950")
                        .hotelId("6e3b9f27-7cbe-47aa-b6e9-34f2cd5b5171")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761952")
                        .seq(3)
                        .seqDesc("Attend Berlin Tech Conference")
                        .tourId("83e6f7ba-0567-426a-b43f-456f947c576b")
                        .eventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761950")
                        .hotelId("6e3b9f27-7cbe-47aa-b6e9-34f2cd5b5171")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761953")
                        .seq(4)
                        .seqDesc("Attend Berlin Tech Conference")
                        .tourId("83e6f7ba-0567-426a-b43f-456f947c576b")
                        .eventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761950")
                        .hotelId("6e3b9f27-7cbe-47aa-b6e9-34f2cd5b5171")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761954")
                        .seq(5)
                        .seqDesc("Attend Berlin Tech Conference")
                        .tourId("83e6f7ba-0567-426a-b43f-456f947c576b")
                        .eventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761950")
                        .hotelId("6e3b9f27-7cbe-47aa-b6e9-34f2cd5b5171")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761955")
                        .seq(6)
                        .seqDesc("Attend Berlin Tech Conference")
                        .tourId("83e6f7ba-0567-426a-b43f-456f947c576b")
                        .eventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761950")
                        .hotelId("6e3b9f27-7cbe-47aa-b6e9-34f2cd5b5171")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761956")
                        .seq(7)
                        .seqDesc("Attend Berlin Tech Conference")
                        .tourId("83e6f7ba-0567-426a-b43f-456f947c576b")
                        .eventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761950")
                        .hotelId("6e3b9f27-7cbe-47aa-b6e9-34f2cd5b5171")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761957")
                        .seq(8)
                        .seqDesc("Attend Berlin Tech Conference")
                        .tourId("83e6f7ba-0567-426a-b43f-456f947c576b")
                        .eventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761950")
                        .hotelId("6e3b9f27-7cbe-47aa-b6e9-34f2cd5b5171")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761958")
                        .seq(9)
                        .seqDesc("Attend Berlin Tech Conference")
                        .tourId("83e6f7ba-0567-426a-b43f-456f947c576b")
                        .eventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761950")
                        .hotelId("6e3b9f27-7cbe-47aa-b6e9-34f2cd5b5171")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761959")
                        .seq(10)
                        .seqDesc("Attend Berlin Tech Conference")
                        .tourId("83e6f7ba-0567-426a-b43f-456f947c576b")
                        .eventId("7a92bd77-c6e4-4f27-9bbe-a5dad2761950")
                        .hotelId("6e3b9f27-7cbe-47aa-b6e9-34f2cd5b5171")
                        .build(),

                // Italian Culinary Retreat
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("d12a7bf7-dc94-4953-9189-68f6a70f7844")
                        .seq(1)
                        .seqDesc("Enjoy Rome Food & Wine Festival")
                        .tourId("d12a7bf7-dc94-4953-9189-68f6a70f7844")
                        .eventId("44475ce1-6358-4d2f-a497-4ca3032c495c")
                        .hotelId("9273ecac-b84d-41e9-9d5f-28f0bd1e467b")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("d12a7bf7-dc94-4953-9189-68f6a70f7841")
                        .seq(2)
                        .seqDesc("Enjoy Rome Food & Wine Festival")
                        .tourId("d12a7bf7-dc94-4953-9189-68f6a70f7844")
                        .eventId("44475ce1-6358-4d2f-a497-4ca3032c495c")
                        .hotelId("9273ecac-b84d-41e9-9d5f-28f0bd1e467b")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("d12a7bf7-dc94-4953-9189-68f6a70f7842")
                        .seq(3)
                        .seqDesc("Enjoy Rome Food & Wine Festival")
                        .tourId("d12a7bf7-dc94-4953-9189-68f6a70f7844")
                        .eventId("44475ce1-6358-4d2f-a497-4ca3032c495c")
                        .hotelId("9273ecac-b84d-41e9-9d5f-28f0bd1e467b")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("d12a7bf7-dc94-4953-9189-68f6a70f7843")
                        .seq(4)
                        .seqDesc("Enjoy Rome Food & Wine Festival")
                        .tourId("d12a7bf7-dc94-4953-9189-68f6a70f7844")
                        .eventId("44475ce1-6358-4d2f-a497-4ca3032c495c")
                        .hotelId("9273ecac-b84d-41e9-9d5f-28f0bd1e467b")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("d12a7bf7-dc94-4953-9189-68f6a70f7845")
                        .seq(5)
                        .seqDesc("Enjoy Rome Food & Wine Festival")
                        .tourId("d12a7bf7-dc94-4953-9189-68f6a70f7844")
                        .eventId("44475ce1-6358-4d2f-a497-4ca3032c495c")
                        .hotelId("9273ecac-b84d-41e9-9d5f-28f0bd1e467b")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("d12a7bf7-dc94-4953-9189-68f6a70f7846")
                        .seq(6)
                        .seqDesc("Enjoy Rome Food & Wine Festival")
                        .tourId("d12a7bf7-dc94-4953-9189-68f6a70f7844")
                        .eventId("44475ce1-6358-4d2f-a497-4ca3032c495c")
                        .hotelId("9273ecac-b84d-41e9-9d5f-28f0bd1e467b")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("d12a7bf7-dc94-4953-9189-68f6a70f7847")
                        .seq(7)
                        .seqDesc("Enjoy Rome Food & Wine Festival")
                        .tourId("d12a7bf7-dc94-4953-9189-68f6a70f7844")
                        .eventId("44475ce1-6358-4d2f-a497-4ca3032c495c")
                        .hotelId("9273ecac-b84d-41e9-9d5f-28f0bd1e467b")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("d12a7bf7-dc94-4953-9189-68f6a70f7848")
                        .seq(8)
                        .seqDesc("Enjoy Rome Food & Wine Festival")
                        .tourId("d12a7bf7-dc94-4953-9189-68f6a70f7844")
                        .eventId("44475ce1-6358-4d2f-a497-4ca3032c495c")
                        .hotelId("9273ecac-b84d-41e9-9d5f-28f0bd1e467b")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("d12a7bf7-dc94-4953-9189-68f6a70f7849")
                        .seq(9)
                        .seqDesc("Enjoy Rome Food & Wine Festival")
                        .tourId("d12a7bf7-dc94-4953-9189-68f6a70f7844")
                        .eventId("44475ce1-6358-4d2f-a497-4ca3032c495c")
                        .hotelId("9273ecac-b84d-41e9-9d5f-28f0bd1e467b")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("d12a7bf7-dc94-4953-9189-68f6a70f7867")
                        .seq(10)
                        .seqDesc("Enjoy Rome Food & Wine Festival")
                        .tourId("d12a7bf7-dc94-4953-9189-68f6a70f7844")
                        .eventId("44475ce1-6358-4d2f-a497-4ca3032c495c")
                        .hotelId("9273ecac-b84d-41e9-9d5f-28f0bd1e467b")
                        .build(),

                // Japan Cherry Blossom Tour
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e39")
                        .seq(1)
                        .seqDesc("Join Tokyo Cherry Blossom Festival")
                        .tourId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e39")
                        .eventId("3ea4a674-3463-4fa2-a3cf-d882485963b6")
                        .hotelId("e8f314c7-716b-4f19-a1d6-fc376b8c81ad")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e38")
                        .seq(2)
                        .seqDesc("Join Tokyo Cherry Blossom Festival")
                        .tourId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e39")
                        .eventId("3ea4a674-3463-4fa2-a3cf-d882485963b6")
                        .hotelId("e8f314c7-716b-4f19-a1d6-fc376b8c81ad")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e37")
                        .seq(3)
                        .seqDesc("Join Tokyo Cherry Blossom Festival")
                        .tourId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e39")
                        .eventId("3ea4a674-3463-4fa2-a3cf-d882485963b6")
                        .hotelId("e8f314c7-716b-4f19-a1d6-fc376b8c81ad")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e36")
                        .seq(4)
                        .seqDesc("Join Tokyo Cherry Blossom Festival")
                        .tourId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e39")
                        .eventId("3ea4a674-3463-4fa2-a3cf-d882485963b6")
                        .hotelId("e8f314c7-716b-4f19-a1d6-fc376b8c81ad")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e35")
                        .seq(5)
                        .seqDesc("Join Tokyo Cherry Blossom Festival")
                        .tourId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e39")
                        .eventId("3ea4a674-3463-4fa2-a3cf-d882485963b6")
                        .hotelId("e8f314c7-716b-4f19-a1d6-fc376b8c81ad")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e34")
                        .seq(6)
                        .seqDesc("Join Tokyo Cherry Blossom Festival")
                        .tourId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e39")
                        .eventId("3ea4a674-3463-4fa2-a3cf-d882485963b6")
                        .hotelId("e8f314c7-716b-4f19-a1d6-fc376b8c81ad")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e33")
                        .seq(7)
                        .seqDesc("Join Tokyo Cherry Blossom Festival")
                        .tourId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e39")
                        .eventId("3ea4a674-3463-4fa2-a3cf-d882485963b6")
                        .hotelId("e8f314c7-716b-4f19-a1d6-fc376b8c81ad")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e32")
                        .seq(8)
                        .seqDesc("Join Tokyo Cherry Blossom Festival")
                        .tourId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e39")
                        .eventId("3ea4a674-3463-4fa2-a3cf-d882485963b6")
                        .hotelId("e8f314c7-716b-4f19-a1d6-fc376b8c81ad")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e31")
                        .seq(9)
                        .seqDesc("Join Tokyo Cherry Blossom Festival")
                        .tourId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e39")
                        .eventId("3ea4a674-3463-4fa2-a3cf-d882485963b6")
                        .hotelId("e8f314c7-716b-4f19-a1d6-fc376b8c81ad")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e30")
                        .seq(10)
                        .seqDesc("Join Tokyo Cherry Blossom Festival")
                        .tourId("7e14cb83-3e2a-41c6-b26f-09ebd62a5e39")
                        .eventId("3ea4a674-3463-4fa2-a3cf-d882485963b6")
                        .hotelId("e8f314c7-716b-4f19-a1d6-fc376b8c81ad")
                        .build(),

                // Brazilian Amazon Adventure
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("42cf8561-6f83-4f2f-becb-e3c72a1bb572")
                        .seq(1)
                        .seqDesc("Experience Rio Carnival")
                        .tourId("42cf8561-6f83-4f2f-becb-e3c72a1bb572")
                        .eventId("528398a9-91a1-4833-bdd1-d88b732b9be6")
                        .hotelId("47c8f2e7-3d6b-402c-93b7-8a92ef43e6ab")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("42cf8561-6f83-4f2f-becb-e3c72a1bb570")
                        .seq(2)
                        .seqDesc("Experience Rio Carnival")
                        .tourId("42cf8561-6f83-4f2f-becb-e3c72a1bb572")
                        .eventId("528398a9-91a1-4833-bdd1-d88b732b9be6")
                        .hotelId("47c8f2e7-3d6b-402c-93b7-8a92ef43e6ab")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("42cf8561-6f83-4f2f-becb-e3c72a1bb571")
                        .seq(3)
                        .seqDesc("Experience Rio Carnival")
                        .tourId("42cf8561-6f83-4f2f-becb-e3c72a1bb572")
                        .eventId("528398a9-91a1-4833-bdd1-d88b732b9be6")
                        .hotelId("47c8f2e7-3d6b-402c-93b7-8a92ef43e6ab")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("42cf8561-6f83-4f2f-becb-e3c72a1bb573")
                        .seq(4)
                        .seqDesc("Experience Rio Carnival")
                        .tourId("42cf8561-6f83-4f2f-becb-e3c72a1bb572")
                        .eventId("528398a9-91a1-4833-bdd1-d88b732b9be6")
                        .hotelId("47c8f2e7-3d6b-402c-93b7-8a92ef43e6ab")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("42cf8561-6f83-4f2f-becb-e3c72a1bb574")
                        .seq(5)
                        .seqDesc("Experience Rio Carnival")
                        .tourId("42cf8561-6f83-4f2f-becb-e3c72a1bb572")
                        .eventId("528398a9-91a1-4833-bdd1-d88b732b9be6")
                        .hotelId("47c8f2e7-3d6b-402c-93b7-8a92ef43e6ab")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("42cf8561-6f83-4f2f-becb-e3c72a1bb575")
                        .seq(6)
                        .seqDesc("Experience Rio Carnival")
                        .tourId("42cf8561-6f83-4f2f-becb-e3c72a1bb572")
                        .eventId("528398a9-91a1-4833-bdd1-d88b732b9be6")
                        .hotelId("47c8f2e7-3d6b-402c-93b7-8a92ef43e6ab")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("42cf8561-6f83-4f2f-becb-e3c72a1bb576")
                        .seq(7)
                        .seqDesc("Experience Rio Carnival")
                        .tourId("42cf8561-6f83-4f2f-becb-e3c72a1bb572")
                        .eventId("528398a9-91a1-4833-bdd1-d88b732b9be6")
                        .hotelId("47c8f2e7-3d6b-402c-93b7-8a92ef43e6ab")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("42cf8561-6f83-4f2f-becb-e3c72a1bb577")
                        .seq(8)
                        .seqDesc("Experience Rio Carnival")
                        .tourId("42cf8561-6f83-4f2f-becb-e3c72a1bb572")
                        .eventId("528398a9-91a1-4833-bdd1-d88b732b9be6")
                        .hotelId("47c8f2e7-3d6b-402c-93b7-8a92ef43e6ab")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("42cf8561-6f83-4f2f-becb-e3c72a1bb578")
                        .seq(9)
                        .seqDesc("Experience Rio Carnival")
                        .tourId("42cf8561-6f83-4f2f-becb-e3c72a1bb572")
                        .eventId("528398a9-91a1-4833-bdd1-d88b732b9be6")
                        .hotelId("47c8f2e7-3d6b-402c-93b7-8a92ef43e6ab")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("42cf8561-6f83-4f2f-becb-e3c72a1bb579")
                        .seq(10)
                        .seqDesc("Experience Rio Carnival")
                        .tourId("42cf8561-6f83-4f2f-becb-e3c72a1bb572")
                        .eventId("528398a9-91a1-4833-bdd1-d88b732b9be6")
                        .hotelId("47c8f2e7-3d6b-402c-93b7-8a92ef43e6ab")
                        .build(),


                // Australian Outback Expedition
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec5")
                        .seq(1)
                        .seqDesc("Enjoy Sydney Fireworks")
                        .tourId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec5")
                        .eventId("72927c9c-750f-4b37-9799-16d3b7a59725")
                        .hotelId("aa12fc4b-619e-4d8e-8563-5a09bc6f1ae1")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec0")
                        .seq(2)
                        .seqDesc("Enjoy Sydney Fireworks")
                        .tourId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec5")
                        .eventId("72927c9c-750f-4b37-9799-16d3b7a59725")
                        .hotelId("aa12fc4b-619e-4d8e-8563-5a09bc6f1ae1")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec1")
                        .seq(3)
                        .seqDesc("Enjoy Sydney Fireworks")
                        .tourId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec5")
                        .eventId("72927c9c-750f-4b37-9799-16d3b7a59725")
                        .hotelId("aa12fc4b-619e-4d8e-8563-5a09bc6f1ae1")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec2")
                        .seq(4)
                        .seqDesc("Enjoy Sydney Fireworks")
                        .tourId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec5")
                        .eventId("72927c9c-750f-4b37-9799-16d3b7a59725")
                        .hotelId("aa12fc4b-619e-4d8e-8563-5a09bc6f1ae1")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec3")
                        .seq(5)
                        .seqDesc("Enjoy Sydney Fireworks")
                        .tourId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec5")
                        .eventId("72927c9c-750f-4b37-9799-16d3b7a59725")
                        .hotelId("aa12fc4b-619e-4d8e-8563-5a09bc6f1ae1")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec4")
                        .seq(6)
                        .seqDesc("Enjoy Sydney Fireworks")
                        .tourId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec5")
                        .eventId("72927c9c-750f-4b37-9799-16d3b7a59725")
                        .hotelId("aa12fc4b-619e-4d8e-8563-5a09bc6f1ae1")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec9")
                        .seq(7)
                        .seqDesc("Enjoy Sydney Fireworks")
                        .tourId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec5")
                        .eventId("72927c9c-750f-4b37-9799-16d3b7a59725")
                        .hotelId("aa12fc4b-619e-4d8e-8563-5a09bc6f1ae1")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec6")
                        .seq(8)
                        .seqDesc("Enjoy Sydney Fireworks")
                        .tourId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec5")
                        .eventId("72927c9c-750f-4b37-9799-16d3b7a59725")
                        .hotelId("aa12fc4b-619e-4d8e-8563-5a09bc6f1ae1")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec7")
                        .seq(9)
                        .seqDesc("Enjoy Sydney Fireworks")
                        .tourId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec5")
                        .eventId("72927c9c-750f-4b37-9799-16d3b7a59725")
                        .hotelId("aa12fc4b-619e-4d8e-8563-5a09bc6f1ae1")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec8")
                        .seq(10)
                        .seqDesc("Enjoy Sydney Fireworks")
                        .tourId("f4a9c2ef-8473-4b67-b527-44fcb3a6eec5")
                        .eventId("72927c9c-750f-4b37-9799-16d3b7a59725")
                        .hotelId("aa12fc4b-619e-4d8e-8563-5a09bc6f1ae1")
                        .build(),

                // Indian Heritage Journey
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                        .seq(1)
                        .seqDesc("Attend Mumbai Film Festival")
                        .tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                        .eventId("82c07322-37da-4635-bcae-3ae241036cf8")
                        .hotelId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1a")
                        .seq(2)
                        .seqDesc("Attend Mumbai Film Festival")
                        .tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                        .eventId("82c07322-37da-4635-bcae-3ae241036cf8")
                        .hotelId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1b")
                        .seq(3)
                        .seqDesc("Attend Mumbai Film Festival")
                        .tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                        .eventId("82c07322-37da-4635-bcae-3ae241036cf8")
                        .hotelId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1v")
                        .seq(4)
                        .seqDesc("Attend Mumbai Film Festival")
                        .tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                        .eventId("82c07322-37da-4635-bcae-3ae241036cf8")
                        .hotelId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1c")
                        .seq(5)
                        .seqDesc("Attend Mumbai Film Festival")
                        .tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                        .eventId("82c07322-37da-4635-bcae-3ae241036cf8")
                        .hotelId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1z")
                        .seq(6)
                        .seqDesc("Attend Mumbai Film Festival")
                        .tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                        .eventId("82c07322-37da-4635-bcae-3ae241036cf8")
                        .hotelId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1p")
                        .seq(7)
                        .seqDesc("Attend Mumbai Film Festival")
                        .tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                        .eventId("82c07322-37da-4635-bcae-3ae241036cf8")
                        .hotelId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1o")
                        .seq(8)
                        .seqDesc("Attend Mumbai Film Festival")
                        .tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                        .eventId("82c07322-37da-4635-bcae-3ae241036cf8")
                        .hotelId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1i")
                        .seq(9)
                        .seqDesc("Attend Mumbai Film Festival")
                        .tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                        .eventId("82c07322-37da-4635-bcae-3ae241036cf8")
                        .hotelId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1u")
                        .seq(10)
                        .seqDesc("Attend Mumbai Film Festival")
                        .tourId("7b82cb14-2ff5-4d8f-b84c-3bfb7b6cda1e")
                        .eventId("82c07322-37da-4635-bcae-3ae241036cf8")
                        .hotelId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                        .build(),


                // Chinese Silk Road Adventure
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("6a237fda-4924-4c73-a6df-73c1e0c37af1")
                        .seq(1)
                        .seqDesc("Experience Beijing Dragon Boat Festival")
                        .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                        .eventId("4e5bf569-27cb-494b-b7e5-9f9bf0ca66fe")
                        .hotelId("3f2e8bbd-84c3-4d3e-bc24-f173acd01be4")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                        .seq(2)
                        .seqDesc("Experience Beijing Dragon Boat Festival")
                        .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                        .eventId("4e5bf569-27cb-494b-b7e5-9f9bf0ca66fe")
                        .hotelId("3f2e8bbd-84c3-4d3e-bc24-f173acd01be4")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("6a237fda-4924-4c73-a6df-73c1e0c37af0")
                        .seq(3)
                        .seqDesc("Experience Beijing Dragon Boat Festival")
                        .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                        .eventId("4e5bf569-27cb-494b-b7e5-9f9bf0ca66fe")
                        .hotelId("3f2e8bbd-84c3-4d3e-bc24-f173acd01be4")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("6a237fda-4924-4c73-a6df-73c1e0c37af3")
                        .seq(4)
                        .seqDesc("Experience Beijing Dragon Boat Festival")
                        .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                        .eventId("4e5bf569-27cb-494b-b7e5-9f9bf0ca66fe")
                        .hotelId("3f2e8bbd-84c3-4d3e-bc24-f173acd01be4")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("6a237fda-4924-4c73-a6df-73c1e0c37af4")
                        .seq(5)
                        .seqDesc("Experience Beijing Dragon Boat Festival")
                        .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                        .eventId("4e5bf569-27cb-494b-b7e5-9f9bf0ca66fe")
                        .hotelId("3f2e8bbd-84c3-4d3e-bc24-f173acd01be4")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("6a237fda-4924-4c73-a6df-73c1e0c37af5")
                        .seq(6)
                        .seqDesc("Experience Beijing Dragon Boat Festival")
                        .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                        .eventId("4e5bf569-27cb-494b-b7e5-9f9bf0ca66fe")
                        .hotelId("3f2e8bbd-84c3-4d3e-bc24-f173acd01be4")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("6a237fda-4924-4c73-a6df-73c1e0c37af6")
                        .seq(7)
                        .seqDesc("Experience Beijing Dragon Boat Festival")
                        .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                        .eventId("4e5bf569-27cb-494b-b7e5-9f9bf0ca66fe")
                        .hotelId("3f2e8bbd-84c3-4d3e-bc24-f173acd01be4")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("6a237fda-4924-4c73-a6df-73c1e0c37af7")
                        .seq(8)
                        .seqDesc("Experience Beijing Dragon Boat Festival")
                        .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                        .eventId("4e5bf569-27cb-494b-b7e5-9f9bf0ca66fe")
                        .hotelId("3f2e8bbd-84c3-4d3e-bc24-f173acd01be4")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("6a237fda-4924-4c73-a6df-73c1e0c37af8")
                        .seq(9)
                        .seqDesc("Experience Beijing Dragon Boat Festival")
                        .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                        .eventId("4e5bf569-27cb-494b-b7e5-9f9bf0ca66fe")
                        .hotelId("3f2e8bbd-84c3-4d3e-bc24-f173acd01be4")
                        .build(),
                TourEvent.builder()
                        .Id(null)
                        .tourEventId("6a237fda-4924-4c73-a6df-73c1e0c37af9")
                        .seq(10)
                        .seqDesc("Experience Beijing Dragon Boat Festival")
                        .tourId("6a237fda-4924-4c73-a6df-73c1e0c37af2")
                        .eventId("4e5bf569-27cb-494b-b7e5-9f9bf0ca66fe")
                        .hotelId("3f2e8bbd-84c3-4d3e-bc24-f173acd01be4")
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

    private final AirportRepository airportRepository;
    @PostConstruct
    public void loadAirports() {
        List<Airport> sampleAirports = List.of(
                Airport.builder()
                        .id(null)
                        .airportId("d1e91f6c-723e-43b9-812f-2f3d3bfb4081")
                        .name("John F. Kennedy International Airport")
                        .cityId("b713c09a-9c3e-4b30-872a-4d89089badd0")
                        .build(),
                Airport.builder()
                        .id(null)
                        .airportId("f7a7e8e4-cc49-4027-8c4d-881e6179e6d2")
                        .name("Toronto Pearson International Airport")
                        .cityId("11a85d86-3fc8-4504-b1e9-25fd87eba3cf")
                        .build(),
                Airport.builder()
                        .id(null)
                        .airportId("b2c4f9ac-0fd6-4327-8e3a-f3c68bd3d62a")
                        .name("Charles de Gaulle Airport")
                        .cityId("f5ad630f-830d-47a8-a26c-76b87163a7e4")
                        .build(),
                Airport.builder()
                        .id(null)
                        .airportId("6e3b9f27-7cbe-47aa-b6e9-34f2cd5b5171")
                        .name("Berlin Brandenburg Airport")
                        .cityId("000f3f3a-8ee2-4690-be4d-a5bd38a5f06f")
                        .build(),
                Airport.builder()
                        .id(null)
                        .airportId("9273ecac-b84d-41e9-9d5f-28f0bd1e467b")
                        .name("Leonardo da Vinci–Fiumicino Airport")
                        .cityId("7f15fafc-85f4-4ba5-822b-27b7ddce6c37")
                        .build(),
                Airport.builder()
                        .id(null)
                        .airportId("e8f314c7-716b-4f19-a1d6-fc376b8c81ad")
                        .name("Narita International Airport")
                        .cityId("92537e75-3fc2-42af-b105-b6150395acbb")
                        .build(),
                Airport.builder()
                        .id(null)
                        .airportId("3f2e8bbd-84c3-4d3e-bc24-f173acd01be4")
                        .name("Rio de Janeiro–Galeão International Airport")
                        .cityId("affc7cc1-b3d8-4146-bc39-6e9ff1e66704")
                        .build(),
                Airport.builder()
                        .id(null)
                        .airportId("aa12fc4b-619e-4d8e-8563-5a09bc6f1ae1")
                        .name("Sydney Kingsford Smith Airport")
                        .cityId("f978f76c-abfc-4b25-ba32-9e1b085b5ab0")
                        .build(),
                Airport.builder()
                        .id(null)
                        .airportId("47c8f2e7-3d6b-402c-93b7-8a92ef43e6ab")
                        .name("Chhatrapati Shivaji Maharaj International Airport")
                        .cityId("0361c975-fe28-4817-be51-6864b8a2bf38")
                        .build(),
                Airport.builder()
                        .id(null)
                        .airportId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                        .name("Beijing Capital International Airport")
                        .cityId("2fce64d8-dfa6-4abe-9a84-af7aaac1293f")
                        .build()
        );

        airportRepository.deleteAll()
                .thenMany(Flux.fromIterable(sampleAirports))
                .flatMap(airportRepository::save)
                .doOnNext(airport -> log.info("Preloaded airport: {}"))
                .subscribe(
                        success -> log.info("Airports preloaded successfully."),
                        error -> log.error("Error preloading airports: {}", error.getMessage())
                );
    }

    // Hotels
    private final HotelRepository hotelRepository;

    @PostConstruct
    public void loadHotels() {
        List<Hotel> sampleHotels = List.of(
                Hotel.builder()
                        .id(null)
                        .hotelId("d1e91f6c-723e-43b9-812f-2f3d3bfb4081")
                        .name("The Plaza Hotel")
                        .cityId("b713c09a-9c3e-4b30-872a-4d89089badd0")
                        .url("https://www.theplazany.com/")
                        .build(),
                Hotel.builder()
                        .id(null)
                        .hotelId("f7a7e8e4-cc49-4027-8c4d-881e6179e6d2")
                        .name("The Ritz-Carlton, Toronto")
                        .cityId("11a85d86-3fc8-4504-b1e9-25fd87eba3cf")
                        .url("https://www.ritzcarlton.com/en/hotels/canada/toronto")
                        .build(),
                Hotel.builder()
                        .id(null)
                        .hotelId("b2c4f9ac-0fd6-4327-8e3a-f3c68bd3d62a")
                        .name("Hôtel Barrière Le Fouquet's")
                        .cityId("f5ad630f-830d-47a8-a26c-76b87163a7e4")
                        .url("https://www.hotelsbarriere.com/en/paris/le-fouquets.html")
                        .build(),
                Hotel.builder()
                        .id(null)
                        .hotelId("6e3b9f27-7cbe-47aa-b6e9-34f2cd5b5171")
                        .name("Hotel Adlon Kempinski")
                        .cityId("000f3f3a-8ee2-4690-be4d-a5bd38a5f06f")
                        .url("https://www.kempinski.com/en/berlin/hotel-adlon/")
                        .build(),
                Hotel.builder()
                        .id(null)
                        .hotelId("9273ecac-b84d-41e9-9d5f-28f0bd1e467b")
                        .name("Hotel Hassler Roma")
                        .cityId("7f15fafc-85f4-4ba5-822b-27b7ddce6c37")
                        .url("https://www.hotelhasslerroma.com/")
                        .build(),
                Hotel.builder()
                        .id(null)
                        .hotelId("e8f314c7-716b-4f19-a1d6-fc376b8c81ad")
                        .name("Hotel Nikko Narita")
                        .cityId("92537e75-3fc2-42af-b105-b6150395acbb")
                        .url("https://www.nikkonarita.com/")
                        .build(),
                Hotel.builder()
                        .id(null)
                        .hotelId("3f2e8bbd-84c3-4d3e-bc24-f173acd01be4")
                        .name("Belmond Copacabana Palace")
                        .cityId("affc7cc1-b3d8-4146-bc39-6e9ff1e66704")
                        .url("https://www.belmond.com/hotels/south-america/brazil/rio-de-janeiro/belmond-copacabana-palace/")
                        .build(),
                Hotel.builder()
                        .id(null)
                        .hotelId("aa12fc4b-619e-4d8e-8563-5a09bc6f1ae1")
                        .name("Shangri-La Hotel, Sydney")
                        .cityId("f978f76c-abfc-4b25-ba32-9e1b085b5ab0")
                        .url("https://www.shangri-la.com/sydney/shangrila/")
                        .build(),
                Hotel.builder()
                        .id(null)
                        .hotelId("47c8f2e7-3d6b-402c-93b7-8a92ef43e6ab")
                        .name("The Leela Mumbai")
                        .cityId("0361c975-fe28-4817-be51-6864b8a2bf38")
                        .url("https://www.theleela.com/en_us/hotels-in-mumbai/the-leela-mumbai-hotel/")
                        .build(),
                Hotel.builder()
                        .id(null)
                        .hotelId("ea1f7a4e-2db7-4812-9e8f-dc4b5a1e7634")
                        .name("Waldorf Astoria Beijing")
                        .cityId("2fce64d8-dfa6-4abe-9a84-af7aaac1293f")
                        .url("https://waldorfastoria3.hilton.com/en/hotels/china/waldorf-astoria-beijing-BJSWAWA/index.html")
                        .build()
        );

        hotelRepository.deleteAll()
                .thenMany(Flux.fromIterable(sampleHotels))
                .flatMap(hotelRepository::save)
                .doOnNext(hotel -> log.info("Preloaded hotel: {}", hotel))
                .subscribe(
                        success -> log.info("Hotels preloaded successfully."),
                        error -> log.error("Error preloading hotels: {}", error.getMessage())
                );
    }

    private final TravelerRepository travelerRepository;


    @PostConstruct
    public void loadTravelers() {
        List<Traveler> sampleTravelers = List.of(
                Traveler.builder()
                        .id(null)
                        .travelerId("c69fa655-d480-48e4-8b66-0d54c1b2b46d")
                        .firstName("amelia")
                        .lastName("clark")
                        .email("ameliaclark@email.com")
                        .addressLine1("123 Main St")
                        .addressLine2("321 Main st")
                        .city("New York")
                        .state("NY")
                        .countryId("ad633b50-83d4-41f3-866a-26452bdd6f33")
                        .seq(1)
                        .build(),
                Traveler.builder()
                        .id(null)
                        .travelerId("d1e91f6c-723e-43b9-812f-2f3d3bfb4081")
                        .firstName("Adem")
                        .lastName("Bessam")
                        .email("adembessam@gmail.com")
                        .addressLine1("123 Main St")
                        .addressLine2("321 Main st")
                        .city("New York")
                        .state("NY")
                        .countryId("ad633b50-83d4-41f3-866a-26452bdd6f33")
                        .seq(1)
                        .build(),
                Traveler.builder()
                        .id(null)
                        .travelerId("b2e91f6c-723e-43b9-812f-2f3d3bfb4082")
                        .firstName("John")
                        .lastName("Doe")
                        .email("johndoe@gmail.com")
                        .addressLine1("456 Elm St")
                        .addressLine2("654 Elm St")
                        .city("Toronto")
                        .state("ON")
                        .countryId("b1db23af-4f2d-4138-b623-e906f5287e90")
                        .seq(2)
                        .build(),
                Traveler.builder()
                        .id(null)
                        .travelerId("c3e91f6c-723e-43b9-812f-2f3d3bfb4083")
                        .firstName("Jane")
                        .lastName("Smith")
                        .email("janesmith@gmail.com")
                        .addressLine1("789 Oak St")
                        .addressLine2("987 Oak St")
                        .city("Paris")
                        .state("IDF")
                        .countryId("118495e2-e1ce-4987-bb09-a95f93515bda")
                        .seq(3)
                        .build(),
                Traveler.builder()
                        .id(null)
                        .travelerId("d4e91f6c-723e-43b9-812f-2f3d3bfb4084")
                        .firstName("Hans")
                        .lastName("Müller")
                        .email("hansmuller@gmail.com")
                        .addressLine1("101 Pine St")
                        .addressLine2("201 Pine St")
                        .city("Berlin")
                        .state("BE")
                        .countryId("cf09374e-6d51-47ec-91e1-2b69a788457f")
                        .seq(4)
                        .build(),
                Traveler.builder()
                        .id(null)
                        .travelerId("e5e91f6c-723e-43b9-812f-2f3d3bfb4085")
                        .firstName("Maria")
                        .lastName("Rossi")
                        .email("mariarossi@gmail.com")
                        .addressLine1("202 Maple St")
                        .addressLine2("303 Maple St")
                        .city("Rome")
                        .state("RM")
                        .countryId("dde33653-fc58-457c-9d33-a322a2a82835")
                        .seq(5)
                        .build(),
                Traveler.builder()
                        .id(null)
                        .travelerId("f6e91f6c-723e-43b9-812f-2f3d3bfb4086")
                        .firstName("Yuki")
                        .lastName("Tanaka")
                        .email("yukitanaka@gmail.com")
                        .addressLine1("303 Cedar St")
                        .addressLine2("404 Cedar St")
                        .city("Tokyo")
                        .state("TK")
                        .countryId("f05f8f2c-f86d-4cf5-8a9b-19e70f1b96d5")
                        .seq(6)
                        .build(),
                Traveler.builder()
                        .id(null)
                        .travelerId("g7e91f6c-723e-43b9-812f-2f3d3bfb4087")
                        .firstName("Carlos")
                        .lastName("Silva")
                        .email("carlossilva@gmail.com")
                        .addressLine1("404 Birch St")
                        .addressLine2("505 Birch St")
                        .city("Rio de Janeiro")
                        .state("RJ")
                        .countryId("b511c7ee-c0f3-4902-9bfa-d50f59ef2b3c")
                        .seq(7)
                        .build(),
                Traveler.builder()
                        .id(null)
                        .travelerId("b3904b84-9cd3-46ca-bcf3-f2206411a842")
                        .firstName("Liam")
                        .lastName("Jones")
                        .email("liambrown@gmail.com")
                        .addressLine1("505 Spruce St")
                        .addressLine2("606 Spruce St")
                        .city("Sydney")
                        .state("NSW")
                        .countryId("d5d4e5e2-f960-4cf4-beed-c38104e5d5b4")
                        .seq(8)
                        .build(),
                Traveler.builder()
                        .id(null)
                        .travelerId("i9e91f6c-723e-43b9-812f-2f3d3bfb4089")
                        .firstName("Amit")
                        .lastName("Sharma")
                        .email("amitsharma@gmail.com")
                        .addressLine1("606 Willow St")
                        .addressLine2("707 Willow St")
                        .city("Mumbai")
                        .state("MH")
                        .countryId("3cd2ad86-26cc-42ad-8b20-8b0b6e6d2a2e")
                        .seq(9)
                        .build(),
                Traveler.builder()
                        .id(null)
                        .travelerId("j0e91f6c-723e-43b9-812f-2f3d3bfb4090")
                        .firstName("Wei")
                        .lastName("Li")
                        .email("weili@gmail.com")
                        .addressLine1("707 Ash St")
                        .addressLine2("808 Ash St")
                        .city("Beijing")
                        .state("BJ")
                        .countryId("877ec1c0-ffab-449e-a2ec-08f95db58f55")
                        .seq(10)
                        .build()
        );

        travelerRepository.deleteAll()
                .thenMany(Flux.fromIterable(sampleTravelers))
                .flatMap(travelerRepository::save)
                .doOnNext(traveler -> log.info("Preloaded traveler: {}"))
                .subscribe(
                        success -> log.info("Travelers preloaded successfully."),
                        error -> log.error("Error preloading travelers: {}", error.getMessage())
                );
    }

    // now for packages

    private final PackageRepository packageRepository;

    @PostConstruct
    public void loadPackages() {
        List<com.traveltrove.betraveltrove.dataaccess.tourpackage.Package> samplePackages = List.of(
                com.traveltrove.betraveltrove.dataaccess.tourpackage.Package.builder()
                        .id(null)
                        .packageId("4f3a6bde-bc68-4b1e-835a-1e5aaf7b752d")
                        .name("New York Adventure Package")
                        .description("Experience the thrill of New York City with a complete package including flights and guided tours.")
                        .startDate(LocalDate.of(2026, 5, 15))
                        .endDate(LocalDate.of(2027, 5, 22))
                        .airportId("d1e91f6c-723e-43b9-812f-2f3d3bfb4081")
                        .tourId("7f54a45d-8c1d-432f-a5c8-1f93b89bfe29")
                        .priceSingle(1800.0)
                        .priceDouble(1600.0)
                        .priceTriple(1400.0)
                        .totalSeats(130)
                        .availableSeats(50)
                        .status(PackageStatus.BOOKING_OPEN)
                        .build(),


                com.traveltrove.betraveltrove.dataaccess.tourpackage.Package.builder()
                        .id(null)
                        .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .name("Toronto Winter Lights Escape")
                        .description("Explore the magic of Toronto’s Winter Lights Festival with this all-inclusive package.")
                        .startDate(LocalDate.of(2024, 1, 10))
                        .endDate(LocalDate.of(2024, 1, 17))
                        .airportId("f7a7e8e4-cc49-4027-8c4d-881e6179e6d2")
                        .tourId("90af5e42-b8a7-4b59-939f-8fa2c4e384d1")
                        .priceSingle(1500.0)
                        .priceDouble(1300.0)
                        .priceTriple(1100.0)
                        .totalSeats(125)
                        .availableSeats(10)
                        .status(PackageStatus.BOOKING_OPEN)
                        .build(),

                com.traveltrove.betraveltrove.dataaccess.tourpackage.Package.builder()
                        .id(null)
                        .packageId("2d1b4a3f-4e7f-42a7-a6fb-b4a1e7d9cf14")
                        .name("Parisian Fashion Week Package")
                        .description("Indulge in Paris Fashion Week with flights, accommodations, and VIP access.")
                        .startDate(LocalDate.of(2024, 3, 20))
                        .endDate(LocalDate.of(2024, 3, 27))
                        .airportId("b2c4f9ac-0fd6-4327-8e3a-f3c68bd3d62a")
                        .tourId("f1f4ac8e-4e24-4bb3-97d3-e5b7b5b7f912")
                        .priceSingle(2200.0)
                        .priceDouble(2000.0)
                        .priceTriple(1800.0)
                        .totalSeats(120)
                        .status(PackageStatus.UPCOMING)
                        .availableSeats(0)
                        .build(),

                com.traveltrove.betraveltrove.dataaccess.tourpackage.Package.builder()
                        .id(null)
                        .packageId("5c3b6d4e-9f42-4d1e-8ba4-df23cb19d8f4")
                        .name("Berlin Tech Conference Package")
                        .description("Join Europe’s largest tech conference with this exclusive package.")
                        .startDate(LocalDate.of(2024, 6, 5))
                        .endDate(LocalDate.of(2024, 6, 12))
                        .airportId("6e3b9f27-7cbe-47aa-b6e9-34f2cd5b5171")
                        .tourId("83e6f7ba-0567-426a-b43f-456f947c576b")
                        .priceSingle(1800.0)
                        .priceDouble(1600.0)
                        .priceTriple(1400.0)
                        .totalSeats(135)
                        .availableSeats(0)
                        .status(PackageStatus.UPCOMING)
                        .build(),

                com.traveltrove.betraveltrove.dataaccess.tourpackage.Package.builder()
                        .id(null)
                        .packageId("3e1a7d9b-5c4f-4a3b-a7f6-cbd4f8e2af16")
                        .name("Rome Culinary Experience")
                        .description("Savor authentic Italian cuisine and wines in this culinary retreat.")
                        .startDate(LocalDate.of(2024, 9, 15))
                        .endDate(LocalDate.of(2024, 9, 22))
                        .airportId("9273ecac-b84d-41e9-9d5f-28f0bd1e467b")
                        .tourId("d12a7bf7-dc94-4953-9189-68f6a70f7844")
                        .priceSingle(1900.0)
                        .priceDouble(1700.0)
                        .priceTriple(1500.0)
                        .totalSeats(130)
                        .availableSeats(100)
                        .status(PackageStatus.UPCOMING)
                        .build()
        );

        packageRepository.deleteAll()
                .thenMany(Flux.fromIterable(samplePackages))
                .flatMap(packageRepository::save)
                .doOnNext(pkg -> log.info("Preloaded package: {}"))
                .subscribe(
                        success -> log.info("Packages preloaded successfully."),
                        error -> log.error("Error preloading packages: {}", error.getMessage())
                );

    }

    private final UserRepository userRepository;

    @PostConstruct
    public void loadUsers() {
        List<User> sampleUsers = List.of(
                // Admins
                User.builder()
                        .id(null)
                        .userId("auth0|675f4a7b9a80612ce548e063")
                        .email("amelia.clark@traveltrove.com")
                        .firstName("Amelia")
                        .lastName("Clark")
                        .roles(List.of("Admin"))
                        .permissions(null)
                        .travelerId("c69fa655-d480-48e4-8b66-0d54c1b2b46d")
                        .travelerIds(List.of("c69fa655-d480-48e4-8b66-0d54c1b2b46d"))
                        .build(),
                User.builder()
                        .id(null)
                        .userId("auth0|675f4aa9e184fd643a8ed8fe")
                        .email("liam.jones@traveltrove.com")
                        .firstName("Liam")
                        .lastName("Jones")
                        .roles(List.of("Admin"))
                        .permissions(null)
                        .travelerId("b3904b84-9cd3-46ca-bcf3-f2206411a842")
                        .travelerIds(List.of("b3904b84-9cd3-46ca-bcf3-f2206411a842"))
                        .build(),

                // Employees
                User.builder()
                        .id(null)
                        .userId("auth0|675f4af1e184fd643a8ed900")
                        .email("oliver.smith@traveltrove.com")
                        .firstName("Oliver")
                        .lastName("Smith")
                        .roles(List.of("Employee"))
                        .permissions(null)
                        .travelerId("9e96ca09-1048-4e7e-b246-51ddfd556e14")
                        .travelerIds(List.of("9e96ca09-1048-4e7e-b246-51ddfd556e14"))
                        .build(),
                User.builder()
                        .id(null)
                        .userId("auth0|675f4b169a80612ce548e066")
                        .email("emma.brown@traveltrove.com")
                        .firstName("Emma")
                        .lastName("Brown")
                        .roles(List.of("Employee"))
                        .permissions(null)
                        .travelerId("2c2cfa3d-de07-42e8-930a-4e63ad39a2b1")
                        .travelerIds(List.of("2c2cfa3d-de07-42e8-930a-4e63ad39a2b1"))
                        .build(),
                User.builder()
                        .id(null)
                        .userId("auth0|675f6ad19a80612ce548e0a5")
                        .email("william.taylor@traveltrove.com")
                        .firstName("William")
                        .lastName("Taylor")
                        .roles(List.of("Employee"))
                        .permissions(null)
                        .travelerId("2a75f97e-f77b-468c-83cd-c9340283a125")
                        .travelerIds(List.of("2a75f97e-f77b-468c-83cd-c9340283a125"))
                        .build(),

                // Customers
                User.builder()
                        .id(null)
                        .userId("auth0|675f4b3c9a80612ce548e067")
                        .email("sophia.johnson@example.com")
                        .firstName("Sophia")
                        .lastName("Johnson")
                        .roles(List.of("Customer"))
                        .permissions(null)
                        .travelerId("ec1c67c8-af60-4956-a84a-fa569897a065")
                        .travelerIds(List.of("ec1c67c8-af60-4956-a84a-fa569897a065"))
                        .build(),
                User.builder()
                        .id(null)
                        .userId("auth0|675f4b619a80612ce548e068")
                        .email("james.williams@example.com")
                        .firstName("James")
                        .lastName("Williams")
                        .roles(List.of("Customer"))
                        .permissions(null)
                        .travelerId("7491755b-1904-4432-96f6-d7c14d3d1532")
                        .travelerIds(List.of("7491755b-1904-4432-96f6-d7c14d3d1532"))
                        .build(),
                User.builder()
                        .id(null)
                        .userId("auth0|675f4b7ae184fd643a8ed902")
                        .email("ava.davis@example.com")
                        .firstName("Ava")
                        .lastName("Davis")
                        .roles(List.of("Customer"))
                        .permissions(null)
                        .travelerId("fd96c9f3-1429-4a32-b8f1-033fdd4aa0eb")
                        .travelerIds(List.of("fd96c9f3-1429-4a32-b8f1-033fdd4aa0eb"))
                        .build(),
                User.builder()
                        .id(null)
                        .userId("auth0|675f4b9d9a80612ce548e069")
                        .email("mason.miller@example.com")
                        .firstName("Mason")
                        .lastName("Miller")
                        .roles(List.of("Customer"))
                        .permissions(null)
                        .travelerId("565ac69b-5022-42d7-89f4-e031f67da710")
                        .travelerIds(List.of("565ac69b-5022-42d7-89f4-e031f67da710"))
                        .build(),
                User.builder()
                        .id(null)
                        .userId("auth0|675f4bb4e184fd643a8ed903")
                        .email("mia.moore@example.com")
                        .firstName("Mia")
                        .lastName("Moore")
                        .roles(List.of("Customer"))
                        .permissions(null)
                        .travelerId("4a9adfcc-cfc9-4c25-908b-031bdeb2ab31")
                        .travelerIds(List.of("4a9adfcc-cfc9-4c25-908b-031bdeb2ab31"))
                        .build()
        );

        userRepository.deleteAll()
                .thenMany(Flux.fromIterable(sampleUsers))
                .flatMap(userRepository::save)
                .doOnNext(user -> log.info("Preloaded user: {}", user))
                .subscribe(
                        success -> log.info("Users preloaded successfully."),
                        error -> log.error("Error preloading users: {}", error.getMessage())
                );
    }

    private final BookingRepository bookingRepository;

    @PostConstruct
    public void loadBookings() {
        List<Booking> sampleBookings = List.of(
        // user : auth0|675f4b3c9a80612ce548e067
            Booking.builder()
                    .bookingId("2a4fff75-5ad6-4ea7-b3ca-b7eca3cdda29")
                    .userId("auth0|675f4b3c9a80612ce548e067")
                    .packageId("4f3a6bde-bc68-4b1e-835a-1e5aaf7b752d")
                    .status(BookingStatus.PAYMENT_PENDING)
                    .bookingDate(LocalDate.now().minusMonths(1))
                    .totalPrice(1600.0)
                    .build(),

            Booking.builder()
                    .bookingId("2a4fff75-5ad6-4ea7-b3ca-b7eca343da29")
                    .userId("auth0|675f4b3c9a80612ce548e067")
                    .packageId("4f3a6bde-bc68-4b1e-835a-1e5aaf7b752d")
                    .status(BookingStatus.BOOKING_CONFIRMED)
                    .bookingDate(LocalDate.now().minusMonths(1))
                    .totalPrice(1600.0)
                    .build(),

            Booking.builder()
                    .bookingId("2a4fff75-5326-4ea7-b3ca-b7eca343da29")
                    .userId("auth0|675f4b3c9a80612ce548e067")
                    .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                    .status(BookingStatus.BOOKING_CONFIRMED)
                    .bookingDate(LocalDate.now().minusMonths(1))
                    .totalPrice(1600.0)
                    .build(),

            Booking.builder()
                    .bookingId("124fff75-5326-4ea7-b3ca-b7eca343da29")
                    .userId("auth0|675f4b3c9a80612ce548e067")
                    .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                    .status(BookingStatus.BOOKING_CONFIRMED)
                    .bookingDate(LocalDate.now().minusMonths(1))
                    .totalPrice(1600.0)
                    .build(),

            Booking.builder()
                    .bookingId("sb4fff75-5326-4ea7-b3ca-b7eca343da29")
                    .userId("auth0|675f4b3c9a80612ce548e067")
                    .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                    .status(BookingStatus.BOOKING_CONFIRMED)
                    .bookingDate(LocalDate.now().minusMonths(1))
                    .totalPrice(1600.0)
                    .build(),

            Booking.builder()
                    .bookingId("1gdu4f75-5326-4ea7-b3ca-b7eca343da29")
                    .userId("auth0|675f4b3c9a80612ce548e067")
                    .packageId("3e1a7d9b-5c4f-4a3b-a7f6-cbd4f8e2af16")
                    .status(BookingStatus.BOOKING_CONFIRMED)
                    .bookingDate(LocalDate.now().minusMonths(1))
                    .totalPrice(1600.0)
                    .build(),

            Booking.builder()
                    .bookingId("42s0353a-af87-4c9d-9f49-102bf198b172")
                    .userId("auth0|675f4b3c9a80612ce548e067")
                    .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                    .status(BookingStatus.BOOKING_CONFIRMED)
                    .bookingDate(LocalDate.now().minusMonths(6))
                    .totalPrice(1300.0)
                    .build(),

            Booking.builder()
                    .bookingId("032931cf-57d3-4278-b0db-8762731a7aa2")
                    .userId("auth0|675f4b3c9a80612ce548e067")
                    .packageId("2d1b4a3f-4e7f-42a7-a6fb-b4a1e7d9cf14")
                    .status(BookingStatus.BOOKING_CONFIRMED)
                    .bookingDate(LocalDate.now().minusWeeks(4))
                    .totalPrice(2000.0)
                    .build(),

            Booking.builder()
                    .bookingId("7ce476dc-3a8e-44ac-9904-fb3bb3d12259")
                    .userId("auth0|675f4b3c9a80612ce548e067")
                    .packageId("5c3b6d4e-9f42-4d1e-8ba4-df23cb19d8f4")
                    .status(BookingStatus.PAYMENT_PENDING)
                    .bookingDate(LocalDate.now())
                    .totalPrice(1700.0)
                    .build(),

            Booking.builder()
                    .bookingId("b8bf0325-471a-43b8-a46a-7ecdf33fed23")
                    .userId("auth0|675f4b3c9a80612ce548e067")
                    .packageId("3e1a7d9b-5c4f-4a3b-a7f6-cbd4f8e2af16")
                    .status(BookingStatus.BOOKING_CONFIRMED)
                    .bookingDate(LocalDate.now().minusMonths(2))
                    .totalPrice(1900.0)
                    .build(),

            Booking.builder()
                    .bookingId("e5310ab5-618b-4ea0-847c-1db26b36b845")
                    .userId("auth0|675f4b3c9a80612ce548e067")
                    .packageId("9d3f8b7e-a6c1-4f42-b6d8-7a9b4e2d5cf3")
                    .status(BookingStatus.BOOKING_CONFIRMED)
                    .bookingDate(LocalDate.now().minusYears(1))
                    .totalPrice(2200.0)
                    .build(),

            Booking.builder()
                    .bookingId("b46bab5b-0358-4ed0-8136-f064e3ca6d08")
                    .userId("auth0|675f4b3c9a80612ce548e067")
                    .packageId("8f2d7e6b-b3c4-4f9e-b72d-f9a3c8e1af25")
                    .status(BookingStatus.BOOKING_CONFIRMED)
                    .bookingDate(LocalDate.now().minusMonths(5))
                    .totalPrice(1800.0)
                    .build(),

            Booking.builder()
                    .bookingId("c63430a2-6535-4c2e-8036-6d952dbe1448")
                    .userId("auth0|675f4b3c9a80612ce548e067")
                    .packageId("7c5f9a6b-d8e4-4f9e-a6b7-3f2c1b5e9a25")
                    .status(BookingStatus.PAYMENT_ATTEMPT2_PENDING)
                    .bookingDate(LocalDate.now().minusDays(3))
                    .totalPrice(2100.0)
                    .build(),

            Booking.builder()
                    .bookingId("d4b8979a-cd20-491d-884f-7ec52d2a2a81")
                    .userId("auth0|675f4b3c9a80612ce548e067")
                    .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                    .status(BookingStatus.BOOKING_CONFIRMED)
                    .bookingDate(LocalDate.now().minusWeeks(2))
                    .totalPrice(1500.0)
                    .build(),

            Booking.builder()
                    .bookingId("98183e89-c6cb-415a-8fb3-ddbc3d327fc7")
                    .userId("auth0|675f4b3c9a80612ce548e067")
                    .packageId("6a4b3c1f-d7e9-4a8f-b7e2-c5a3b1d9f7c6")
                    .status(BookingStatus.BOOKING_CONFIRMED)
                    .bookingDate(LocalDate.now().minusYears(1).minusMonths(2))
                    .totalPrice(2000.0)
                    .build(),

        // user:  675f4b619a80612ce548e068
                Booking.builder()
                        .bookingId("e5310ab5-618b-4ea0-847c-1db26b36b843")
                        .userId("auth0|675f4b619a80612ce548e068")
                        .packageId("4f3a6bde-bc68-4b1e-835a-1e5aaf7b752d")
                        .status(BookingStatus.BOOKING_CONFIRMED)
                        .bookingDate(LocalDate.now().minusMonths(2))
                        .totalPrice(1600.0)
                        .build(),

                Booking.builder()
                        .bookingId("b46bab5b-0358-4ed0-8136-f064e3ca6d08")
                        .userId("auth0|675f4b619a80612ce548e068")
                        .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .status(BookingStatus.PAYMENT_PENDING)
                        .bookingDate(LocalDate.now().minusMonths(1))
                        .totalPrice(1300.0)
                        .build(),

                Booking.builder()
                        .bookingId("c63430a2-6535-4c2e-8036-6d952dbe1448")
                        .userId("auth0|675f4b619a80612ce548e068")
                        .packageId("2d1b4a3f-4e7f-42a7-a6fb-b4a1e7d9cf14")
                        .status(BookingStatus.PAYMENT_ATTEMPT2_PENDING)
                        .bookingDate(LocalDate.now().minusWeeks(2))
                        .totalPrice(2000.0)
                        .build(),

                Booking.builder()
                        .bookingId("d4b8979a-cd20-491d-884f-7ec52d2a2a81")
                        .userId("auth0|675f4b619a80612ce548e068")
                        .packageId("5c3b6d4e-9f42-4d1e-8ba4-df23cb19d8f4")
                        .status(BookingStatus.BOOKING_FAILED)
                        .bookingDate(LocalDate.now().minusDays(10))
                        .totalPrice(1600.0)
                        .build(),

                Booking.builder()
                        .bookingId("98183e89-c6cb-415a-8fb3-ddbc3d327fc7")
                        .userId("auth0|675f4b619a80612ce548e068")
                        .packageId("3e1a7d9b-5c4f-4a3b-a7f6-cbd4f8e2af16")
                        .status(BookingStatus.BOOKING_CONFIRMED)
                        .bookingDate(LocalDate.now())
                        .totalPrice(1700.0)
                        .build(),
                // user: 675f4b7ae184fd643a8ed902

                Booking.builder()
                        .bookingId("3ad0353a-af87-4c9d-9f49-102bf198b175")
                        .userId("auth0|675f4b7ae184fd643a8ed902")
                        .packageId("4f3a6bde-bc68-4b1e-835a-1e5aaf7b752d")
                        .status(BookingStatus.BOOKING_CONFIRMED)
                        .bookingDate(LocalDate.now().minusMonths(3))
                        .totalPrice(1600.0)
                        .build(),

                Booking.builder()
                        .bookingId("032931cf-57d3-4278-b0db-8762731a7aa2")
                        .userId("auth0|675f4b7ae184fd643a8ed902")
                        .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .status(BookingStatus.BOOKING_CONFIRMED)
                        .bookingDate(LocalDate.now().minusMonths(2))
                        .totalPrice(1300.0)
                        .build(),

                Booking.builder()
                        .bookingId("b8bf0325-471a-43b8-a46a-7ecdf33fed23")
                        .userId("auth0|675f4b7ae184fd643a8ed902")
                        .packageId("2d1b4a3f-4e7f-42a7-a6fb-b4a1e7d9cf14")
                        .status(BookingStatus.PAYMENT_PENDING)
                        .bookingDate(LocalDate.now().minusWeeks(5))
                        .totalPrice(2000.0)
                        .build(),

                Booking.builder()
                        .bookingId("e5310ab5-618b-4ea0-847c-1db26b36b844")
                        .userId("auth0|675f4b7ae184fd643a8ed902")
                        .packageId("5c3b6d4e-9f42-4d1e-8ba4-df23cb19d8f4")
                        .status(BookingStatus.BOOKING_FAILED)
                        .bookingDate(LocalDate.now().minusWeeks(2))
                        .totalPrice(1600.0)
                        .build(),

                //user: 675f4b9d9a80612ce548e069
                Booking.builder()
                        .bookingId("2a4fff75-5ad6-4ea7-b3ca-b7eca3cdda23")
                        .userId("auth0|675f4b9d9a80612ce548e069")
                        .packageId("4f3a6bde-bc68-4b1e-835a-1e5aaf7b752d")
                        .status(BookingStatus.BOOKING_CONFIRMED)
                        .bookingDate(LocalDate.now().minusMonths(4))
                        .totalPrice(1600.0)
                        .build(),

                Booking.builder()
                        .bookingId("3ad0353a-af87-4c9d-9f49-102bf198b177")
                        .userId("auth0|675f4b9d9a80612ce548e069")
                        .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .status(BookingStatus.PAYMENT_PENDING)
                        .bookingDate(LocalDate.now().minusMonths(3))
                        .totalPrice(1300.0)
                        .build(),

                Booking.builder()
                        .bookingId("032931cf-57d3-4278-b0db-8762731a7aa2")
                        .userId("auth0|675f4b9d9a80612ce548e069")
                        .packageId("2d1b4a3f-4e7f-42a7-a6fb-b4a1e7d9cf14")
                        .status(BookingStatus.BOOKING_CONFIRMED)
                        .bookingDate(LocalDate.now().minusWeeks(6))
                        .totalPrice(2000.0)
                        .build(),

                Booking.builder()
                        .bookingId("206fa0ba-4803-4730-a7ab-70e6f3cf5826")
                        .userId("auth0|675f4bb4e184fd643a8ed903")
                        .packageId("4f3a6bde-bc68-4b1e-835a-1e5aaf7b752d")
                        .status(BookingStatus.BOOKING_CONFIRMED)
                        .bookingDate(LocalDate.now().minusMonths(3))
                        .travelerIds(List.of("c69fa655-d480-48e4-8b66-0d54c1b2b46d", "b3904b84-9cd3-46ca-bcf3-f2206411a842"))
                        .totalPrice(1600.0)
                        .build(),

                Booking.builder()
                        .bookingId("c4c6369b-63c6-4e29-92d5-f5bb264aa98f")
                        .userId("auth0|675f4bb4e184fd643a8ed903")
                        .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .status(BookingStatus.BOOKING_CONFIRMED)
                        .bookingDate(LocalDate.now().minusMonths(2))
                        .totalPrice(1300.0)
                        .build(),

                Booking.builder()
                        .bookingId("a30c2487-952a-437c-abad-610ec2d8bb85")
                        .userId("auth0|675f4bb4e184fd643a8ed903")
                        .packageId("2d1b4a3f-4e7f-42a7-a6fb-b4a1e7d9cf14")
                        .status(BookingStatus.PAYMENT_PENDING)
                        .bookingDate(LocalDate.now().minusWeeks(6))
                        .totalPrice(2000.0)
                        .build(),

                Booking.builder()
                        .bookingId("cfcbc4a8-20b2-486c-a95b-aa0cc872b841")
                        .userId("auth0|675f4bb4e184fd643a8ed903")
                        .packageId("5c3b6d4e-9f42-4d1e-8ba4-df23cb19d8f4")
                        .status(BookingStatus.BOOKING_FAILED)
                        .bookingDate(LocalDate.now().minusWeeks(3))
                        .totalPrice(1600.0)
                        .build(),

                Booking.builder()
                        .bookingId("11ecc760-10d0-4713-a9ee-0aed88f38f32")
                        .userId("auth0|675f4bb4e184fd643a8ed903")
                        .packageId("3e1a7d9b-5c4f-4a3b-a7f6-cbd4f8e2af16")
                        .status(BookingStatus.BOOKING_CONFIRMED)
                        .bookingDate(LocalDate.now())
                        .totalPrice(1700.0)
                        .build(),

                Booking.builder()
                        .bookingId("c433128f-96b5-49b9-8a0c-b2db3602ffb6")
                        .userId("auth0|675f4bb4e184fd643a8ed903")
                        .packageId("9d3f8b7e-a6c1-4f42-b6d8-7a9b4e2d5cf3")
                        .status(BookingStatus.REFUNDED)
                        .bookingDate(LocalDate.now().plusDays(10))
                        .totalPrice(2200.0)
                        .build(),

                Booking.builder()
                        .bookingId("2fb6db54-8c75-49ed-9e87-f656c73101d5")
                        .userId("auth0|675f4bb4e184fd643a8ed903")
                        .packageId("8f2d7e6b-b3c4-4f9e-b72d-f9a3c8e1af25")
                        .status(BookingStatus.PAYMENT_ATTEMPT2_PENDING)
                        .bookingDate(LocalDate.now().plusWeeks(2))
                        .totalPrice(1800.0)
                        .build(),

                Booking.builder()
                        .bookingId("1b02a876-9714-48ff-ace3-3035222e256c")
                        .userId("auth0|675f4bb4e184fd643a8ed903")
                        .packageId("7c5f9a6b-d8e4-4f9e-a6b7-3f2c1b5e9a25")
                        .status(BookingStatus.BOOKING_CONFIRMED)
                        .bookingDate(LocalDate.now().plusMonths(1))
                        .totalPrice(1900.0)
                        .build(),
                // user: 675f4bb4e184fd643a8ed903
                Booking.builder()
                        .bookingId("dc04ebc7-82c3-4b89-a33b-cfd3c16d2420")
                        .userId("auth0|675f4bb4e184fd643a8ed903")
                        .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .status(BookingStatus.BOOKING_CONFIRMED)
                        .bookingDate(LocalDate.now().plusMonths(2))
                        .totalPrice(1500.0)
                        .build(),

                Booking.builder()
                        .bookingId("e8f9b624-0fd8-4db7-8fdf-e28af7e652cb")
                        .userId("auth0|675f4bb4e184fd643a8ed903")
                        .packageId("6a4b3c1f-d7e9-4a8f-b7e2-c5a3b1d9f7c6")
                        .status(BookingStatus.PAYMENT_ATTEMPT2_PENDING)
                        .bookingDate(LocalDate.now().plusMonths(3))
                        .totalPrice(2000.0)
                        .build()
        );


        bookingRepository.deleteAll()
                .thenMany(Flux.fromIterable(sampleBookings))
                .flatMap(bookingRepository::save)
                .doOnNext(booking -> log.info("Preloaded booking: {}", booking))
                .subscribe(
                        success -> log.info("Bookings preloaded successfully."),
                        error -> log.error("Error preloading bookings: {}", error.getMessage())
                );
    }


    private final ReviewRepository reviewRepository;

    @PostConstruct
    public void loadReviews() {
        List<Review> sampleReviews = List.of(
                // Admins
                Review.builder()
                        .packageId("4f3a6bde-bc68-4b1e-835a-1e5aaf7b752d")
                        .reviewId("f6e91f6c-723e-43b9-812f-2f3d3bfb4123")
                        .reviewerName("Amelia Clark")
                        .review("This was an amazing experience! I would definitely recommend it to anyone.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("4f3a6bde-bc68-4b1e-835a-1e5aaf7b752d")
                        .reviewId("f6e91f6c-723e-43b9-812f-2f3d3bfb4121")
                        .reviewerName("John Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("4f3a6bde-bc68-4b1e-835a-1e5aaf7b752d")
                        .reviewId("f6e91f6c-723e-43b9-812f-2f3d3bfb4122")
                        .reviewerName("Amelia Johnson")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(4)
                        .build(),
                Review.builder()
                        .packageId("4f3a6bde-bc68-4b1e-835a-1e5aaf7b752d")
                        .reviewId("f6e91f6c-723e-43b9-812f-2f3d3bfb4124")
                        .reviewerName("John Johnson")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(3)
                        .build(),
                Review.builder()
                        .packageId("4f3a6bde-bc68-4b1e-835a-1e5aaf7b752d")
                        .reviewId("f6e91f6c-723e-43b9-812f-2f3d3bfb4125")
                        .reviewerName("John Doe")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("4f3a6bde-bc68-4b1e-835a-1e5aaf7b752d")
                        .reviewId("f6e91f6c-723e-43b9-812f-2f3d3bfb4126")
                        .reviewerName("Jason Clark")
                        .review("Bad experience. Would not recommend.")
                        .rating(1)
                        .build(),
                Review.builder()
                        .packageId("4f3a6bde-bc68-4b1e-835a-1e5aaf7b752d")
                        .reviewId("f6e91f6c-723e-43b9-812f-2f3d3bfb4127")
                        .reviewerName("Mael Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("4f3a6bde-bc68-4b1e-835a-1e5aaf7b752d")
                        .reviewId("f6e91f6c-723e-43b9-812f-2f3d3bfb4128")
                        .reviewerName("Christine Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("4f3a6bde-bc68-4b1e-835a-1e5aaf7b752d")
                        .reviewId("f6e91f6c-723e-43b9-812f-2f3d3bfb4129")
                        .reviewerName("Emie Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(4)
                        .build(),
                Review.builder()
                        .packageId("4f3a6bde-bc68-4b1e-835a-1e5aaf7b752d")
                        .reviewId("f6e91f6c-723e-43b9-812f-2f3d3bfb4130")
                        .reviewerName("Johna Doe")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("4f3a6bde-bc68-4b1e-835a-1e5aaf7b752d")
                        .reviewId("f6e91f6c-723e-43b9-812f-2f3d3bfb4120")
                        .reviewerName("Amelia Second")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),


                Review.builder()
                        .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .reviewId("a6e91f6c-723e-43b9-812f-2f3d3bfb4123")
                        .reviewerName("Amelia Clark")
                        .review("This was an amazing experience! I would definitely recommend it to anyone.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .reviewId("b6e91f6c-723e-43b9-812f-2f3d3bfb4121")
                        .reviewerName("John Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .reviewId("c6e91f6c-723e-43b9-812f-2f3d3bfb4122")
                        .reviewerName("Amelia Johnson")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(4)
                        .build(),
                Review.builder()
                        .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .reviewId("d6e91f6c-723e-43b9-812f-2f3d3bfb4124")
                        .reviewerName("John Johnson")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(3)
                        .build(),
                Review.builder()
                        .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .reviewId("e6e91f6c-723e-43b9-812f-2f3d3bfb4125")
                        .reviewerName("John Doe")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .reviewId("g6e91f6c-723e-43b9-812f-2f3d3bfb4126")
                        .reviewerName("Jason Clark")
                        .review("Bad experience. Would not recommend.")
                        .rating(1)
                        .build(),
                Review.builder()
                        .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .reviewId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa43")
                        .reviewerName("Mael Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .reviewId("h6e91f6c-723e-43b9-812f-2f3d3bfb4128")
                        .reviewerName("Christine Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .reviewId("i6e91f6c-723e-43b9-812f-2f3d3bfb4129")
                        .reviewerName("Emie Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(4)
                        .build(),
                Review.builder()
                        .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .reviewId("j6e91f6c-723e-43b9-812f-2f3d3bfb4130")
                        .reviewerName("Johna Doe")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("8e7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .reviewId("k6e91f6c-723e-43b9-812f-2f3d3bfb4120")
                        .reviewerName("Amelia Second")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),




                Review.builder()
                        .packageId("2d1b4a3f-4e7f-42a7-a6fb-b4a1e7d9cf14")
                        .reviewId("l6e91f6c-723e-43b9-812f-2f3d3bfb4123")
                        .reviewerName("Amelia Clark")
                        .review("This was an amazing experience! I would definitely recommend it to anyone.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("2d1b4a3f-4e7f-42a7-a6fb-b4a1e7d9cf14")
                        .reviewId("m6e91f6c-723e-43b9-812f-2f3d3bfb4121")
                        .reviewerName("John Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("2d1b4a3f-4e7f-42a7-a6fb-b4a1e7d9cf14")
                        .reviewId("o6e91f6c-723e-43b9-812f-2f3d3bfb4122")
                        .reviewerName("Amelia Johnson")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(4)
                        .build(),
                Review.builder()
                        .packageId("2d1b4a3f-4e7f-42a7-a6fb-b4a1e7d9cf14")
                        .reviewId("p6e91f6c-723e-43b9-812f-2f3d3bfb4124")
                        .reviewerName("John Johnson")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(3)
                        .build(),
                Review.builder()
                        .packageId("2d1b4a3f-4e7f-42a7-a6fb-b4a1e7d9cf14")
                        .reviewId("q6e91f6c-723e-43b9-812f-2f3d3bfb4125")
                        .reviewerName("John Doe")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("2d1b4a3f-4e7f-42a7-a6fb-b4a1e7d9cf14")
                        .reviewId("r6e91f6c-723e-43b9-812f-2f3d3bfb4126")
                        .reviewerName("Jason Clark")
                        .review("Bad experience. Would not recommend.")
                        .rating(1)
                        .build(),
                Review.builder()
                        .packageId("2d1b4a3f-4e7f-42a7-a6fb-b4a1e7d9cf14")
                        .reviewId("se7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .reviewerName("Mael Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("2d1b4a3f-4e7f-42a7-a6fb-b4a1e7d9cf14")
                        .reviewId("t6e91f6c-723e-43b9-812f-2f3d3bfb4128")
                        .reviewerName("Christine Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("2d1b4a3f-4e7f-42a7-a6fb-b4a1e7d9cf14")
                        .reviewId("u6e91f6c-723e-43b9-812f-2f3d3bfb4129")
                        .reviewerName("Emie Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(4)
                        .build(),
                Review.builder()
                        .packageId("2d1b4a3f-4e7f-42a7-a6fb-b4a1e7d9cf14")
                        .reviewId("v6e91f6c-723e-43b9-812f-2f3d3bfb4130")
                        .reviewerName("Johna Doe")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("2d1b4a3f-4e7f-42a7-a6fb-b4a1e7d9cf14")
                        .reviewId("w6e91f6c-723e-43b9-812f-2f3d3bfb4120")
                        .reviewerName("Amelia Second")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),




                Review.builder()
                        .packageId("5c3b6d4e-9f42-4d1e-8ba4-df23cb19d8f4")
                        .reviewId("x6e91f6c-723e-43b9-812f-2f3d3bfb4123")
                        .reviewerName("Amelia Clark")
                        .review("This was an amazing experience! I would definitely recommend it to anyone.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("5c3b6d4e-9f42-4d1e-8ba4-df23cb19d8f4")
                        .reviewId("y6e91f6c-723e-43b9-812f-2f3d3bfb4121")
                        .reviewerName("John Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("5c3b6d4e-9f42-4d1e-8ba4-df23cb19d8f4")
                        .reviewId("z6e91f6c-723e-43b9-812f-2f3d3bfb4122")
                        .reviewerName("Amelia Johnson")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(4)
                        .build(),
                Review.builder()
                        .packageId("5c3b6d4e-9f42-4d1e-8ba4-df23cb19d8f4")
                        .reviewId("f1e91f6c-723e-43b9-812f-2f3d3bfb4124")
                        .reviewerName("John Johnson")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(3)
                        .build(),
                Review.builder()
                        .packageId("5c3b6d4e-9f42-4d1e-8ba4-df23cb19d8f4")
                        .reviewId("f2e91f6c-723e-43b9-812f-2f3d3bfb4125")
                        .reviewerName("John Doe")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("5c3b6d4e-9f42-4d1e-8ba4-df23cb19d8f4")
                        .reviewId("f3e91f6c-723e-43b9-812f-2f3d3bfb4126")
                        .reviewerName("Jason Clark")
                        .review("Bad experience. Would not recommend.")
                        .rating(1)
                        .build(),
                Review.builder()
                        .packageId("5c3b6d4e-9f42-4d1e-8ba4-df23cb19d8f4")
                        .reviewId("8e3a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .reviewerName("Mael Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("5c3b6d4e-9f42-4d1e-8ba4-df23cb19d8f4")
                        .reviewId("f4e91f6c-723e-43b9-812f-2f3d3bfb4128")
                        .reviewerName("Christine Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("5c3b6d4e-9f42-4d1e-8ba4-df23cb19d8f4")
                        .reviewId("f5e91f6c-723e-43b9-812f-2f3d3bfb4129")
                        .reviewerName("Emie Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(4)
                        .build(),
                Review.builder()
                        .packageId("5c3b6d4e-9f42-4d1e-8ba4-df23cb19d8f4")
                        .reviewId("f7e91f6c-723e-43b9-812f-2f3d3bfb4130")
                        .reviewerName("Johna Doe")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("5c3b6d4e-9f42-4d1e-8ba4-df23cb19d8f4")
                        .reviewId("f8e91f6c-723e-43b9-812f-2f3d3bfb4120")
                        .reviewerName("Amelia Second")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),




                Review.builder()
                        .packageId("3e1a7d9b-5c4f-4a3b-a7f6-cbd4f8e2af16")
                        .reviewId("f9e91f6c-723e-43b9-812f-2f3d3bfb4123")
                        .reviewerName("Amelia Clark")
                        .review("This was an amazing experience! I would definitely recommend it to anyone.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("3e1a7d9b-5c4f-4a3b-a7f6-cbd4f8e2af16")
                        .reviewId("f6a91f6c-723e-43b9-812f-2f3d3bfb4121")
                        .reviewerName("John Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("3e1a7d9b-5c4f-4a3b-a7f6-cbd4f8e2af16")
                        .reviewId("f6b91f6c-723e-43b9-812f-2f3d3bfb4122")
                        .reviewerName("Amelia Johnson")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(4)
                        .build(),
                Review.builder()
                        .packageId("3e1a7d9b-5c4f-4a3b-a7f6-cbd4f8e2af16")
                        .reviewId("f6c91f6c-723e-43b9-812f-2f3d3bfb4124")
                        .reviewerName("John Johnson")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(3)
                        .build(),
                Review.builder()
                        .packageId("3e1a7d9b-5c4f-4a3b-a7f6-cbd4f8e2af16")
                        .reviewId("f6d91f6c-723e-43b9-812f-2f3d3bfb4125")
                        .reviewerName("John Doe")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("3e1a7d9b-5c4f-4a3b-a7f6-cbd4f8e2af16")
                        .reviewId("f6f91f6c-723e-43b9-812f-2f3d3bfb4126")
                        .reviewerName("Jason Clark")
                        .review("Bad experience. Would not recommend.")
                        .rating(1)
                        .build(),
                Review.builder()
                        .packageId("3e1a7d9b-5c4f-4a3b-a7f6-cbd4f8e2af16")
                        .reviewId("8g7a6dbc-cd45-44d2-9bfb-3419a6b4fa45")
                        .reviewerName("Mael Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("3e1a7d9b-5c4f-4a3b-a7f6-cbd4f8e2af16")
                        .reviewId("f6h91f6c-723e-43b9-812f-2f3d3bfb4128")
                        .reviewerName("Christine Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("3e1a7d9b-5c4f-4a3b-a7f6-cbd4f8e2af16")
                        .reviewId("f6i91f6c-723e-43b9-812f-2f3d3bfb4129")
                        .reviewerName("Emie Clark")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(4)
                        .build(),
                Review.builder()
                        .packageId("3e1a7d9b-5c4f-4a3b-a7f6-cbd4f8e2af16")
                        .reviewId("f6j91f6c-723e-43b9-812f-2f3d3bfb4130")
                        .reviewerName("Johna Doe")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build(),
                Review.builder()
                        .packageId("3e1a7d9b-5c4f-4a3b-a7f6-cbd4f8e2af16")
                        .reviewId("f6k91f6c-723e-43b9-812f-2f3d3bfb4120")
                        .reviewerName("Amelia Second")
                        .review("The tour was great, but the accommodations could have been better.")
                        .rating(5)
                        .build()

                );

        reviewRepository.deleteAll()
                .thenMany(Flux.fromIterable(sampleReviews))
                .flatMap(reviewRepository::save)
                .doOnNext(user -> log.info("Preloaded review: {}", user))
                .subscribe(
                        success -> log.info("Reviews preloaded successfully."),
                        error -> log.error("Error preloading reviews: {}", error.getMessage())
                );
    }

    private final NotificationRepository notificationRepository;

    @PostConstruct
    public void loadNotifications() {
        List<Notification> sampleNotifications = List.of(
                Notification.builder()
                        .notificationId("2b1c1f70-1a5f-4e88-9945-2b6a0e476002")
                        .to("amelia.clark@traveltrove.com")
                        .subject("Welcome to Travel Trove!")
                        .messageContent("Thank you for joining Travel Trove!")
                        .sentAt(LocalDateTime.of(2025, 1, 1, 9, 0))
                        .build(),
                Notification.builder()
                        .notificationId("3a24f7b1-98e4-49d3-b6ff-7c19fae24d9c")
                        .to("liam.jones@traveltrove.com")
                        .subject("Booking Confirmation")
                        .messageContent("Your booking has been confirmed.")
                        .sentAt(LocalDateTime.of(2025, 1, 2, 10, 30))
                        .build(),
                Notification.builder()
                        .notificationId("4c88a8d2-913a-4f92-b8fa-6ad3dbec29b1")
                        .to("oliver.smith@traveltrove.com")
                        .subject("Tour Update")
                        .messageContent("One of your tours has been updated.")
                        .sentAt(LocalDateTime.of(2025, 1, 3, 14, 15))
                        .build(),
                Notification.builder()
                        .notificationId("9b1204c3-cb85-4dfb-9b4e-cfdfb223b2b6")
                        .to("emma.brown@traveltrove.com")
                        .subject("Feedback Request")
                        .messageContent("We would love to hear about your experience.")
                        .sentAt(LocalDateTime.of(2025, 1, 4, 16, 45))
                        .build(),
                Notification.builder()
                        .notificationId("1d8d441d-18f1-4395-81c1-0f56b3c17fa4")
                        .to("william.taylor@traveltrove.com")
                        .subject("Spots Running Out!")
                        .messageContent("Hurry! Limited spots left for your desired tour.")
                        .sentAt(LocalDateTime.of(2025, 1, 5, 12, 0))
                        .build(),
                Notification.builder()
                        .notificationId("f1a26a34-5c12-4d71-86b7-28f5c87c5af5")
                        .to("sophia.johnson@example.com")
                        .subject("Special Offer")
                        .messageContent("Enjoy exclusive discounts on your next booking.")
                        .sentAt(LocalDateTime.of(2025, 1, 6, 18, 30))
                        .build(),
                Notification.builder()
                        .notificationId("81f62e87-3c5e-4729-bbc2-264d1af8ad23")
                        .to("james.williams@example.com")
                        .subject("Booking Reminder")
                        .messageContent("Your tour is coming up soon! Get ready!")
                        .sentAt(LocalDateTime.of(2025, 1, 7, 8, 15))
                        .build(),
                Notification.builder()
                        .notificationId("4bc1c0fa-3b5b-4f7d-8f6c-e3d2c5af36ed")
                        .to("ava.davis@example.com")
                        .subject("Payment Confirmation")
                        .messageContent("Your payment has been successfully processed.")
                        .sentAt(LocalDateTime.of(2025, 1, 8, 11, 0))
                        .build(),
                Notification.builder()
                        .notificationId("96c1d5f2-c7ea-4f9e-b10b-4b3afc16b76a")
                        .to("mason.miller@example.com")
                        .subject("New Destinations Added")
                        .messageContent("Check out the latest additions to our tours.")
                        .sentAt(LocalDateTime.of(2025, 1, 9, 15, 45))
                        .build(),
                Notification.builder()
                        .notificationId("f7e1d3c7-b1e2-481f-91a8-5f6d2ab4c5a7")
                        .to("mia.moore@example.com")
                        .subject("Welcome Back!")
                        .messageContent("We missed you! Explore new travel opportunities.")
                        .sentAt(LocalDateTime.of(2025, 1, 10, 9, 0))
                        .build()
        );

        notificationRepository.deleteAll()
                .thenMany(Flux.fromIterable(sampleNotifications))
                .flatMap(notificationRepository::save)
                .doOnNext(notification -> log.info("Preloaded notification: {}", notification))
                .subscribe(
                        success -> log.info("Notifications preloaded successfully."),
                        error -> log.error("Error preloading notifications: {}", error.getMessage())
                );
    }

    //payments
    private final PaymentRepository paymentRepository;
    @PostConstruct
    public void loadPayments() {
        List<Payment> samplePayments = List.of(
                // January 2023
                createPayment("1a2b3c", "booking1", 1000L, "usd", LocalDateTime.of(2023, 1, 5, 14, 30), "succeeded", "stripe1"),
                createPayment("1b2c3d", "booking2", 1200L, "usd", LocalDateTime.of(2023, 1, 25, 10, 15), "failed", "stripe2"),

                //  for February 2023
                createPayment("2a3b4c", "booking3", 1500L, "usd", LocalDateTime.of(2023, 2, 10, 18, 45), "created", "stripe3"),

                // March 2023
                createPayment("3a4b5c", "booking4", 2000L, "usd", LocalDateTime.of(2023, 3, 15, 9, 0), "succeeded", "stripe4"),

                //  April 2023
                createPayment("4a5b6c", "booking5", 1800L, "usd", LocalDateTime.of(2023, 4, 25, 12, 0), "failed", "stripe5"),

                // January 2024
                createPayment("5a6b7c", "booking6", 2500L, "usd", LocalDateTime.of(2024, 1, 10, 8, 30), "succeeded", "stripe6"),

                //  February 2024
                createPayment("6a7b8c", "booking7", 3000L, "usd", LocalDateTime.of(2024, 2, 5, 16, 0), "created", "stripe7"),
                createPayment("7a8b9c", "booking8", 2200L, "usd", LocalDateTime.of(2024, 2, 28, 21, 30), "succeeded", "stripe8"),

                //  March 2024
                createPayment("8a9b0c", "booking9", 5000L, "usd", LocalDateTime.of(2024, 3, 17, 11, 0), "succeeded", "stripe9"),

                //  December 2024 (end-of-year edge case)
                createPayment("9a0b1c", "booking10", 2700L, "usd", LocalDateTime.of(2024, 12, 31, 23, 59), "failed", "stripe10"),

                // January 2025 (new year edge case)
                createPayment("0a1b2c", "booking11", 3100L, "usd", LocalDateTime.of(2025, 1, 1, 0, 1), "succeeded", "stripe11"),

                        Payment.builder()
                        .paymentId("1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d")
                        .bookingId("2a4fff75-5ad6-4ea7-b3ca-b7eca3cdda23")
                        .paymentDate(LocalDateTime.now().minusDays(1))
                        .amount(190000L)
                        .status("PAYMENT_SUCCESSFUL")
                        .stripePaymentId("sample: ch_1Jb02a876-9714-48ff-ace3-3035222e256c")
                        .currency("USD")
                        .build(),
                Payment.builder()
                        .paymentId("2b3c4d5e-6f7a-8b9c-0d1e-2f3a4b5c6d7")
                        .bookingId("e5310ab5-618b-4ea0-847c-1db26b36b843")
                        .paymentDate(LocalDateTime.now().minusDays(1))
                        .amount(150000L)
                        .status("PAYMENT_SUCCESSFUL")
                        .stripePaymentId("sample: ch_2dc04ebc7-82c3-4b89-a33b-cfd3c16d2420")
                        .currency("USD")
                        .build(),
                Payment.builder()
                        .paymentId("3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8")
                        .bookingId("3ad0353a-af87-4c9d-9f49-102bf198b175")
                        .paymentDate(LocalDateTime.now().minusDays(1))
                        .amount(200000L)
                        .status("PAYMENT_SUCCESSFUL")
                        .stripePaymentId("sample: ch_3e8f9b624-0fd8-4db7-8fdf-e28af7e652cb")
                        .currency("USD")
                        .build(),
                Payment.builder()
                        .paymentId("4d5e6f7a-8b9c-0d1e-2f3a-4b5c6d7e8f9")
                        .bookingId("206fa0ba-4803-4730-a7ab-70e6f3cf5826")
                        .paymentDate(LocalDateTime.now().minusDays(1))
                        .amount(175000L)
                        .status("PAYMENT_SUCCESSFUL")
                        .stripePaymentId("sample: ch_4a7b8c9d5-6e7f-8a9b-0c1d2e3f4a5b")
                        .currency("USD")
                        .build()
                );

        paymentRepository.deleteAll()
                .thenMany(Flux.fromIterable(samplePayments))
                .flatMap(paymentRepository::save)
                .doOnNext(payment -> log.info("Preloaded payment: {}", payment))
                .subscribe(
                        success -> log.info("Payments preloaded successfully."),
                        error -> log.error("Error preloading payments: {}", error.getMessage())
                );
    }

    private Payment createPayment(String paymentId, String bookingId, Long amount, String currency, LocalDateTime date, String status, String stripePaymentId) {
        return Payment.builder()
                .paymentId(paymentId)
                .bookingId(bookingId)
                .amount(amount)
                .currency(currency)
                .paymentDate(date)
                .status(status)
                .stripePaymentId(stripePaymentId)
                .build();
    }


}

