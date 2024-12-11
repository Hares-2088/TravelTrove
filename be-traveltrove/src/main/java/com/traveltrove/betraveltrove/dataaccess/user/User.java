package com.traveltrove.betraveltrove.dataaccess.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")

public class User {

    @Id
    private String id;
    private String userId;   // Auth0's "sub" claim
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String travelerId;
}

