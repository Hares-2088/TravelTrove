package com.traveltrove.betraveltrove.presentation.tour;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourEventResponseModel {
    private String tourEventId;
    private Integer seq;
    private String seqDesc;
    private String tourId;
    private String eventId;
}
