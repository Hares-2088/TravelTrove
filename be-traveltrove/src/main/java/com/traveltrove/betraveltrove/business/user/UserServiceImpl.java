package com.traveltrove.betraveltrove.business.user;

import com.traveltrove.betraveltrove.business.notification.NotificationService;
import com.traveltrove.betraveltrove.dataaccess.user.User;
import com.traveltrove.betraveltrove.dataaccess.user.UserRepository;
import com.traveltrove.betraveltrove.externalservices.auth0.Auth0Service;

import com.traveltrove.betraveltrove.presentation.user.UserRequestModel;
import com.traveltrove.betraveltrove.presentation.user.UserResponseModel;
import com.traveltrove.betraveltrove.utils.entitymodelyutils.UserEntityToModel;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Auth0Service auth0Service;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Value("${frontend.domain}")
    private String baseUrl;

    @Override
    public Mono<UserResponseModel> addUserFromAuth0(String auth0UserId) {
        return auth0Service.getUserById(auth0UserId)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found with Auth0 ID: " + auth0UserId)))
                .flatMap(auth0User ->
                        userRepository.findByUserId(auth0UserId)
                                .switchIfEmpty(
                                        auth0Service.assignCustomerRoleToUser(auth0UserId, "rol_bGEYlXT5XYsHGhcQ")
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
                                                                )
                                                                        .doOnSuccess(user -> {
                                                                            log.info("User successfully created in MongoDB: {}", user);

                                                                            String templateName = "welcome-email.html";
                                                                            String editProfileLink = String.format("%s/profile/create", baseUrl);
                                                                            notificationService.sendEmail(
                                                                                    user.getEmail(),
                                                                                    "Welcome to Travel Trove!",
                                                                                    templateName,
                                                                                    user.getFirstName(),
                                                                                    editProfileLink
                                                                            );
                                                                        })
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
    public Flux<UserResponseModel> getAllUsers() {
        return userRepository.findAll()
                .map(UserEntityToModel::toUserResponseModel)
                .doOnNext(user -> log.info("Fetched User Details: {}", user))
                .doOnError(error -> log.error("Error fetching users: {}", error));
    }

    @Override
    public Mono<UserResponseModel> updateUser(String auth0UserId, UserRequestModel userRequestModel) {
        log.info("Starting update process for user with Auth0 ID: {}", auth0UserId);

        return userRepository.findByUserId(auth0UserId)
                .flatMap(existingUser -> {
                    // Update local user details
                    existingUser.setEmail(userRequestModel.getEmail());
                    existingUser.setFirstName(userRequestModel.getFirstName());
                    existingUser.setLastName(userRequestModel.getLastName());
                    existingUser.setPermissions(userRequestModel.getPermissions());
                    existingUser.setRoles(userRequestModel.getRoles());

                    return userRepository.save(existingUser)
                            .map(UserEntityToModel::toUserResponseModel)
                            .doOnSuccess(user -> log.info("Successfully updated user details: {}", user))
                            .doOnError(error -> log.error("Failed to update user details: {}", error));

                })
                .switchIfEmpty(Mono.error(new NotFoundException("User not found with Auth0 ID: " + auth0UserId)));
    }

    @Override
    public Mono<UserResponseModel> createNewUser(UserResponseModel userResponseModel) {
        return null;
    }

    @Override
    public Mono<Void> updateUserRole(String userId, List<String> roleId) {
        log.info("Updating role for user: {}", userId);


        log.info("Assigning roles: {} to user: {}", roleId, userId);
        // Update the roles locally in the database
        Mono<User> localUpdate = userRepository.findByUserId(userId)
                .flatMap(user -> {
                    user.setRoles(roleId); // Update roles locally
                    return userRepository.save(user);
                })
                .doOnSuccess(updatedUser -> log.info("User roles updated locally: {}", updatedUser))
                .doOnError(error -> log.error("Failed to update user roles locally: {}", error.getMessage()));

        // Update the roles in Auth0
        Mono<Void> auth0Update = auth0Service.updateUserRole(userId, roleId);

        // Combine both updates
        return localUpdate.then(auth0Update)
                .doOnSuccess(unused -> log.info("Roles updated successfully for user: {}", userId))
                .doOnError(error -> log.error("Failed to update roles for user: {}", error.getMessage()));
    }
}

