package com.traveltrove.betraveltrove.dataaccess.hotel;

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
@Document(collection = "hotels")
public class Hotel {

    @Id
    private String id;

    private String hotelId;
    private String name;
    private String cityId;
    private String url;
}
