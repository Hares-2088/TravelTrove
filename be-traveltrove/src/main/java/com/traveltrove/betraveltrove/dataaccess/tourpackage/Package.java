package com.traveltrove.betraveltrove.dataaccess.tourpackage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

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
    private LocalDate startDate;
    private LocalDate endDate;
    private Double priceSingle;
    private Double priceDouble;
    private Double priceTriple;

    private Integer availableSeats;
    private Integer totalSeats;

}
