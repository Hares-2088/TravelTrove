package com.traveltrove.betraveltrove.dataaccess.tour;

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
@Document(collection = "tours")
public class Tour {

    @Id
    private String id;
    private String tourId;
    private String name;
    private String description;
}
