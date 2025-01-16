package com.traveltrove.betraveltrove.presentation.tourpackage;

import com.traveltrove.betraveltrove.business.tourpackage.PackageService;
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
    public Mono<ResponseEntity<PackageResponseModel>> updatePackage(@PathVariable String packageId, @RequestBody Mono<PackageRequestModel> packageRequestModel) {
        log.info("Updating package with package ID: {}", packageId);
        return packageService.updatePackage(packageId, packageRequestModel)
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

    @PatchMapping(value = "/{packageId}/decreaseAvailableSeats", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PackageResponseModel>> decreaseAvailableSeats(@PathVariable String packageId, @RequestParam Integer quantity) {
        log.info("Decreasing available seats for package with package ID: {}", packageId);
        return packageService.decreaseAvailableSeats(packageId, quantity)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(throwable -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PatchMapping(value = "/{packageId}/increaseAvailableSeats", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PackageResponseModel>> increaseAvailableSeats(@PathVariable String packageId, @RequestParam Integer quantity) {
        log.info("Increasing available seats for package with package ID: {}", packageId);
        return packageService.increaseAvailableSeats(packageId, quantity)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(throwable -> Mono.just(ResponseEntity.badRequest().build()));
    }

}
