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
public class PackageRequestModel {

    //foreign keys
    private String airportId;
    private String tourId;

    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double priceSingle;
    private Double priceDouble;
    private Double priceTriple;

    private Integer totalSeats;
    private String packageImageUrl;


   // private Integer availableSeats; //commented out because it will be managed by the system


}
