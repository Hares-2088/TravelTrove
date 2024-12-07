package com.traveltrove.betraveltrove.utils;

import com.traveltrove.betraveltrove.dataaccess.city.City;
import com.traveltrove.betraveltrove.dataaccess.tour.Event;
import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import com.traveltrove.betraveltrove.dataaccess.tour.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;

@Service
public class DataLoaderService implements CommandLineRunner {

    @Autowired
    TourRepository tourRepository;

    @Override
    public void run(String... args) throws Exception {
        // Tour 1: Greece Tour
        Event event1 = Event.builder()
                .eventId("1")
                .name("Acropolis Visit")
                .description("Explore the ancient Acropolis in Athens.")
                .image("https://example.com/acropolis.jpg")
                .startDate(LocalDate.of(2025, 1, 1))
                .gatheringTime(LocalDate.of(2025, 1, 1).atTime(8, 0))
                .departureTime(LocalDate.of(2025, 1, 1).atTime(9, 0))
                .endTime(LocalDate.of(2025, 1, 1).atTime(12, 0))
                .build();

        City city1 = City.builder()
                .cityId("1")
                .name("Athens")
                .description("The heart of ancient Greece.")
                .image("https://example.com/athens.jpg")
                .startDate(LocalDate.of(2025, 1, 1))
                .hotel("Athens Grand Hotel")
                .events(List.of(event1))
                .build();

        Tour tour1 = Tour.builder()
                .tourId("1")
                .name("Greece Tour")
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 1, 10))
                .overallDescription("Explore the history and beauty of Greece.")
                .available(true)
                .price(1200)
                .spotsAvailable(20)
                .cities(List.of(city1))
                .image("data:image/jpeg;base64,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")
                .itineraryPicture("https://example.com/itinerary-greece.jpg")
                .build();

        // Tour 2: Italy Tour
        Event event2 = Event.builder()
                .eventId("2")
                .name("Colosseum Tour")
                .description("Experience the grandeur of ancient Rome.")
                .image("https://example.com/colosseum.jpg")
                .startDate(LocalDate.of(2025, 2, 1))
                .gatheringTime(LocalDate.of(2025, 2, 1).atTime(9, 0))
                .departureTime(LocalDate.of(2025, 2, 1).atTime(10, 0))
                .endTime(LocalDate.of(2025, 2, 1).atTime(13, 0))
                .build();

        City city2 = City.builder()
                .cityId("2")
                .name("Rome")
                .description("The Eternal City.")
                .image("https://example.com/rome.jpg")
                .startDate(LocalDate.of(2025, 2, 1))
                .hotel("Rome Luxury Hotel")
                .events(List.of(event2))
                .build();

        Tour tour2 = Tour.builder()
                .tourId("2")
                .name("Italy Tour")
                .startDate(LocalDate.of(2025, 2, 1))
                .endDate(LocalDate.of(2025, 2, 15))
                .overallDescription("Discover the art and culture of Italy.")
                .available(true)
                .price(1500)
                .spotsAvailable(15)
                .cities(List.of(city2))
                .image("https://www.earthtrekkers.com/wp-content/uploads/2023/01/Castelmezzano-Italy.jpg.optimal.jpg")
                .itineraryPicture("https://example.com/itinerary-italy.jpg")
                .build();

        // Tour 3: Japan Tour
        Event event3 = Event.builder()
                .eventId("3")
                .name("Cherry Blossom Viewing")
                .description("Enjoy the stunning cherry blossoms in Tokyo.")
                .image("https://example.com/cherry-blossoms.jpg")
                .startDate(LocalDate.of(2025, 3, 15))
                .gatheringTime(LocalDate.of(2025, 3, 15).atTime(10, 0))
                .departureTime(LocalDate.of(2025, 3, 15).atTime(11, 0))
                .endTime(LocalDate.of(2025, 3, 15).atTime(15, 0))
                .build();

        City city3 = City.builder()
                .cityId("3")
                .name("Tokyo")
                .description("The vibrant capital of Japan.")
                .image("https://example.com/tokyo.jpg")
                .startDate(LocalDate.of(2025, 3, 15))
                .hotel("Tokyo Central Hotel")
                .events(List.of(event3))
                .build();

        Tour tour3 = Tour.builder()
                .tourId("3")
                .name("Japan Tour")
                .startDate(LocalDate.of(2025, 3, 15))
                .endDate(LocalDate.of(2025, 3, 25))
                .overallDescription("Experience the culture and beauty of Japan.")
                .available(true)
                .price(1800)
                .spotsAvailable(10)
                .cities(List.of(city3))
                .image("https://www.celebritycruises.com/blog/content/uploads/2021/03/what-is-japan-known-for-mt-fuji-hero-1920x890.jpg")
                .itineraryPicture("https://example.com/itinerary-japan.jpg")
                .build();

        // Save tours to the repository
        Flux.just(tour1, tour2, tour3)
                .flatMap(tourRepository::insert)
                .doOnNext(savedTour -> System.out.println("Tour saved: " + savedTour))
                .doOnError(error -> System.out.println("Error inserting tour: " + error.getMessage()))
                .subscribe();
    }
}
