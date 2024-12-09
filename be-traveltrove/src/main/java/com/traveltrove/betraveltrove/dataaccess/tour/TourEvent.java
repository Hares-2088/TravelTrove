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
<<<<<<< HEAD:be-traveltrove/src/main/java/com/traveltrove/betraveltrove/dataaccess/tour/TourEvent.java
public class TourEvent {
=======
public class TourEvents {
>>>>>>> f576ce1a2f8d1ab68f7872032570f00efd806719:be-traveltrove/src/main/java/com/traveltrove/betraveltrove/dataaccess/tour/TourEvents.java

    @Id
    private String Id;
    private String tourEventId;
    private Integer seq;
    private String seqDesc;
    private String tourId;
<<<<<<< HEAD:be-traveltrove/src/main/java/com/traveltrove/betraveltrove/dataaccess/tour/TourEvent.java
    private String eventId;
=======
    private String events;
>>>>>>> f576ce1a2f8d1ab68f7872032570f00efd806719:be-traveltrove/src/main/java/com/traveltrove/betraveltrove/dataaccess/tour/TourEvents.java
}
