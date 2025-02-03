package com.traveltrove.betraveltrove.presentation.tourpackage;

import com.traveltrove.betraveltrove.dataaccess.tourpackage.Subscription;
import com.traveltrove.betraveltrove.dataaccess.tourpackage.SubscriptionRepository;
import org.junit.jupiter.api.*;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SubscriptionControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private final Subscription testSubscription = Subscription.builder()
            .userId("user123")
            .packageId("package123")
            .subscribedAt(LocalDateTime.now())
            .build();

    @BeforeEach
    public void setupDB() {
        Publisher<Subscription> setupDB = subscriptionRepository.deleteAll()
                .thenMany(Flux.just(testSubscription))
                .flatMap(subscriptionRepository::save);

        StepVerifier.create(setupDB).expectNextCount(1).verifyComplete();
    }

    @Test
    void whenSubscribe_thenReturnSubscriptionResponse() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .post()
                .uri("/api/v1/subscriptions/subscribe?userId=user456&packageId=package456")
                .exchange()
                .expectStatus().isOk()
                .expectBody(SubscriptionResponseModel.class)
                .value(response -> Assertions.assertEquals("user456", response.getUserId()));
    }

    @Test
    void whenUnsubscribe_thenReturnNoContent() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .delete()
                .uri("/api/v1/subscriptions/unsubscribe?userId=user123&packageId=package123")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void whenGetUserSubscriptions_thenReturnSubscriptions() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/subscriptions/user/user123")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(SubscriptionResponseModel.class)
                .hasSize(1);
    }

    @Test
    void whenGetPackageSubscribers_thenReturnSubscribers() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/subscriptions/package/package123")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(SubscriptionResponseModel.class)
                .hasSize(1);
    }
}
