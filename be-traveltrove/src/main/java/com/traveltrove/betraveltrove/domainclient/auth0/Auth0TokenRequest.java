package com.traveltrove.betraveltrove.domainclient.auth0;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Auth0TokenRequest {
    private String client_id;
    private String client_secret;
    private String audience;
    private String grant_type;
}
