package com.traveltrove.betraveltrove.presentation.tour;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourEventRequestModel {
    private Integer seq;
    private String seqDesc;
    private String tourId;
    private String eventId;
    private String hotelId;
}
