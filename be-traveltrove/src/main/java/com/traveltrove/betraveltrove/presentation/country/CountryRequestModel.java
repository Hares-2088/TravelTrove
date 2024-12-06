package com.traveltrove.betraveltrove.presentation.country;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryRequestModel {

    @NotBlank(message = "Country name cannot be blank")
    private String name;

    @NotBlank(message = "Image URL cannot be blank")
    private String image;
}
