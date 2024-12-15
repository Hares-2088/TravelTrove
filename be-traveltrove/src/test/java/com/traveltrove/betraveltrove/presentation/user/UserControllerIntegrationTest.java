package com.traveltrove.betraveltrove.presentation.user;

import com.traveltrove.betraveltrove.dataaccess.user.User;
import com.traveltrove.betraveltrove.dataaccess.user.UserRepository;
import com.traveltrove.betraveltrove.presentation.mockserverconfigs.MockServerConfigUserService;
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

    private MockServerConfigUserService mockServerConfigUserService;

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

    @BeforeAll
    public void startServer() {
        mockServerConfigUserService = new MockServerConfigUserService();
        mockServerConfigUserService.startMockServer();

        // Register mock Auth0 API endpoints
        mockServerConfigUserService.registerHandleUserLoginEndpoint(existingUser);
        mockServerConfigUserService.registerSyncUserEndpoint(updatedUser);
        mockServerConfigUserService.registerInvalidUserLoginEndpoint(INVALID_USER_ID);
        mockServerConfigUserService.registerInvalidUserSyncEndpoint(INVALID_USER_ID);
    }

    @BeforeEach
    public void setupRepository() {
        // Clean repository before each test
        StepVerifier.create(userRepository.deleteAll()).verifyComplete();

        // Create and insert the actual User entity
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

    @AfterAll
    public void stopServer() {
        mockServerConfigUserService.stopMockServer();
    }

    @Test
    void whenHandleUserLogin_withValidUserId_thenReturnUserDetails() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .mutateWith(SecurityMockServerConfigurers.mockUser("testuser"))
                .post()
                .uri("/api/v1/users/{userId}/login", existingUser.getUserId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseModel.class)
                .value(user -> Assertions.assertEquals(existingUser.getEmail(), user.getEmail()));
    }

    @Test
    void whenHandleUserLogin_withInvalidUserId_thenReturnNotFound() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .mutateWith(SecurityMockServerConfigurers.mockUser("testuser"))
                .post()
                .uri("/api/v1/users/{userId}/login", INVALID_USER_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("User ID not found: " + INVALID_USER_ID);
    }

    @Test
    void whenSyncUser_withValidUserId_thenReturnUpdatedUserDetails() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .mutateWith(SecurityMockServerConfigurers.mockUser("testuser"))
                .put()
                .uri("/api/v1/users/{userId}/sync", existingUser.getUserId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseModel.class)
                .value(user -> {
                    Assertions.assertEquals(updatedUser.getEmail(), user.getEmail());
                    Assertions.assertEquals(updatedUser.getFirstName(), user.getFirstName());
                    Assertions.assertEquals(updatedUser.getLastName(), user.getLastName());
                });
    }

    @Test
    void whenSyncUser_withInvalidUserId_thenReturnNotFound() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .mutateWith(SecurityMockServerConfigurers.mockUser("testuser"))
                .put()
                .uri("/api/v1/users/{userId}/sync", INVALID_USER_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("User ID not found: " + INVALID_USER_ID);
    }
}
