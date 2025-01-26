package com.traveltrove.betraveltrove.presentation.tourpackage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageResponseModel {

    //foreign keys
    private String airportId;
    private String tourId;

    private String packageId; //public id
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double priceSingle;
    private Double priceDouble;
    private Double priceTriple;

    private Integer totalSeats;
    private Integer availableSeats;

    private String packageImageUrl;
}
