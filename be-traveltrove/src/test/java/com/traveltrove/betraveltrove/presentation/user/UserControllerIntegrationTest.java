package com.traveltrove.betraveltrove.presentation.user;

import com.traveltrove.betraveltrove.dataaccess.user.User;
import com.traveltrove.betraveltrove.dataaccess.user.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;


    private final String INVALID_USER_ID = "invalid-user-id";

    private final UserResponseModel existingUser = UserResponseModel.builder()
            .userId("auth0|675e3886e184fd643a8ed5aa")
            .firstName("Hello")
            .lastName("Goodbye")
            .email("admin@traveltrove.com")
            .roles(List.of("Admin"))
            .permissions(List.of("read:notification"))
            .travelerId("d572a38b-7dec-4064-8c29-839633ea238e")
            .build();

    private final UserResponseModel updatedUser = UserResponseModel.builder()
            .userId(existingUser.getUserId())
            .email("admin@traveltrove.com")
            .roles(List.of("Admin", "Customer"))
            .permissions(List.of("write:trips"))
            .build();

    @BeforeEach
    public void setupRepository() {
        StepVerifier.create(userRepository.deleteAll()).verifyComplete();

        User existingUserEntity = User.builder()
                .userId(existingUser.getUserId())
                .email(existingUser.getEmail())
                .firstName(existingUser.getFirstName())
                .lastName(existingUser.getLastName())
                .roles(existingUser.getRoles())
                .permissions(existingUser.getPermissions())
                .travelerId(existingUser.getTravelerId())
                .build();

        StepVerifier.create(userRepository.save(existingUserEntity))
                .expectNextMatches(user -> user.getEmail().equals(existingUser.getEmail()))
                .verifyComplete();
    }

    @Test
    void whenHandleGetUser_withValidUserId_thenReturnUserDetails() {
        webTestClient.get()
                .uri("/api/v1/users/{userId}", existingUser.getUserId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseModel.class)
                .value(user -> StepVerifier.create(Mono.just(user))
                        .expectNextMatches(u -> u.getEmail().equals(existingUser.getEmail()))
                        .verifyComplete());
    }

    @Test
    void whenHandleGetUser_withInvalidUserId_thenReturnNotFound() {
        webTestClient.get()
                .uri("/api/v1/users/{userId}", INVALID_USER_ID)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenHandleUserLogin_withValidUserId_thenReturnUserDetails() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .mutateWith(SecurityMockServerConfigurers.mockUser("TestUser"))
                .post()
                .uri("/api/v1/users/{userId}/login", existingUser.getUserId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseModel.class)
                .value(user -> StepVerifier.create(Mono.just(user))
                        .expectNextMatches(u -> u.getEmail().equals(existingUser.getEmail()))
                        .verifyComplete());
    }

    @Test
    void whenSyncUser_withValidUserId_thenReturnUpdatedUserDetails() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .mutateWith(SecurityMockServerConfigurers.mockUser("TestUser"))
                .put()
                .uri("/api/v1/users/{userId}/sync", existingUser.getUserId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseModel.class)
                .value(user -> StepVerifier.create(Mono.just(user))
                        .expectNextMatches(u -> u.getEmail().equals(updatedUser.getEmail()))
                        .verifyComplete());
    }

    @Test
    void whenAccessProtectedEndpoint_withoutAuthentication_thenUnauthorized() {
        webTestClient.get()
                .uri("/api/v1/users/protected")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenAccessProtectedEndpoint_withAuthentication_thenSuccess() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .mutateWith(SecurityMockServerConfigurers.mockUser("TestUser"))
                .post()
                .uri("/api/v1/users/{userId}/login", existingUser.getUserId())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void whenGetUserById_withValidUserId_thenReturnUserDetails() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .mutateWith(SecurityMockServerConfigurers.mockUser("TestUser"))
                .get()
                .uri("/api/v1/users/{userId}", existingUser.getUserId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseModel.class)
                .value(user -> StepVerifier.create(Mono.just(user))
                        .expectNextMatches(u ->
                                u.getUserId().equals(existingUser.getUserId()) &&
                                        u.getEmail().equals(existingUser.getEmail()) &&
                                        u.getRoles().equals(existingUser.getRoles()) &&
                                        u.getFirstName().equals(existingUser.getFirstName()) &&
                                        u.getLastName().equals(existingUser.getLastName()) &&
                                        u.getTravelerId().equals(existingUser.getTravelerId()) &&
                                        u.getPermissions().equals(existingUser.getPermissions())
                        )
                        .expectNextMatches(u -> u.getEmail().equals(existingUser.getEmail()))
                        .verifyComplete());
    }

    @Test
    void whenGetUserById_withInvalidUserId_thenReturnNotFound() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .mutateWith(SecurityMockServerConfigurers.mockUser("TestUser"))
                .get()
                .uri("/api/v1/users/{userId}", INVALID_USER_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .isEmpty();
    }


    @Test
    void whenUpdateUserRole_withValidUserId_thenReturnSuccess() {
        RoleUpdateRequestModel roleUpdateRequest = new RoleUpdateRequestModel(List.of("rol_bGEYlXT5XYsHGhcQ")); //one of the roles from Auth0

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .mutateWith(SecurityMockServerConfigurers.mockUser("TestUser"))
                .post()
                .uri("/api/v1/users/{userId}/roles", existingUser.getUserId())
                .bodyValue(roleUpdateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Roles updated successfully");
    }

    @Test
    void whenUpdateUserRole_withInvalidUserId_thenReturnBadRequest() {
        RoleUpdateRequestModel roleUpdateRequest = new RoleUpdateRequestModel(List.of("Admin", "Customer"));

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .mutateWith(SecurityMockServerConfigurers.mockUser("TestUser"))
                .post()
                .uri("/api/v1/users/{userId}/roles", INVALID_USER_ID)
                .bodyValue(roleUpdateRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(errorMessage -> StepVerifier.create(Mono.just(errorMessage))
                        .expectNextMatches(msg -> msg.contains("Failed to update roles"))
                        .verifyComplete());
    }
}

