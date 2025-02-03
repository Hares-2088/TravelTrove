package com.traveltrove.betraveltrove.business.tourpackage;

import com.traveltrove.betraveltrove.dataaccess.tourpackage.Subscription;
import com.traveltrove.betraveltrove.dataaccess.tourpackage.SubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceUnitTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @Test
    void whenSubscribeUser_thenReturnSubscriptionResponse() {
        String userId = "user123";
        String packageId = "package123";

        Subscription subscription = Subscription.builder()
                .userId(userId)
                .packageId(packageId)
                .subscribedAt(LocalDateTime.now())
                .build();

        when(subscriptionRepository.existsByUserIdAndPackageId(userId, packageId))
                .thenReturn(Mono.just(false));
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenReturn(Mono.just(subscription));

        StepVerifier.create(subscriptionService.subscribeUserToPackage(userId, packageId))
                .expectNextMatches(response -> response.getUserId().equals(userId) && response.getPackageId().equals(packageId))
                .verifyComplete();
    }

    @Test
    void whenUserAlreadySubscribed_thenReturnError() {
        String userId = "user123";
        String packageId = "package123";

        when(subscriptionRepository.existsByUserIdAndPackageId(userId, packageId))
                .thenReturn(Mono.just(true));

        StepVerifier.create(subscriptionService.subscribeUserToPackage(userId, packageId))
                .expectErrorMatches(error -> error instanceof IllegalArgumentException &&
                        error.getMessage().equals("User is already subscribed."))
                .verify();
    }

    @Test
    void whenUnsubscribeUser_thenComplete() {
        String userId = "user123";
        String packageId = "package123";

        when(subscriptionRepository.deleteByUserIdAndPackageId(userId, packageId))
                .thenReturn(Mono.empty());

        StepVerifier.create(subscriptionService.unsubscribeUserFromPackage(userId, packageId))
                .verifyComplete();
    }

    @Test
    void whenGetSubscriptionsForUser_thenReturnFlux() {
        String userId = "user123";

        Subscription subscription = Subscription.builder()
                .userId(userId)
                .packageId("package123")
                .subscribedAt(LocalDateTime.now())
                .build();

        when(subscriptionRepository.findByUserId(userId))
                .thenReturn(Flux.just(subscription));

        StepVerifier.create(subscriptionService.getSubscriptionsForUser(userId))
                .expectNextMatches(response -> response.getUserId().equals(userId))
                .verifyComplete();
    }

    @Test
    void whenGetUsersSubscribedToPackage_thenReturnFlux() {
        String packageId = "package123";

        Subscription subscription = Subscription.builder()
                .userId("user123")
                .packageId(packageId)
                .subscribedAt(LocalDateTime.now())
                .build();

        when(subscriptionRepository.findByPackageId(packageId))
                .thenReturn(Flux.just(subscription));

        StepVerifier.create(subscriptionService.getUsersSubscribedToPackage(packageId))
                .expectNextMatches(response -> response.getPackageId().equals(packageId))
                .verifyComplete();
    }
}
