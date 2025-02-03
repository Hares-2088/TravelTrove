package com.traveltrove.betraveltrove.dataaccess.tourpackage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

@DataMongoTest
@ActiveProfiles("test")
public class SubscriptionRepositoryIntegrationTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @BeforeEach
    void setup() {
        Subscription testSubscription = Subscription.builder()
                .userId("user123")
                .packageId("package123")
                .subscribedAt(LocalDateTime.now())
                .build();

        StepVerifier.create(subscriptionRepository.save(testSubscription))
                .expectNextMatches(sub -> sub.getUserId().equals("user123"))
                .verifyComplete();
    }

    @AfterEach
    void cleanup() {
        StepVerifier.create(subscriptionRepository.deleteAll()).verifyComplete();
    }

    @Test
    void whenFindByUserId_thenReturnSubscriptions() {
        StepVerifier.create(subscriptionRepository.findByUserId("user123"))
                .expectNextMatches(sub -> sub.getUserId().equals("user123"))
                .verifyComplete();
    }

    @Test
    void whenFindByPackageId_thenReturnSubscribers() {
        StepVerifier.create(subscriptionRepository.findByPackageId("package123"))
                .expectNextMatches(sub -> sub.getPackageId().equals("package123"))
                .verifyComplete();
    }
}
