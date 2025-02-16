package com.traveltrove.betraveltrove.business.engagement;

import reactor.core.publisher.Mono;

public interface PackageViewService {
    Mono<Void> trackPackageView(String userId, String packageId);
}