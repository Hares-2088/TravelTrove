package com.traveltrove.betraveltrove.presentation.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String eventImageUrl;
}
