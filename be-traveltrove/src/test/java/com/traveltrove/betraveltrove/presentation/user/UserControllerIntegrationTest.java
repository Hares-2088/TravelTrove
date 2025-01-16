package com.traveltrove.betraveltrove.presentation.user;

import com.traveltrove.betraveltrove.dataaccess.user.User;
import com.traveltrove.betraveltrove.dataaccess.user.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

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
            .email("admin@traveltrove.com")
            .roles(List.of("Admin"))
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
                .build();

        StepVerifier.create(userRepository.save(existingUserEntity))
                .expectNextMatches(user -> user.getEmail().equals(existingUser.getEmail()))
                .verifyComplete();
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

}
