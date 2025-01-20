package com.traveltrove.betraveltrove.presentation.user;

import com.traveltrove.betraveltrove.business.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public Mono<ResponseEntity<UserResponseModel>> getUserById(@PathVariable String userId) {
        return userService.getUser(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnSuccess(response -> log.info("Fetched user details for ID: {}", userId))
                .doOnError(error -> log.error("Error fetching user details for ID: {}", userId, error));
    }

    @PostMapping("/{userId}/login")
    public Mono<ResponseEntity<UserResponseModel>> handleUserLogin(@PathVariable String userId) {
        return userService.addUserFromAuth0(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}/sync")
    public Mono<ResponseEntity<UserResponseModel>> syncUser(@PathVariable String userId) {
        return userService.syncUserWithAuth0(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<UserResponseModel> getAllUser() {
        return userService.getAllUsers();
    }

    @PostMapping("/{userId}/roles")
    public Mono<ResponseEntity<String>> updateUserRole(
            @PathVariable String userId,
            @RequestBody RoleUpdateRequestModel roleUpdateRequest) {
        log.info("Received request to update roles for user: {}", userId);
        log.info("Request body: {}", roleUpdateRequest.getRoles()); // Log the roles field

        return userService.updateUserRole(userId, roleUpdateRequest.getRoles())
//                .then(userService.syncUserWithAuth0(userId))// sync user from auth0 locally
                //
                .thenReturn(ResponseEntity.ok("Roles updated successfully"))
                .onErrorResume(error -> {
                    log.error("Failed to update roles for user {}: {}", userId, error.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Failed to update roles: " + error.getMessage()));
                });
    }

    @PutMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<UserResponseModel>> updateUser(
            @PathVariable String userId,
            @RequestBody UserRequestModel userRequestModel) {
        return userService.updateUser(userId, userRequestModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/auth0")
    public Flux<UserResponseModel> getAllUsersFromAuth0() {
        log.info("Fetching all users directly from Auth0...");
        return userService.getAllUsersFromAuth0()
                .doOnComplete(() -> log.info("Successfully fetched all users from Auth0"))
                .doOnError(error -> log.error("Error fetching users from Auth0", error));
    }

}