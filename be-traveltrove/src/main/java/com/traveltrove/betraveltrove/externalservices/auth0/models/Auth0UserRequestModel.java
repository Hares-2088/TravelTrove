package com.traveltrove.betraveltrove.externalservices.auth0.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Auth0UserRequestModel {

    private String email;

    private String password;

    @JsonProperty("given_name")
    private String firstName;

    @JsonProperty("family_name")
    private String lastName;

    private String connection = "Username-Password-Authentication";

    @JsonProperty("verify_email")
    private boolean verifyEmail = true;
}
