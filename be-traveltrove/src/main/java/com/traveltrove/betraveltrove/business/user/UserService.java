package com.traveltrove.betraveltrove.business.user;

import com.traveltrove.betraveltrove.presentation.user.UserResponseModel;
import com.traveltrove.betraveltrove.presentation.user.UserUpdateRequest;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserResponseModel> addUserFromAuth0(String auth0UserId);
    Mono<UserResponseModel> syncUserWithAuth0(String auth0UserId);

    Mono<UserResponseModel> getUser(String userId);
    Mono<UserResponseModel> updateUserProfile(String userId, UserUpdateRequest updateRequest);

}

