package com.traveltrove.betraveltrove.presentation.country;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryResponseModel {
    private String countryId;
    private String name;
    private String image;
}