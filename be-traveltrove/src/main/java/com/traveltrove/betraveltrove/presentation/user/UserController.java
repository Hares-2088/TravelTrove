package com.traveltrove.betraveltrove.presentation.user;

import com.traveltrove.betraveltrove.business.user.UserService;
import com.traveltrove.betraveltrove.dataaccess.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Mono<User> registerUser(@RequestBody User user) {
        return userService.registerUserManually(user);
    }

    @GetMapping("/profile")
    public Mono<User> getUserProfile(JwtAuthenticationToken auth) {
        var jwt = auth.getToken();
        return userService.syncUserAfterAuth0Login(
                jwt.getSubject(),              // Auth0's "sub"
                jwt.getClaim("email"),
                jwt.getClaim("given_name"),
                jwt.getClaim("family_name")
        );
    }

    @GetMapping("/test/{userId}")
    public Mono<User> getUserById(@PathVariable String userId) {
        return userService.syncUserAfterAuth0Login(userId, null, null, null)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")));
    }

    @GetMapping
    public Flux<UserResponseModel> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public Mono<UserResponseModel> getUserByUserId(@PathVariable String userId) {
        return userService.getUserByUserId(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")));
    }

}
