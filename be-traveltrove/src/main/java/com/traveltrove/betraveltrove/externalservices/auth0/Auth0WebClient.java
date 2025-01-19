package com.traveltrove.betraveltrove.externalservices.auth0;

import com.traveltrove.betraveltrove.externalservices.auth0.models.*;
import com.traveltrove.betraveltrove.presentation.user.UserResponseModel;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class Auth0WebClient implements Auth0Service {

    @Value("${auth0.domain}")
    private String domain;

    @Value("${auth0.clientId}")
    private String clientId;

    @Value("${auth0.clientSecret}")
    private String clientSecret;

    @Value("${auth0.audience}")
    private String audience;

    private final WebClient webClient;

    public Auth0WebClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    private Mono<String> getAccessToken() {

        Auth0TokenRequestModel requestModel = new Auth0TokenRequestModel(
                clientId, clientSecret, audience, "client_credentials"
        );

        log.info("Requesting Auth0 Management API Access Token...");

        return webClient.post()
                .uri("https://" + domain + "/oauth/token")
                .header("Content-Type", "application/json")
                .bodyValue(requestModel)
                .exchangeToMono(response -> {
                    log.info("Auth0 Token Request Response Status: {}", response.statusCode());

                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(Auth0TokenResponseModel.class)
                                .doOnNext(tokenResponse -> log.info("Auth0 Access Token Retrieved Successfully"))
                                .map(Auth0TokenResponseModel::getAccessToken);
                    } else {
                        return response.bodyToMono(String.class).flatMap(errorBody -> {
                            log.error("Auth0 Token Request Failed: {}", errorBody);
                            return Mono.error(new RuntimeException("Auth0 Token Request Failed"));
                        });
                    }
                })
                .doOnError(error -> log.error("Failed to retrieve Auth0 Access Token", error));
    }

    @Override
    public Mono<UserResponseModel> getUserById(String auth0UserId) {
        log.info("Fetching Auth0 User Details for User ID: {}", auth0UserId);

        return getAccessToken()
                .flatMap(token -> Mono.defer(() -> {
                    Mono<Auth0UserResponseModel> userMono = fetchUser(auth0UserId, token)
                            .switchIfEmpty(Mono.error(new NotFoundException("User Not Found in Auth0: " + auth0UserId)));

                    Flux<Auth0RoleResponseModel> rolesFlux = fetchRoles(auth0UserId, token);
                    Flux<Auth0PermissionResponseModel> permissionsFlux = fetchPermissions(auth0UserId, token);

                    return Mono.zip(userMono, rolesFlux.collectList(), permissionsFlux.collectList())
                            .map(tuple -> mapToUserResponseModel(auth0UserId, tuple.getT1(), tuple.getT2(), tuple.getT3()));
                }))
                .doOnSuccess(user -> log.info("Successfully Retrieved User Response: {}", user))
                .doOnError(error -> log.error("Failed to retrieve user by ID {}", auth0UserId, error));
    }

    @Override
    public Mono<Void> assignCustomerRoleToUser(String auth0UserId, String roleName) {
        log.info("Assigning Role '{}' to User ID: {}", roleName, auth0UserId);

        return getAccessToken()
                .flatMap(token -> webClient.post()
                        .uri("https://" + domain + "/api/v2/users/" + auth0UserId + "/roles")
                        .headers(headers -> headers.setBearerAuth(token))
                        .bodyValue(new AssignRolesRequestModel(roleName))
                        .retrieve()
                        .toBodilessEntity()
                        .doOnSuccess(response -> log.info("Role '{}' assigned successfully to User ID: {}", roleName, auth0UserId))
                        .doOnError(error -> log.error("Failed to assign role '{}' to User ID: {}", roleName, auth0UserId, error))
                        .then()
                );
    }

    private Mono<Auth0UserResponseModel> fetchUser(String auth0UserId, String token) {
        return webClient.get()
                .uri("https://" + domain + "/api/v2/users/" + auth0UserId)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(Auth0UserResponseModel.class)
                .doOnSuccess(user -> log.info("Fetched Auth0 User with ID {}: {}", auth0UserId, user))
                .switchIfEmpty(Mono.error(new NotFoundException("User Not Found in Auth0: " + auth0UserId)))
                .doOnError(error -> log.error("Failed to fetch Auth0 User with ID: {}", auth0UserId, error));
    }

    private Flux<Auth0RoleResponseModel> fetchRoles(String auth0UserId, String token) {
        return webClient.get()
                .uri("https://" + domain + "/api/v2/users/" + auth0UserId + "/roles")
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToFlux(Auth0RoleResponseModel.class)
                .doOnNext(role -> log.info("Fetched Role for User ID {}: {}", auth0UserId, role))
                .doOnError(error -> log.error("Failed to fetch roles for User ID: {}", auth0UserId, error));
    }

    private Flux<Auth0PermissionResponseModel> fetchPermissions(String auth0UserId, String token) {
        return webClient.get()
                .uri("https://" + domain + "/api/v2/users/" + auth0UserId + "/permissions")
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToFlux(Auth0PermissionResponseModel.class)
                .doOnNext(permission -> log.info("Fetched Permission for User ID {}: {}", auth0UserId, permission))
                .doOnError(error -> log.error("Failed to fetch permissions for User ID: {}", auth0UserId, error));
    }

    private UserResponseModel mapToUserResponseModel(
            String auth0UserId,
            Auth0UserResponseModel auth0User,
            List<Auth0RoleResponseModel> roles,
            List<Auth0PermissionResponseModel> permissions) {

        List<String> roleNames = roles.stream().map(Auth0RoleResponseModel::getName).toList();
        List<String> permissionNames = permissions.stream().map(Auth0PermissionResponseModel::getName).toList();

        log.info("Mapping User Response for User ID {} with Roles and Permissions", auth0UserId);

        return UserResponseModel.builder()
                .userId(auth0User.getUserId())
                .email(auth0User.getEmail())
                .firstName(auth0User.getFirstName())
                .lastName(auth0User.getLastName())
                .roles(roleNames)
                .permissions(permissionNames)
                .build();
    }



    @Override
    public Mono<Void> updateUserRole(String auth0UserId, List<String> roleId) {
        log.info("Updating roles for Auth0 User ID: {} with Roles: {}", auth0UserId, roleId);

        // Request payload for assigning roles
        AssignRolesRequestModel assignRolesRequest = new AssignRolesRequestModel(roleId);

        return getAccessToken()
                .flatMap(token -> {
                    log.info("Access Token: {}", token);
                    log.info("Request Payload: {}", assignRolesRequest);
                    return webClient.post()
                            .uri("https://" + domain + "/api/v2/users/" + auth0UserId + "/roles")
                            .headers(headers -> headers.setBearerAuth(token))
                            .bodyValue(assignRolesRequest)
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, response ->
                                    response.bodyToMono(String.class) // Read error body as a string
                                            .doOnNext(errorBody -> {
                                                log.error("Error from Auth0 API: Status={}, Body={}", response.statusCode(), errorBody);
                                            })
                                            .flatMap(errorBody -> Mono.error(new RuntimeException("Auth0 API Error: " + errorBody)))
                            )
                            .toBodilessEntity()
                            .doOnSuccess(response -> log.info("Roles successfully updated for User ID: {}", auth0UserId))
                            .doOnError(error -> log.error("Failed to update roles for User ID {}: {}", auth0UserId, error.getMessage()))
                            .then();
                });
    }

    @Override
    public Mono<Void> removeUserRoles(String auth0UserId, String roleId) {
        log.info("Removing roles {} from user {}", roleId, auth0UserId);

        AssignRolesRequestModel assignRolesRequest = new AssignRolesRequestModel(roleId);


        return getAccessToken()
                .flatMap(token -> webClient.method(HttpMethod.DELETE) // Explicitly use DELETE method
                        .uri("https://" + domain + "/api/v2/users/" + auth0UserId + "/roles")
                        .headers(headers -> headers.setBearerAuth(token)) // Add Authorization header
                        .bodyValue(assignRolesRequest) // Pass roles in the body
                        .retrieve()
                        .toBodilessEntity() // Expect no response body
                        .doOnSuccess(response -> log.info("Successfully removed roles for user: {}", auth0UserId))
                        .doOnError(error -> log.error("Failed to remove roles for user {}: {}", auth0UserId, error))
                        .then());
    }
}
