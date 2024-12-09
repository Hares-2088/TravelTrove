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
@Document(collection = "tourevents")
public class TourEvent {
    @Id
    private String Id;
    private String tourEventId;
    private Integer seq;
    private String seqDesc;
    private String tourId;
    private String eventId;
}
