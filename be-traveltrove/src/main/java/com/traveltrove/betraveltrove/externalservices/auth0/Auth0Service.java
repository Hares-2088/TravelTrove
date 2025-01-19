package com.traveltrove.betraveltrove.externalservices.auth0;

import com.traveltrove.betraveltrove.presentation.user.UserResponseModel;
import reactor.core.publisher.Mono;

import java.util.List;

public interface Auth0Service {

    Mono<UserResponseModel> getUserById(String auth0UserId);
    Mono<Void> assignCustomerRoleToUser(String auth0UserId, String roleName);

    Mono<Void> updateUserRole(String auth0UserId, List<String> roleId);

    Mono<Void> removeUserRoles(String auth0UserId, String roleId);

}
