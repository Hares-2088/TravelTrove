package com.traveltrove.betraveltrove.business.user;

import com.traveltrove.betraveltrove.externalservices.auth0.models.Auth0UserResponseModel;
import com.traveltrove.betraveltrove.presentation.user.UserRequestModel;
import com.traveltrove.betraveltrove.presentation.user.UserResponseModel;
import com.traveltrove.betraveltrove.presentation.user.UserUpdateRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserService {

    Mono<UserResponseModel> addUserFromAuth0(String auth0UserId);
    Mono<UserResponseModel> syncUserWithAuth0(String auth0UserId);

    Mono<UserResponseModel> getUser(String userId);
    Flux<UserResponseModel> getAllUsers();
    Mono<UserResponseModel> updateUser(String auth0UserId, UserRequestModel userRequestModel);
    Mono<Void> updateUserRole(String userId, List<String> roleId);
}

