package com.traveltrove.betraveltrove.dataaccess.tour;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    private String eventId;
    private String cityId;
    private String name;
    private LocalDate date;
    private String image;
    private String description;
    private LocalDateTime gatheringTime;
    private LocalDateTime departureTime;
    private LocalDateTime endTime;
}
