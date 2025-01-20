package com.traveltrove.betraveltrove.business.user;

import com.traveltrove.betraveltrove.business.notification.NotificationService;
import com.traveltrove.betraveltrove.dataaccess.user.User;
import com.traveltrove.betraveltrove.dataaccess.user.UserRepository;
import com.traveltrove.betraveltrove.externalservices.auth0.Auth0Service;
import com.traveltrove.betraveltrove.presentation.user.UserRequestModel;
import com.traveltrove.betraveltrove.presentation.user.UserResponseModel;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Auth0Service auth0Service;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private UserServiceImpl userService;

//    @Test
//    public void whenAddUserFromAuth0_thenReturnUserResponseModel() {
//        String auth0UserId = UUID.randomUUID().toString();
//        UserResponseModel auth0User = UserResponseModel.builder()
//                .userId(auth0UserId)
//                .email("test@example.com")
//                .firstName("John")
//                .lastName("Doe")
//                .roles(List.of("Customer"))
//                .permissions(List.of("read:countries"))
//                .build();
//
//        User savedUser = User.builder()
//                .userId(auth0UserId)
//                .email("test@example.com")
//                .firstName("John")
//                .lastName("Doe")
//                .roles(List.of("Customer"))
//                .permissions(List.of("read:countries"))
//                .travelerId(UUID.randomUUID().toString())
//                .build();
//
//        when(auth0Service.getUserById(auth0UserId)).thenReturn(Mono.just(auth0User));
//        when(auth0Service.assignRoleToUser(auth0UserId, "rol_bGEYlXT5XYsHGhcQ")).thenReturn(Mono.empty());
//        when(userRepository.findByUserId(auth0UserId)).thenReturn(Mono.empty());
//        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));
//
//        StepVerifier.create(userService.addUserFromAuth0(auth0UserId))
//                .expectNextMatches(response ->
//                        response.getUserId().equals(auth0UserId) &&
//                                response.getEmail().equals("test@example.com") &&
//                                response.getRoles().contains("Customer")
//                )
//                .verifyComplete();
//    }

    @Test
    public void whenSyncUserWithAuth0_thenReturnUpdatedUserResponseModel() {
        String auth0UserId = UUID.randomUUID().toString();
        User existingUser = User.builder()
                .userId(auth0UserId)
                .email("old@example.com")
                .firstName("Old")
                .lastName("Name")
                .roles(List.of("Customer"))
                .permissions(List.of("read:countries"))
                .travelerId(UUID.randomUUID().toString())
                .build();

        UserResponseModel auth0User = UserResponseModel.builder()
                .userId(auth0UserId)
                .email("new@example.com")
                .firstName("New")
                .lastName("Name")
                .roles(List.of("Customer", "Admin"))
                .permissions(List.of("read:countries", "write:countries"))
                .build();

        User updatedUser = User.builder()
                .userId(auth0UserId)
                .email("new@example.com")
                .firstName("New")
                .lastName("Name")
                .roles(List.of("Customer", "Admin"))
                .permissions(List.of("read:countries", "write:countries"))
                .travelerId(existingUser.getTravelerId())
                .build();

        when(auth0Service.getUserById(auth0UserId)).thenReturn(Mono.just(auth0User));
        when(userRepository.findByUserId(auth0UserId)).thenReturn(Mono.just(existingUser));
        when(userRepository.save(existingUser)).thenReturn(Mono.just(updatedUser));

        StepVerifier.create(userService.syncUserWithAuth0(auth0UserId))
                .expectNextMatches(response ->
                        response.getEmail().equals("new@example.com") &&
                                response.getRoles().contains("Admin")
                )
                .verifyComplete();
    }

    @Test
    public void whenSyncUserWithAuth0_withNonExistingUser_thenThrowError() {
        String auth0UserId = UUID.randomUUID().toString();
        UserResponseModel auth0User = UserResponseModel.builder()
                .userId(auth0UserId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .roles(List.of("Customer"))
                .permissions(List.of("read:countries"))
                .build();

        when(auth0Service.getUserById(auth0UserId)).thenReturn(Mono.just(auth0User));
        when(userRepository.findByUserId(auth0UserId)).thenReturn(Mono.empty());

        StepVerifier.create(userService.syncUserWithAuth0(auth0UserId))
                .expectErrorMatches(error -> error instanceof RuntimeException &&
                        error.getMessage().contains("User Not Found in Database"))
                .verify();
    }

    @Test
    public void whenGetUser_withValidUserId_thenReturnUserResponseModel() {
        String userId = "89f75581-5112-4f2c-a85e-ecc2773d9802";

        User existingUser = User.builder()
                .userId(userId)
                .email("user@example.com")
                .firstName("Max")
                .lastName("Power")
                .roles(List.of("Admin"))
                .permissions(List.of("read:users"))
                .travelerId(UUID.randomUUID().toString())
                .build();

        UserResponseModel expectedResponse = UserResponseModel.builder()
                .userId(userId)
                .email("user@example.com")
                .firstName("Max")
                .lastName("Power")
                .roles(List.of("Admin"))
                .permissions(List.of("read:users"))
                .build();

        when(userRepository.findByUserId(userId)).thenReturn(Mono.just(existingUser));

        StepVerifier.create(userService.getUser(userId))
                .expectNextMatches(response ->
                        response.getUserId().equals(expectedResponse.getUserId()) &&
                                response.getEmail().equals(expectedResponse.getEmail()) &&
                                response.getFirstName().equals(expectedResponse.getFirstName()) &&
                                response.getLastName().equals(expectedResponse.getLastName()) &&
                                response.getRoles().equals(expectedResponse.getRoles()) &&
                                response.getPermissions().equals(expectedResponse.getPermissions()))
                .verifyComplete();

        verify(userRepository, times(1)).findByUserId(userId);
    }


    @Test
    void whenUpdateUserLocally_withValidUserId_thenReturnUpdatedUserDetails() {
        UserRequestModel userUpdateRequest = UserRequestModel.builder()
                .email("newemail@example.com")
                .firstName("NewFirstName")
                .lastName("NewLastName")
                .roles(List.of("rol_bGEYlXT5XYsHGhcQ"))
                .permissions(List.of(""))
                .build();

        User existingUser = User.builder()
                .userId("auth0%7C675f4bb4e184fd643a8ed903")
                .email("mia.moore@example.com")
                .firstName("Mia")
                .lastName("Moore")
                .roles(List.of("customer"))
                .permissions(List.of(""))
                .travelerId("4a9adfcc-cfc9-4c25-908b-031bdeb2ab31")
                .build();

        User updatedUser = User.builder()
                .userId(existingUser.getUserId())
                .email("newemail@example.com")
                .firstName("NewFirstName")
                .lastName("NewLastName")
                .roles(List.of( "customer"))
                .permissions(List.of(""))
                .travelerId(existingUser.getTravelerId())
                .build();

        when(userRepository.findByUserId(existingUser.getUserId())).thenReturn(Mono.just(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(updatedUser));

        StepVerifier.create(userService.updateUser(existingUser.getUserId(), userUpdateRequest))
                .expectNextMatches(response ->
                        response.getEmail().equals("newemail@example.com") &&
                                response.getFirstName().equals("NewFirstName") &&
                                response.getLastName().equals("NewLastName") &&
                                response.getRoles().equals(List.of("customer")) &&
                                response.getPermissions().equals(List.of("")))
                .verifyComplete();
    }

    @Test
    void whenUpdateUserLocally_withInvalidUserId_thenThrowNotFoundException() {
        UserRequestModel userUpdateRequest = UserRequestModel.builder()
                .email("newemail@example.com")
                .firstName("NewFirstName")
                .lastName("NewLastName")
                .roles(List.of("Admin", "Customer"))
                .permissions(List.of("read:countries", "write:countries"))
                .build();

        when(userRepository.findByUserId("invalid-user-id")).thenReturn(Mono.empty());

        StepVerifier.create(userService.updateUser("invalid-user-id", userUpdateRequest))
                .expectErrorMatches(error -> error instanceof NotFoundException &&
                        error.getMessage().contains("User not found with Auth0 ID: invalid-user-id"))
                .verify();
    }

}
