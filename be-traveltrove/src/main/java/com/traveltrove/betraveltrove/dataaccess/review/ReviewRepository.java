package com.traveltrove.betraveltrove.dataaccess.review;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ReviewRepository extends ReactiveMongoRepository<Review, String> {
    Flux<Review> findByPackageId(String packageId);
    Flux<Review> findByUserId(String userId);
}

