package com.traveltrove.betraveltrove.dataaccess.tour;

import com.traveltrove.betraveltrove.dataaccess.Booking;
import com.traveltrove.betraveltrove.dataaccess.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "tour")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tour {

    @Id
    private String id;

    private String tourId;
    private String name;
    private String description;

    private List<TourEvents> tourEvents;
}
