package com.traveltrove.betraveltrove.dataaccess.engagement;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface PackageViewRepository extends ReactiveMongoRepository<PackageView, String> {
    Flux<PackageView> findByPackageId(String packageId);
}