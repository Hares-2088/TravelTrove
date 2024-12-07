package com.traveltrove.betraveltrove.presentation.city;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityRequestModel {
    private String name;
    private String countryId;
}
