package com.traveltrove.betraveltrove.presentation.tourpackage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String startDate;
    private String endDate;
    private String priceSingle;
    private String priceDouble;
    private String priceTriple;
}
