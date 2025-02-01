package com.traveltrove.betraveltrove.presentation.tourpackage;

import com.traveltrove.betraveltrove.business.tourpackage.PackageService;
import com.traveltrove.betraveltrove.dataaccess.tourpackage.PackageStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/packages")
@Slf4j
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<PackageResponseModel> getPackages(
            @RequestParam(required = false) String tourId) {
        log.info("Fetching packages with filters: tourId={}", tourId);
        return packageService.getAllPackages(tourId);
    }

    @GetMapping(value = "/{packageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PackageResponseModel>> getPackageByPackageId(@PathVariable String packageId) {
        log.info("Fetching package by package ID: {}", packageId);
        return packageService.getPackageByPackageId(packageId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PackageResponseModel>> createPackage(@RequestBody Mono<PackageRequestModel> packageRequestModel) {
        log.info("Creating new package");
        return packageService.createPackage(packageRequestModel)
                .map(packageResponseModel -> ResponseEntity.status(201).body(packageResponseModel))
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
    }

    @PutMapping(value = "/{packageId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PackageResponseModel>> updatePackage(@PathVariable String packageId, @RequestBody Mono<PackageRequestModel> packageRequestModel,
                                                                    @RequestParam (required = false) String notificationMessage) {
        log.info("Updating package with package ID: {}", packageId);
        return packageService.updatePackage(packageId, packageRequestModel, notificationMessage)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping(value = "/{packageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PackageResponseModel>> deletePackage(@PathVariable String packageId) {
        log.info("Deleting package with package ID: {}", packageId);
        return packageService.deletePackage(packageId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }


    @PatchMapping(value = "/{packageId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PackageResponseModel>> updatePackageStatus(
            @PathVariable String packageId,
            @RequestBody PackageRequestStatus newStatus) {

        log.info("📢 Received request to update package status for packageId={}, payload={}", packageId, newStatus);

        if (newStatus == null || newStatus.getStatus() == null) {
            log.error("❌ ERROR: Received null status in request for packageId={}", packageId);
            return Mono.just(ResponseEntity.badRequest().body(null));
        }

        return packageService.updatePackageStatus(packageId, newStatus)
                .doOnSuccess(response -> log.info("✅ Successfully updated package status for packageId={}, newStatus={}", packageId, response.getStatus()))
                .doOnError(error -> log.error("❌ Error updating package status for packageId={}, error={}", packageId, error.getMessage(), error))
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> {
                    log.warn("⚠️ Returning BAD REQUEST due to error updating package status: {}", throwable.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

}
