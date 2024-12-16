package com.traveltrove.betraveltrove.dataaccess.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

@DataMongoTest
@ActiveProfiles("test")
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private String userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID().toString();

        User testUser = User.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .email("testuser@example.com")
                .firstName("Test")
                .lastName("User")
                .roles(List.of("Customer"))
                .permissions(List.of("read:trips"))
                .travelerId(UUID.randomUUID().toString())
                .build();

        StepVerifier.create(userRepository.save(testUser))
                .expectNextMatches(savedUser -> savedUser.getUserId().equals(userId))
                .verifyComplete();
    }

    @AfterEach
    void cleanup() {
        StepVerifier.create(userRepository.deleteAll())
                .verifyComplete();
    }

    @Test
    void whenFindByUserId_withExistingId_thenReturnExistingUser() {
        Mono<User> foundUser = userRepository.findByUserId(userId);

        StepVerifier.create(foundUser)
                .expectNextMatches(user ->
                        user.getUserId().equals(userId) &&
                                user.getEmail().equals("testuser@example.com") &&
                                user.getFirstName().equals("Test") &&
                                user.getLastName().equals("User") &&
                                user.getRoles().contains("Customer")
                )
                .verifyComplete();
    }

    @Test
    void whenFindByUserId_withNonExistingId_thenReturnEmptyMono() {
        Mono<User> foundUser = userRepository.findByUserId("INVALID_ID");

        StepVerifier.create(foundUser)
                .verifyComplete();
    }
}
