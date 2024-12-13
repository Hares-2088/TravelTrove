package com.traveltrove.betraveltrove.domainclient.auth0;

import com.traveltrove.betraveltrove.presentation.user.UserResponseModel;
import reactor.core.publisher.Mono;

public interface Auth0Client {

    Mono<UserResponseModel> getUserById(String auth0UserId);
    Mono<Void> assignRoleToUser(String auth0UserId, String roleName);
}
