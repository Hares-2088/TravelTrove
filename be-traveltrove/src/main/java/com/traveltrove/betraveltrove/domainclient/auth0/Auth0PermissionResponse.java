package com.traveltrove.betraveltrove.domainclient.auth0;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Auth0PermissionResponse {

    @JsonProperty("permission_name")
    private String permissionName;

    @JsonProperty("resource_server_identifier")
    private String resourceServerIdentifier;

    @JsonProperty("description")
    private String description;
}
