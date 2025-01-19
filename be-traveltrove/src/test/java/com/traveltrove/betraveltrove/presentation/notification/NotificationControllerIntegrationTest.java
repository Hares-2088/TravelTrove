package com.traveltrove.betraveltrove.presentation.notification;

import com.traveltrove.betraveltrove.dataaccess.notification.Notification;
import com.traveltrove.betraveltrove.dataaccess.notification.NotificationRepository;
import org.junit.jupiter.api.*;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NotificationControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private NotificationRepository notificationRepository;

    private final String INVALID_NOTIFICATION_ID = "invalid-notification-id";

    private final Notification notification1 = Notification.builder()
            .id("1")
            .notificationId(UUID.randomUUID().toString())
            .to("user1@example.com")
            .subject("Subject 1")
            .messageContent("Message Content 1")
            .sentAt(LocalDateTime.now())
            .build();

    private final Notification notification2 = Notification.builder()
            .id("2")
            .notificationId(UUID.randomUUID().toString())
            .to("user2@example.com")
            .subject("Subject 2")
            .messageContent("Message Content 2")
            .sentAt(LocalDateTime.now())
            .build();

    @BeforeEach
    public void setupDB() {
        Publisher<Notification> setupDB = notificationRepository.deleteAll()
                .thenMany(Flux.just(notification1, notification2))
                .flatMap(notificationRepository::save);

        StepVerifier.create(setupDB)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void whenGetAllNotifications_thenReturnAllNotifications() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser()
                        .authorities(new SimpleGrantedAuthority("read:notification")))
                .get()
                .uri("/api/v1/notifications")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(Notification.class)
                .hasSize(2)
                .value(notifications -> {
                    assertEquals(2, notifications.size());
                    assertEquals(notification1.getTo(), notifications.get(0).getTo());
                    assertEquals(notification2.getTo(), notifications.get(1).getTo());
                });

        StepVerifier.create(notificationRepository.findAll())
                .expectNextMatches(notification -> notification.getTo().equals(notification1.getTo()))
                .expectNextMatches(notification -> notification.getTo().equals(notification2.getTo()))
                .verifyComplete();
    }

    @Test
    void whenGetNotificationByNotificationId_withExistingId_thenReturnNotification() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser()
                        .authorities(new SimpleGrantedAuthority("read:notification")))
                .get()
                .uri("/api/v1/notifications/{notificationId}", notification1.getNotificationId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Notification.class)
                .value(notification -> assertEquals(notification1.getNotificationId(), notification.getNotificationId()));
    }

    @Test
    void whenGetNotificationByNotificationId_withInvalidId_thenReturnNotFound() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser()
                        .authorities(new SimpleGrantedAuthority("read:notification")))
                .get()
                .uri("/api/v1/notifications/{notificationId}", INVALID_NOTIFICATION_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Notification not found with ID: " + INVALID_NOTIFICATION_ID);
    }

    @Test
    void whenGetNotificationByNotificationId_withInvalidAuthority_thenReturnForbidden() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri("/api/v1/notifications/{notificationId}", notification1.getNotificationId())
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenDeleteNotification_withExistingId_thenReturnNoContent() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser()
                        .authorities(new SimpleGrantedAuthority("delete:notification")))
                .mutateWith(SecurityMockServerConfigurers.csrf()).delete()
                .uri("/api/v1/notifications/{notificationId}", notification1.getNotificationId())
                .exchange()
                .expectStatus().isNoContent();

        StepVerifier.create(notificationRepository.findById(notification1.getId()))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void whenDeleteNotification_withInvalidId_thenReturnNotFound() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser()
                        .authorities(new SimpleGrantedAuthority("delete:notification")))
                .mutateWith(SecurityMockServerConfigurers.csrf()).delete()
                .uri("/api/v1/notifications/{notificationId}", INVALID_NOTIFICATION_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Notification not found with ID: " + INVALID_NOTIFICATION_ID);
    }

    @Test
    void whenDeleteNotification_withInvalidAuthority_thenReturnForbidden() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).delete()
                .uri("/api/v1/notifications/{notificationId}", notification1.getNotificationId())
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenSendCustomEmail_thenReturnSuccessMessage() {
        NotificationRequestModel requestModel = NotificationRequestModel.builder()
                .to("user3@example.com")
                .subject("New Subject")
                .messageContent("New message content.")
                .build();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser()
                        .authorities(new SimpleGrantedAuthority("create:notification")))
                .mutateWith(SecurityMockServerConfigurers.csrf()).post()
                .uri("/api/v1/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> assertEquals("Custom email sent successfully.", response));
    }

    @Test
    void whenSendCustomEmail_withInvalidAuthority_thenReturnForbidden() {
        NotificationRequestModel requestModel = NotificationRequestModel.builder()
                .to("user3@example.com")
                .subject("New Subject")
                .messageContent("New message content.")
                .build();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf()).post()
                .uri("/api/v1/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isForbidden();
    }
}
