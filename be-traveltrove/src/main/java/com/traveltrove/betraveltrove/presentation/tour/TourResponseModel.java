package com.traveltrove.betraveltrove.presentation.tour;

import com.traveltrove.betraveltrove.presentation.CityResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourResponseModel {

    private String tourId;
    private String name;
    private String startDate;
    private String endDate;
    private String overallDescription;
    private double price;
    private int spotsAvailable;
    private boolean available;
    private String image;
    private String itineraryPicture;

    private List<CityResponseModel> cities;
}
