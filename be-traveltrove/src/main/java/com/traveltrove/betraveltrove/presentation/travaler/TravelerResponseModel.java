package com.traveltrove.betraveltrove.presentation.travaler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelerResponseModel {

    private String travelerId;
    private Integer seq;
    private String firstName;
    private String lastName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String email;
    private String countryId;
}