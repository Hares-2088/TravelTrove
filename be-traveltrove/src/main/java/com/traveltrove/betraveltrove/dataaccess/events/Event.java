package com.traveltrove.betraveltrove.dataaccess.events;

import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    private String id; //private

    private String eventId; //public

    @Nullable
    private String cityId; //not always related to a city

    @Nullable
    private String countryId;

    private String name;
    private LocalDate startDate;
    private String image;
    private String description;
    private LocalDateTime gatheringTime;
    private LocalDateTime departureTime;
    private LocalDateTime endTime;
}
