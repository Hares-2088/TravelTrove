package com.traveltrove.betraveltrove.presentation.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserUpdateRequest {
    private String email;
    private String firstName;
    private String lastName;
    private List<String> travelerIds;
}
