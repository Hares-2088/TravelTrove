package com.traveltrove.betraveltrove.presentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourRequestModel {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String overallDescription;
    private double price;
    private int spotsAvailable;
    private String image;
    private String itineraryPicture;
}
