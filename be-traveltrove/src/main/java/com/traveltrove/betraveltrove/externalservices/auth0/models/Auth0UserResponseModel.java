package com.traveltrove.betraveltrove.externalservices.auth0.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Auth0UserResponseModel {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("given_name")
    private String firstName;

    @JsonProperty("family_name")
    private String lastName;
}
