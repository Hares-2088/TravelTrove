package com.traveltrove.betraveltrove.presentation.user;

import com.traveltrove.betraveltrove.business.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

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

}
