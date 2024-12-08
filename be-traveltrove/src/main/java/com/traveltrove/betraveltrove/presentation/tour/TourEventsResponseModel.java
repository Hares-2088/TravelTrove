package com.traveltrove.betraveltrove.presentation.tour;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class TourEventsResponseModel {
    private String toursEventId;
    private int seq;
    private String seqDesc;
    private String tourId; // FK tour ID
    private String events;

    public TourEventsResponseModel(String toursEventId, int seq, String seqDesc, String tourId, String events) {
    }
}
