package com.traveltrove.betraveltrove.dataaccess.engagement;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface EngagementRepository extends ReactiveMongoRepository<Engagement, String> {
    Mono<Engagement> findByPackageId(String packageId);
}