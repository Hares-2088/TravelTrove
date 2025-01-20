package com.traveltrove.betraveltrove.presentation.user;

import lombok.*;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoleRequestModel {
    private List<String> roles; // Auth0 expects this exact field name


}
