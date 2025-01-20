package com.traveltrove.betraveltrove.presentation.user;

import com.traveltrove.betraveltrove.business.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
}
