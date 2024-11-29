package com.traveltrove.betraveltrove.presentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseModel {

    private String eventId;
    private String name;
    private String description;
    private String image;
    private LocalDate startDate;
    private LocalDateTime gatheringTime;
    private LocalDateTime departureTime;
    private LocalDateTime endTime;
}
