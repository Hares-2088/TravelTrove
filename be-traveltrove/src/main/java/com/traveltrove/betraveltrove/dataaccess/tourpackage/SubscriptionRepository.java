package com.traveltrove.betraveltrove.dataaccess.tourpackage;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SubscriptionRepository extends ReactiveMongoRepository<Subscription, String> {
    Flux<Subscription> findByUserId(String userId);
    Flux<Subscription> findByPackageId(String packageId);
    Mono<Boolean> existsByUserIdAndPackageId(String userId, String packageId);
    Mono<Void> deleteByUserIdAndPackageId(String userId, String packageId);
}
