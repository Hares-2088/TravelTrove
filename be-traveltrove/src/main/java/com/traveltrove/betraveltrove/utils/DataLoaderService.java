package com.traveltrove.betraveltrove.utils;

import com.traveltrove.betraveltrove.dataaccess.Booking;
import com.traveltrove.betraveltrove.dataaccess.Review;
import com.traveltrove.betraveltrove.dataaccess.tour.City;
import com.traveltrove.betraveltrove.dataaccess.tour.Event;
import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
import com.traveltrove.betraveltrove.dataaccess.tour.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataLoaderService implements CommandLineRunner {

    @Autowired
    TourRepository tourRepository;

    @Override
    public void run(String... args) throws Exception {

        Event event1 = Event.builder()
                .eventId("1")
                .name("Event 1")
                .description("Event 1 Description")
                .image("Event 1 Image")
                .date(LocalDate.of(2025, 1, 1))
                .gatheringTime(LocalDate.of(2025, 1, 1).atTime(8, 0))
                .departureTime(LocalDate.of(2025, 1, 1).atTime(9, 0))
                .endTime(LocalDate.of(2025, 1, 1).atTime(10, 0))
                .build();

        List<Event> events = new ArrayList<>();
        events.add(event1);


        City city1 = City.builder()
                .cityId("1")
                .name("City 1")
                .description("City 1 Description")
                .image("City 1 Image")
                .startDate(LocalDate.of(2025, 1, 1))
                .hotel("City 1 Hotel")
                .events(events)
                .build();

        List<City> cities = new ArrayList<>();
        cities.add(city1);


        Review review1 = Review.builder()
                .reviewId("1")
                .tourId("1")
                .rating(5)
                .review("Review 1 Comment")
                .reviewerName("Reviewer 1")
                .date(LocalDate.of(2025, 1, 1).atTime(8, 0))
                .build();

        List<Review> reviews = new ArrayList<>();
        reviews.add(review1);

        Booking booking1 = Booking.builder()
                .bookingId("1")
                .tourId("1")
                .customerId("1")
                .bookingDate(LocalDate.of(2025, 1, 1))
                .build();

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);

        Tour tour1 = Tour.builder()
                .tourId("1")
                .name("Tour 1")
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 1, 10))
                .overallDescription("Tour 1 Description")
                .available(true)
                .price(1000)
                .spotsAvailable(10)
                .cities(cities)
                .reviews(reviews)
                .bookings(bookings)
                .image("Tour 1 Image")
                .itineraryPicture("Tour 1 Itinerary Picture")
                .build();

        Flux.just(tour1)
                .flatMap(tourRepository::insert)
                .doOnNext(savedTour -> System.out.println("Tour saved: " + savedTour))
                .doOnError(error -> System.out.println("Error inserting tour: " + error.getMessage()))
                .subscribe();
    }
}
