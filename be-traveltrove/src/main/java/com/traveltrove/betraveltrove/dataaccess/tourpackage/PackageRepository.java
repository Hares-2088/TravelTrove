package com.traveltrove.betraveltrove.dataaccess.tourpackage;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PackageRepository extends ReactiveMongoRepository<Package, String> {

    public Mono<Package> findPackageByPackageId(String packageId);
    public Flux<Package> findPackagesByTourId(String tourId);
    Mono<Boolean> existsByPackageId(String packageId);
}
