package com.traveltrove.betraveltrove.business.tourpackage;

import com.traveltrove.betraveltrove.presentation.tourpackage.PackageRequestModel;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PackageService {

    Flux<PackageResponseModel> getAllPackages(String tourId);

    Mono<PackageResponseModel> getPackageByPackageId(String packageId);

    Mono<PackageResponseModel> createPackage(PackageRequestModel packageRequestModel, String tourId, String airportId);

    Mono<PackageResponseModel> updatePackage(String packageId, PackageRequestModel packageRequestModel);

    Mono<PackageResponseModel> deletePackage(String packageId);
}
