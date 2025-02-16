package com.traveltrove.betraveltrove.business.tourpackage;

import com.traveltrove.betraveltrove.dataaccess.tourpackage.Subscription;
import com.traveltrove.betraveltrove.dataaccess.tourpackage.SubscriptionRepository;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageResponseModel;
import com.traveltrove.betraveltrove.presentation.tourpackage.SubscriptionResponseModel;
import com.traveltrove.betraveltrove.utils.entitymodelyutils.SubscriptionEntityModelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PackageService packageService;
    @Override
    public Mono<SubscriptionResponseModel> subscribeUserToPackage(String userId, String packageId) {
        return subscriptionRepository.existsByUserIdAndPackageId(userId, packageId)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("User is already subscribed."));
                    }

                    Subscription subscription = Subscription.builder()
                            .userId(userId)
                            .packageId(packageId)
                            .subscribedAt(LocalDateTime.now())
                            .build();

                    return subscriptionRepository.save(subscription)
                            .map(SubscriptionEntityModelUtil::toSubscriptionResponseModel);
                });
    }

    @Override
    public Mono<Void> unsubscribeUserFromPackage(String userId, String packageId) {
        return subscriptionRepository.deleteByUserIdAndPackageId(userId, packageId)
                .doOnSuccess(unused -> log.info("User {} unsubscribed from package {}", userId, packageId));
    }

    @Override
    public Flux<SubscriptionResponseModel> getSubscriptionsForUser(String userId) {
        return subscriptionRepository.findByUserId(userId)
                .map(SubscriptionEntityModelUtil::toSubscriptionResponseModel);
    }

    @Override
    public Flux<SubscriptionResponseModel> getUsersSubscribedToPackage(String packageId) {
        return subscriptionRepository.findByPackageId(packageId)
                .map(SubscriptionEntityModelUtil::toSubscriptionResponseModel);
    }

    @Override
    public Flux<PackageResponseModel> getSubscribedPackages(String userId) {
        return subscriptionRepository.findByUserId(userId)
                .flatMap(subscription -> packageService.getPackageByPackageId(subscription.getPackageId()));
    }

}