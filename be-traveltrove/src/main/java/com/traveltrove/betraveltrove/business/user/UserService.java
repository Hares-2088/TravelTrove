package com.traveltrove.betraveltrove.business.user;

import com.traveltrove.betraveltrove.dataaccess.user.User;
import com.traveltrove.betraveltrove.presentation.user.UserResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    // Sync user after Auth0 login
    Mono<User> syncUserAfterAuth0Login(String userId, String email, String firstName, String lastName);

    // Register user manually
    Mono<User> registerUserManually(User user);

    Mono<UserResponseModel> getUserByUserId(String userId);

    Flux<UserResponseModel> getAllUsers();
}

