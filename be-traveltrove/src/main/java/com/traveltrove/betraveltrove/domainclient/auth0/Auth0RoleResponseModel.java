package com.traveltrove.betraveltrove.domainclient.auth0;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Auth0RoleResponseModel {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;
}
