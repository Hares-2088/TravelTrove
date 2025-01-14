package com.traveltrove.betraveltrove.dataaccess.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")

public class User {

    @Id
    private String id;
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
    private List<String> permissions;
    private List<String> other_travelers;
    private String travelerId;
}

