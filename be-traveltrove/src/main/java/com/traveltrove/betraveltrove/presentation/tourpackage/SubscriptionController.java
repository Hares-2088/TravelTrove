package com.traveltrove.betraveltrove.presentation.tourpackage;

import com.traveltrove.betraveltrove.business.tourpackage.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/subscriptions")
@Slf4j
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping(value = "/subscribe", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<SubscriptionResponseModel>> subscribe(
            @RequestParam String userId, @RequestParam String packageId) {
        log.info("User {} subscribing to package {}", userId, packageId);
        return subscriptionService.subscribeUserToPackage(userId, packageId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @DeleteMapping(value = "/unsubscribe", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> unsubscribe(@RequestParam String userId, @RequestParam String packageId) {
        log.info("User {} unsubscribing from package {}", userId, packageId);
        return subscriptionService.unsubscribeUserFromPackage(userId, packageId);
    }

    @GetMapping(value = "/user/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<SubscriptionResponseModel> getUserSubscriptions(@PathVariable String userId) {
        log.info("Fetching subscriptions for user {}", userId);
        return subscriptionService.getSubscriptionsForUser(userId);
    }

    @GetMapping(value = "/package/{packageId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<SubscriptionResponseModel> getPackageSubscribers(@PathVariable String packageId) {
        log.info("Fetching subscribers for package {}", packageId);
        return subscriptionService.getUsersSubscribedToPackage(packageId);
    }

    @GetMapping("/user/{userId}/subscriptions")
    public Flux<PackageResponseModel> getSubscribedPackages(@PathVariable String userId) {
        log.info("Fetching subscribed packages for user: {}", userId);
        return subscriptionService.getSubscribedPackages(userId);
    }
}
