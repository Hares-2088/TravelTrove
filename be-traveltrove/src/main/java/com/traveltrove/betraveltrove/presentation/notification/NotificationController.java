package com.traveltrove.betraveltrove.presentation.notification;

import com.traveltrove.betraveltrove.business.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('create:notification')")
    public Mono<ResponseEntity<String>> sendCustomEmail(@RequestBody NotificationRequestModel emailRequest) {
        log.info("Received request to send custom email to: {}", emailRequest.getTo());
        return notificationService.sendCustomEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getMessageContent())
                .doOnSuccess(unused -> log.info("Custom email sent successfully to: {}", emailRequest.getTo()))
                .then(Mono.just(ResponseEntity.ok("Custom email sent successfully.")))
                .onErrorResume(ex -> {
                    log.error("Failed to send custom email to: {}. Error: {}", emailRequest.getTo(), ex.getMessage());
                    return Mono.just(ResponseEntity.status(500).body("Failed to send custom email: " + ex.getMessage()));
                });
    }

    @GetMapping(value = "/{notificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('read:notification')")
    public Mono<ResponseEntity<NotificationResponseModel>> getNotificationByNotificationId(@PathVariable String notificationId) {
        log.info("Fetching notification with ID: {}", notificationId);
        return notificationService.getNotificationByNotificationId(notificationId)
                .doOnSuccess(notification -> log.info("Successfully fetched notification with ID: {}", notificationId))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{notificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('delete:notification')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteNotificationByNotificationId(@PathVariable String notificationId) {
        log.info("Received request to delete notification with ID: {}", notificationId);
        return notificationService.deleteNotificationByNotificationId(notificationId);
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasAuthority('read:notification')")
    public Flux<NotificationResponseModel> getAllNotifications() {
        log.info("Fetching all notifications");
        return notificationService.getAllNotifications();
    }
}
