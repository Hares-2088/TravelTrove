package com.traveltrove.betraveltrove.presentation;

import com.traveltrove.betraveltrove.presentation.events.EventResponseModel;
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
public class EventRequestModel {
    private String cityId;
    private String name;
    private LocalDate startDate;
    private String image;
    private String description;
    private LocalDateTime gatheringTime;
    private LocalDateTime departureTime;
    private LocalDateTime endTime;
}
