package com.traveltrove.betraveltrove.presentation.city;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityResponseModel {
    private String cityId;
    private String name;
    private String countryId;
}
