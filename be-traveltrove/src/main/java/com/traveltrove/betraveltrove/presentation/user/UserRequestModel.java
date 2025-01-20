package com.traveltrove.betraveltrove.presentation.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserRequestModel {
    private String email;
    private String firstName;
    private String lastName;
//    private List<String> roles;
//    private List<String> permissions;
    private List<String> travelerIds;
}
