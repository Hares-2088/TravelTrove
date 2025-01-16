package com.traveltrove.betraveltrove.dataaccess.review;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ReviewRepository extends ReactiveMongoRepository<Review, String> {
    public Flux<Review> findByPackageId(String packageId);
    public Flux<Review> findByUserId(String userId);
}

