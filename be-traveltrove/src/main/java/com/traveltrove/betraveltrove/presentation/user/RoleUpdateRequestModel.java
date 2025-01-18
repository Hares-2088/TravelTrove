package com.traveltrove.betraveltrove.presentation.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RoleUpdateRequestModel {
    @NotEmpty
    private List<String> roles;

    public RoleUpdateRequestModel(List<String> roles) {
        this.roles = roles;
    }
}
