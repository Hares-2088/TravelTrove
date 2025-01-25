package com.traveltrove.betraveltrove.presentation.events;

import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestModel {

    @Nullable
    private String cityId;
    @Nullable
    private String countryId;

    private String name;
    private String description;
    private String eventImageUrl;
}
