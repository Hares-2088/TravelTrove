package com.traveltrove.betraveltrove.dataaccess.airport;
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
@Document(collection = "airports")
public class Airport {
    @Id
    private String id;
    private String airportId;
    private String name;
    private String cityId; //FK of City
}
