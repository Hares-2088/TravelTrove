package com.traveltrove.betraveltrove.presentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityRequestModel {
    private String tourId;
    private String name;
    private String description;
    private String image;
    private LocalDate startDate;
    private String hotel;
    //private String cityId;
}
