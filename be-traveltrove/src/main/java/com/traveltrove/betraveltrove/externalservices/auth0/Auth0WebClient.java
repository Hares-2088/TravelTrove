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
import java.util.Map;

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
        log.info("Assigning roles {} to user {}", roleId, auth0UserId);

        return getAccessToken()
                .flatMap(token -> {
                    // Wrap the role IDs in an object with a "roles" key
                    Map<String, List<String>> payload = Map.of("roles", roleId);
                    log.info("Payload for role assignment: {}", payload);

                    return webClient.post()
                            .uri("https://" + domain + "/api/v2/users/" + auth0UserId + "/roles")
                            .headers(headers -> headers.setBearerAuth(token))
                            .bodyValue(payload) // Send the correct payload format
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, response ->
                                    response.bodyToMono(String.class)
                                            .doOnNext(errorBody -> log.error("Error from Auth0 API: {}", errorBody))
                                            .flatMap(errorBody -> Mono.error(new RuntimeException("Auth0 API Error: " + errorBody)))
                            )
                            .toBodilessEntity()
                            .doOnSuccess(response -> log.info("Successfully assigned roles for user: {}", auth0UserId))
                            .doOnError(error -> log.error("Failed to assign roles for user {}: {}", auth0UserId, error.getMessage()))
                            .then();
                });
    }

    @Override
    public Mono<Void> removeUserRoles(String auth0UserId, List<String> roleId) {
        log.info("Removing roles {} from user {}", roleId, auth0UserId);

        return getAccessToken()
                .flatMap(token -> {
                    // Ensure role IDs are being sent, not role names
                    Map<String, List<String>> payload = Map.of("roles", roleId);
                    log.info("Payload for role removal: {}", payload);

                    return webClient.method(HttpMethod.DELETE)
                            .uri("https://" + domain + "/api/v2/users/" + auth0UserId + "/roles")
                            .headers(headers -> headers.setBearerAuth(token))
                            .bodyValue(payload) // Send role IDs, not names
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, response ->
                                    response.bodyToMono(String.class)
                                            .doOnNext(errorBody -> log.error("Error from Auth0 API: {}", errorBody))
                                            .flatMap(errorBody -> Mono.error(new RuntimeException("Auth0 API Error: " + errorBody)))
                            )
                            .toBodilessEntity()
                            .doOnSuccess(response -> log.info("Successfully removed roles for user: {}", auth0UserId))
                            .doOnError(error -> log.error("Failed to remove roles for user {}: {}", auth0UserId, error.getMessage()))
                            .then();
                });
    }

    @Override
    public Flux<Auth0UserResponseModel> fetchUsersFromAuth0(String token, int page, int perPage) {
        log.info("Fetching Auth0 users - Page: {}, Per Page: {}", page, perPage);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(domain)
                        .path("/api/v2/users")
                        .queryParam("page", page)
                        .queryParam("per_page", perPage)
                        .build())
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToFlux(Auth0UserResponseModel.class)
                .collectList()
                .flatMapMany(users -> {
                    if (users.isEmpty()) {
                        return Flux.empty(); // No more users to fetch
                    }
                    return Flux.fromIterable(users)
                            .concatWith(fetchUsersFromAuth0(token, page + 1, perPage)); // Recursively fetch the next page
                });
    }

    @Override
    public Flux<Auth0UserResponseModel> getAllUsersFromAuth0() {
        log.info("Fetching all users from Auth0...");

        return getAccessToken()
                .flatMapMany(token -> fetchUsersFromAuth0(token, 0, 100)) // Start from page 0, fetch 100 users per page
                .doOnNext(user -> log.info("Fetched User from Auth0: {}", user))
                .doOnError(error -> log.error("Error fetching users from Auth0: {}", error));
    }
}

//    @Override
//    public Flux<Auth0UserResponseModel> getAllUsers() {
//        log.info("Fetching all Auth0 users...");
//
//        return getAccessToken()
//                .flatMapMany(token -> webClient.get()
//                        .uri("https://" + domain + "/api/v2/users")
//                        .headers(headers -> headers.setBearerAuth(token))
//                        .retrieve()
//                        .bodyToFlux(Auth0UserResponseModel.class)
//                        .doOnNext(user -> log.info("Fetched Auth0 User: {}", user))
//                        .doOnError(error -> log.error("Failed to fetch all users", error))
//                );    }


