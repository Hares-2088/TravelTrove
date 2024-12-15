package com.traveltrove.betraveltrove.dataaccess.tourpackage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "packages")
public class Package {

    @Id
    private String id; //private id

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
