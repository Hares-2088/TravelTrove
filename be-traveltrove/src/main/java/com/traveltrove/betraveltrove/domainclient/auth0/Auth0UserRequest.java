package com.traveltrove.betraveltrove.domainclient.auth0;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Auth0UserRequest {
    private String email;
    private String password;
}