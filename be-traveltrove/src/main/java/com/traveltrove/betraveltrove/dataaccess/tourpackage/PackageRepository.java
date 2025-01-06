package com.traveltrove.betraveltrove.dataaccess.tourpackage;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PackageRepository extends ReactiveMongoRepository<Package, String> {

    public Mono<Package> findPackageByPackageId(String packageId);
    public Flux<Package> findPackagesByTourId(String tourId);
    public Flux<Package> findPackagesByPackageStatus(PackageStatus packageStatus);
}
