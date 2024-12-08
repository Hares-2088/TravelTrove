package com.traveltrove.betraveltrove.presentation.city;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "City name cannot be blank")
    private String name;

    @NotBlank(message = "Country id cannot be blank")
    private String countryId;
}
