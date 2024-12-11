package com.traveltrove.betraveltrove.dataaccess.country;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "hotels")
public class Hotel {

    @Id
    private String id;

    private String hotelId;
    private String name;
    private String cityId;
    private String url;
}
