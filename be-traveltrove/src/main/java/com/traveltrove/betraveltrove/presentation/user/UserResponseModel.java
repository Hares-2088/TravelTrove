package com.traveltrove.betraveltrove.presentation.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseModel {
    private String userId;        // Auth0's "sub"
    private String email;         // Auth0 + Local
    private String firstName;     // Local
    private String lastName;      // Local
    private List<String> roles;   // From Auth0
    private List<String> permissions; // From Auth0
    private String travelerId;    // From Local User Entity
}
