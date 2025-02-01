package com.traveltrove.betraveltrove.business.tourpackage;

import reactor.core.publisher.Mono;

public interface PackageServiceHelper {
    Mono<Boolean> packageExistsReactive(String packageId);
}
