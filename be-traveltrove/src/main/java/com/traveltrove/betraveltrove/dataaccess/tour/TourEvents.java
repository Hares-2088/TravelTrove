package com.traveltrove.betraveltrove.dataaccess.tour;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tourevents")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourEvents {

    @Id
    private String Id;
    private int seq;
    private String seqDesc;
    private String tourId; // FK tour ID
    private String events; // FK events ID
}
