package com.traveltrove.betraveltrove.presentation.email;

import com.traveltrove.betraveltrove.business.email.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('create:notification')")
    public Mono<ResponseEntity<String>> sendCustomEmail(@RequestBody NotificationRequestModel emailRequest) {
        return notificationService.sendCustomEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getMessageContent())
                .then(Mono.just(ResponseEntity.ok("Custom email sent successfully.")))
                .onErrorResume(ex -> Mono.just(ResponseEntity.status(500).body("Failed to send custom email: " + ex.getMessage())));
    }

}