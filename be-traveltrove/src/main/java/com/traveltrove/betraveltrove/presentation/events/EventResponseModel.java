package com.traveltrove.betraveltrove.presentation.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseModel {

    private String eventId;
    private String cityId;
    private String countryId;
    private String name;
    private String description;
    private String image;
}
