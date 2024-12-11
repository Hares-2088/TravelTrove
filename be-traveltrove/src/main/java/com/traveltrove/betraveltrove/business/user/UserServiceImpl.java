package com.traveltrove.betraveltrove.business.user;

import com.traveltrove.betraveltrove.dataaccess.user.User;
import com.traveltrove.betraveltrove.dataaccess.user.UserRepository;
import com.traveltrove.betraveltrove.domainclient.auth0.Auth0Client;
import com.traveltrove.betraveltrove.presentation.user.UserResponseModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Auth0Client auth0Client;

    /**
     * Sync User After Auth0 Login
     */
    @Override
    public Mono<User> syncUserAfterAuth0Login(String userId, String email, String firstName, String lastName) {
        return userRepository.findByUserId(userId)
                .switchIfEmpty(
                        userRepository.save(
                                new User(null, userId, email, firstName, lastName, null, null)
                        )
                );
    }

    /**
     * Register User Manually and Sync with Auth0
     */
    @Override
    public Mono<User> registerUserManually(User user) {
        return auth0Client.createAuth0User(user.getEmail(), user.getPassword())
                .flatMap(auth0User -> {
                    user.setUserId(auth0User.getUserId());
                    // If travelerId is null, link to a new Traveler entity here if applicable
                    if (user.getTravelerId() == null) {
                        user.setTravelerId(generateTravelerId());
                    }
                    return userRepository.save(user);
                });
    }

    @Override
    public Mono<UserResponseModel> getUserByUserId(String userId) {
        return Mono.zip(
                userRepository.findByUserId(userId),
                auth0Client.getAuth0UserByUserId(userId),
                auth0Client.getUserRoles(userId),
                auth0Client.getUserPermissions(userId)
        ).map(tuple -> new UserResponseModel(
                tuple.getT1().getUserId(),
                tuple.getT1().getEmail(),
                tuple.getT1().getFirstName(),
                tuple.getT1().getLastName(),
                tuple.getT3(),              // Roles from Auth0
                tuple.getT4(),              // Permissions from Auth0
                tuple.getT1().getTravelerId()
        ));
    }

    @Override
    public Flux<UserResponseModel> getAllUsers() {
        return userRepository.findAll()
                .flatMap(user -> getUserByUserId(user.getUserId()));
    }
    // Simulate Traveler ID generation if needed
    private String generateTravelerId() {
        return "testing-traveler-" + System.currentTimeMillis();
    }
}
