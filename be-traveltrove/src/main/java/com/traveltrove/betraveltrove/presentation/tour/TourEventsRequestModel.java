package com.traveltrove.betraveltrove.presentation.tour;

import lombok.Data;

@Data
public class TourEventsRequestModel {
    private int seq;
    private String seqDesc;
    private String tourId; // FK tour ID
    private String events; // FK events ID
}
