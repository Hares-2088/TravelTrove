package com.traveltrove.betraveltrove.domainclient.auth0;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignRolesRequestModel {
    private List<String> roles;

    public AssignRolesRequestModel(String roleName) {
        this.roles = List.of(roleName);
    }
}

