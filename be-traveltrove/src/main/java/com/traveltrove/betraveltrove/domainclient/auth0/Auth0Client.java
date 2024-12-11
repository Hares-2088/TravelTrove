package com.traveltrove.betraveltrove.domainclient.auth0;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Auth0Client {

    private final WebClient webClient = WebClient.create();

    @Value("${auth0.domain}")
    private String domain;

    @Value("${auth0.clientId}")
    private String clientId;

    @Value("${auth0.clientSecret}")
    private String clientSecret;

    @Value("${auth0.audience}")
    private String audience;

    /**
     * Logs Auth0 configuration on initialization
     */
    @PostConstruct
    public void logAuth0Config() {
        log.info("Auth0 Configuration Loaded:");
        log.info(" - Domain: {}", domain);
        log.info(" - Client ID: {}", clientId);
        log.info(" - Client Secret: [PROTECTED]");
        log.info(" - Audience: {}", audience);
    }

    /**
     * Get Access Token from Auth0 Management API
     */
    public Mono<String> getAccessToken() {
        log.info("Requesting Auth0 Management API Access Token...");

        return webClient.post()
                .uri("https://" + domain + "/oauth/token")
                .bodyValue(
                        new Auth0TokenRequest(clientId, clientSecret, audience, "client_credentials")
                )
                .retrieve()
                .bodyToMono(Auth0TokenResponse.class)
                .doOnSuccess(response -> log.info("Access Token retrieved successfully"))
                .doOnError(error -> log.error("Failed to retrieve Access Token", error))
                .map(Auth0TokenResponse::getAccessToken);
    }

    /**
     * Create a New User in Auth0
     */
    public Mono<Auth0UserResponse> createAuth0User(String email, String password) {
        log.info("Creating new Auth0 user with email: {}", email);

        return getAccessToken()
                .flatMap(token -> webClient.post()
                        .uri("https://" + domain + "/api/v2/users")
                        .headers(headers -> headers.setBearerAuth(token))
                        .bodyValue(new Auth0UserRequest(email, password))
                        .retrieve()
                        .bodyToMono(Auth0UserResponse.class)
                        .doOnSuccess(response -> log.info("User created successfully: {}", response))
                        .doOnError(error -> log.error("Failed to create user in Auth0", error))
                );
    }

    /**
     * Find a User in Auth0 by Email
     */
    public Mono<Auth0UserResponse> getAuth0UserByEmail(String email) {
        log.info("Looking up Auth0 user by email: {}", email);

        return getAccessToken()
                .flatMap(token -> webClient.get()
                        .uri("https://" + domain + "/api/v2/users-by-email?email={email}", email)
                        .headers(headers -> headers.setBearerAuth(token))
                        .retrieve()
                        .bodyToMono(Auth0UserResponse[].class)
                        .doOnSuccess(response -> log.info("Lookup successful: {}", (Object) response))
                        .doOnError(error -> log.error("Failed to lookup user by email", error))
                        .map(users -> users.length > 0 ? users[0] : null)
                );
    }

    public Mono<List<String>> getUserRoles(String userId) {
        log.info("Fetching roles for Auth0 user: {}", userId);

        return getAccessToken()
                .flatMap(token -> webClient.get()
                        .uri("https://" + domain + "/api/v2/users/{userId}/roles", userId)
                        .headers(headers -> headers.setBearerAuth(token))
                        .retrieve()
                        .bodyToFlux(Auth0RoleResponse.class)
                        .doOnNext(response -> log.info("Role retrieved: {}", response))
                        .doOnError(error -> log.error("Failed to fetch roles", error))
                        .map(Auth0RoleResponse::getName)
                        .collectList()
                        .doOnSuccess(roles -> log.info("Roles fetched successfully: {}", roles))
                );
    }

    public Mono<List<String>> getUserPermissions(String userId) {
        log.info("Fetching permissions for Auth0 user: {}", userId);

        return getAccessToken()
                .flatMap(token -> webClient.get()
                        .uri("https://" + domain + "/api/v2/users/{userId}/permissions", userId)
                        .headers(headers -> headers.setBearerAuth(token))
                        .retrieve()
                        .bodyToFlux(Auth0PermissionResponse.class)
                        .doOnNext(response -> log.info("Permission retrieved: {}", response))
                        .doOnError(error -> log.error("Failed to fetch permissions", error))
                        .map(Auth0PermissionResponse::getPermissionName)
                        .collectList()
                        .doOnSuccess(permissions -> log.info("Permissions fetched successfully: {}", permissions))
                );
    }

    public Mono<Auth0UserResponse> getAuth0UserByUserId(String userId) {
        log.info("Fetching Auth0 user by ID: {}", userId);

        return getAccessToken()
                .flatMap(token -> webClient.get()
                        .uri("https://" + domain + "/api/v2/users/{userId}", userId)
                        .headers(headers -> headers.setBearerAuth(token))
                        .retrieve()
                        .bodyToMono(Auth0UserResponse.class)
                        .doOnSuccess(response -> log.info("User fetched successfully: {}", response))
                        .doOnError(error -> log.error("Failed to fetch user by ID", error))
                );
    }
}
