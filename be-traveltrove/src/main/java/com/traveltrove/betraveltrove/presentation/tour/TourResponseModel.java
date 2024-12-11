package com.traveltrove.betraveltrove.presentation.tour;

import com.traveltrove.betraveltrove.presentation.city.CityResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.event.ListDataEvent;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourResponseModel {
    private String tourId;
    private String name;
    private String description;
}
