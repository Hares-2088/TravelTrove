package com.traveltrove.betraveltrove.business.user;

import com.traveltrove.betraveltrove.dataaccess.user.User;
import com.traveltrove.betraveltrove.dataaccess.user.UserRepository;
import com.traveltrove.betraveltrove.externalservices.auth0.Auth0Service;
import com.traveltrove.betraveltrove.presentation.user.UserResponseModel;
import com.traveltrove.betraveltrove.presentation.user.UserUpdateRequest;
import com.traveltrove.betraveltrove.utils.entitymodelyutils.UserEntityToModel;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Auth0Service auth0Service;
    private final UserRepository userRepository;

    @Override
    public Mono<UserResponseModel> addUserFromAuth0(String auth0UserId) {
        return auth0Service.getUserById(auth0UserId)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found with Auth0 ID: " + auth0UserId)))
                .flatMap(auth0User ->
                        userRepository.findByUserId(auth0UserId)
                                .switchIfEmpty(
                                        auth0Service.assignRoleToUser(auth0UserId, "rol_bGEYlXT5XYsHGhcQ")
                                                .doOnSuccess(unused -> log.info("Successfully assigned 'Customer' role to User ID: {}", auth0UserId))
                                                .doOnError(error -> log.error("Failed to assign 'Customer' role to User ID: {}", auth0UserId, error))
                                                .then(auth0Service.getUserById(auth0UserId)
                                                        .doOnSuccess(updatedAuth0User -> log.info("Updated Auth0 User Details After Role Assignment: {}", updatedAuth0User))
                                                        .flatMap(updatedAuth0User ->
                                                                userRepository.save(
                                                                        User.builder()
                                                                                .userId(auth0UserId)
                                                                                .email(updatedAuth0User.getEmail())
                                                                                .firstName(updatedAuth0User.getFirstName())
                                                                                .lastName(updatedAuth0User.getLastName())
                                                                                .roles(updatedAuth0User.getRoles())
                                                                                .permissions(updatedAuth0User.getPermissions())
                                                                                .travelerId(UUID.randomUUID().toString())
                                                                                .build()
                                                                ).doOnSuccess(user -> log.info("User successfully created in MongoDB: {}", user))
                                                        )
                                                )
                                )
                )
                .map(UserEntityToModel::toUserResponseModel)
                .doOnSuccess(user -> log.info("Final User Response: {}", user))
                .doOnError(error -> log.error("Error processing user with ID: {}", auth0UserId, error));
    }


    @Override
    public Mono<UserResponseModel> syncUserWithAuth0(String auth0UserId) {
        log.info("Starting user sync process for Auth0 User ID: {}", auth0UserId);

        return auth0Service.getUserById(auth0UserId)
                .doOnSuccess(auth0User -> log.info("Fetched Auth0 User Details: {}", auth0User))
                .flatMap(auth0User -> userRepository.findByUserId(auth0UserId)
                        .flatMap(existingUser -> {
                            log.info("Existing User Found in Database: {}", existingUser);

                            // Update User Fields
                            existingUser.setEmail(auth0User.getEmail());
                            existingUser.setFirstName(auth0User.getFirstName());
                            existingUser.setLastName(auth0User.getLastName());
                            existingUser.setRoles(auth0User.getRoles());
                            existingUser.setPermissions(auth0User.getPermissions());

                            log.info("Updated User Details Before Saving: {}", existingUser);

                            return userRepository.save(existingUser)
                                    .doOnSuccess(updatedUser -> log.info("Successfully Synced User in MongoDB: {}", updatedUser))
                                    .doOnError(error -> log.error("Failed to Save Synced User to MongoDB: {}", error.getMessage()));
                        })
                        .switchIfEmpty(Mono.error(new RuntimeException("User Not Found in Database: " + auth0UserId)))
                )
                .map(UserEntityToModel::toUserResponseModel)
                .doOnSuccess(user -> log.info("Final Synced User Response: {}", user))
                .doOnError(error -> log.error("Error Syncing User with ID {}: {}", auth0UserId, error.getMessage()));
    }

    @Override
    public Mono<UserResponseModel> getUser(String userId) {
        return userRepository.findByUserId(userId)
                .map(UserEntityToModel::toUserResponseModel)
                .doOnSuccess(user -> log.info("Fetched User Details: {}", user))
                .doOnError(error -> log.error("Error fetching user with ID: {}", userId, error));
    }

    @Override
    public Mono<UserResponseModel> updateUserProfile(String userId, UserUpdateRequest updateRequest) {
        // Find existing user
        return userRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found with ID: " + userId)))
                // Update only the fields we allow the user to change
                .flatMap(existingUser -> {
                    if (updateRequest.getEmail() != null) {
                        existingUser.setEmail(updateRequest.getEmail());
                    }
                    if (updateRequest.getFirstName() != null) {
                        existingUser.setFirstName(updateRequest.getFirstName());
                    }
                    if (updateRequest.getLastName() != null) {
                        existingUser.setLastName(updateRequest.getLastName());
                    }
                    if (updateRequest.getTravelerIds() != null) {
                        existingUser.setTravelerIds(updateRequest.getTravelerIds());
                    }

                    // Save & convert to response
                    return userRepository.save(existingUser);
                })
                .map(UserEntityToModel::toUserResponseModel)
                .doOnSuccess(user -> log.info("Successfully updated user profile for userId={}", userId))
                .doOnError(err -> log.error("Error while updating user profile for userId={}: {}", userId, err.getMessage()));
    }

}

