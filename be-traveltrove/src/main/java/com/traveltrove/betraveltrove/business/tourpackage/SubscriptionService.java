package com.traveltrove.betraveltrove.business.tourpackage;

import com.traveltrove.betraveltrove.presentation.tourpackage.SubscriptionResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SubscriptionService {
    Mono<SubscriptionResponseModel> subscribeUserToPackage(String userId, String packageId);
    Mono<Void> unsubscribeUserFromPackage(String userId, String packageId);
    Flux<SubscriptionResponseModel> getSubscriptionsForUser(String userId);
    Flux<SubscriptionResponseModel> getUsersSubscribedToPackage(String packageId);
}
