package com.traveltrove.betraveltrove.dataaccess.notification;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

@DataMongoTest
@ActiveProfiles("test")
public class NotificationRepositoryIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    private final String notificationId = "33cff3cf-de6c-4f66-8f52-82f7ebf8f6d6";

    @BeforeEach
    void setup() {
        Notification testNotification = Notification.builder()
                .id("1")
                .notificationId(notificationId)
                .to("test@example.com")
                .subject("Test Subject")
                .messageContent("This is a test message.")
                .sentAt(LocalDateTime.now())
                .build();

        StepVerifier.create(notificationRepository.save(testNotification))
                .expectNextMatches(savedNotification -> savedNotification.getNotificationId().equals(notificationId))
                .verifyComplete();
    }

    @AfterEach
    void cleanup() {
        StepVerifier.create(notificationRepository.deleteAll())
                .verifyComplete();
    }

    @Test
    void whenFindNotificationByNotificationId_withExistingId_thenReturnExistingNotification() {
        Mono<Notification> foundNotification = notificationRepository.findByNotificationId(notificationId);

        StepVerifier.create(foundNotification)
                .expectNextMatches(notification ->
                        notification.getNotificationId().equals(notificationId) &&
                                notification.getTo().equals("test@example.com") &&
                                notification.getSubject().equals("Test Subject") &&
                                notification.getMessageContent().equals("This is a test message.")
                )
                .verifyComplete();
    }

    @Test
    void whenFindNotificationByNotificationId_withNonExistingId_thenReturnEmptyMono() {
        Mono<Notification> foundNotification = notificationRepository.findByNotificationId("INVALID_ID");

        StepVerifier.create(foundNotification)
                .verifyComplete();
    }
}
