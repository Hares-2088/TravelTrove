package com.traveltrove.betraveltrove.dataaccess.tour;

import com.traveltrove.betraveltrove.dataaccess.events.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class City {

    private String cityId;
    private String name;
    private String description;
    private String image;
    private LocalDate startDate;
    private List<Event> events;
    private String hotel;
}
