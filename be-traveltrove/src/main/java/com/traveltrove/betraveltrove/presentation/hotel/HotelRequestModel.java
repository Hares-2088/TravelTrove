package com.traveltrove.betraveltrove.presentation.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelRequestModel {
    private String name;
    private String cityId;
    private String url;
}
