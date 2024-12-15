package com.traveltrove.betraveltrove.presentation.tourpackage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String startDate;
    private String endDate;
    private String priceSingle;
    private String priceDouble;
    private String priceTriple;
}
