package com.traveltrove.betraveltrove.business.notification;

import com.traveltrove.betraveltrove.dataaccess.notification.Notification;
import com.traveltrove.betraveltrove.dataaccess.notification.NotificationRepository;
import com.traveltrove.betraveltrove.presentation.notification.NotificationResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceUnitTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void whenSendEmail_withValidInput_thenSucceed() {
        doNothing().when(mailSender).send(any(MimeMessage.class));

        notificationService.sendEmail("test@example.com", "Test Subject", "test-template.html", "arg1", "arg2");

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void whenSendEmail_withNonexistentTemplate_thenThrowError() {
        notificationService.sendEmail("test@example.com", "Test Subject", "nonexistent-template.html");

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void whenSendCustomEmail_withValidInput_thenSucceed() {
        Notification notification = Notification.builder()
                .notificationId(UUID.randomUUID().toString())
                .to("test@example.com")
                .subject("Test Subject")
                .messageContent("Test Content")
                .sentAt(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(Mono.just(notification));

        Mono<Void> result = notificationService.sendCustomEmail("test@example.com", "Test Subject", "Test Content");

        StepVerifier.create(result)
                .verifyComplete();

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void whenGetAllNotifications_thenReturnAllNotifications() {
        Notification notification = Notification.builder()
                .notificationId("1")
                .to("test@example.com")
                .subject("Subject")
                .messageContent("Content")
                .sentAt(LocalDateTime.now())
                .build();

        when(notificationRepository.findAll()).thenReturn(Flux.just(notification));

        Flux<NotificationResponseModel> result = notificationService.getAllNotifications();

        StepVerifier.create(result)
                .expectNextMatches(notificationResponseModel -> notificationResponseModel.getNotificationId().equals("1"))
                .verifyComplete();
    }

    @Test
    void whenGetNotificationById_withExistingId_thenReturnNotification() {
        Notification notification = Notification.builder()
                .notificationId("1")
                .to("test@example.com")
                .subject("Subject")
                .messageContent("Content")
                .sentAt(LocalDateTime.now())
                .build();

        when(notificationRepository.findByNotificationId("1")).thenReturn(Mono.just(notification));

        Mono<NotificationResponseModel> result = notificationService.getNotificationByNotificationId("1");

        StepVerifier.create(result)
                .expectNextMatches(notificationResponseModel -> notificationResponseModel.getNotificationId().equals("1"))
                .verifyComplete();
    }

    @Test
    void whenGetNotificationById_withNonExistingId_thenThrowError() {
        when(notificationRepository.findByNotificationId("1")).thenReturn(Mono.empty());

        Mono<NotificationResponseModel> result = notificationService.getNotificationByNotificationId("1");

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("Notification not found"))
                .verify();
    }

    @Test
    void whenDeleteNotification_withExistingId_thenSucceed() {
        Notification notification = Notification.builder()
                .notificationId("1")
                .to("test@example.com")
                .subject("Subject")
                .messageContent("Content")
                .sentAt(LocalDateTime.now())
                .build();

        when(notificationRepository.findByNotificationId("1")).thenReturn(Mono.just(notification));
        when(notificationRepository.delete(notification)).thenReturn(Mono.empty());

        Mono<Void> result = notificationService.deleteNotificationByNotificationId("1");

        StepVerifier.create(result)
                .verifyComplete();

        verify(notificationRepository, times(1)).delete(notification);
    }

    @Test
    void whenDeleteNotification_withNonExistingId_thenThrowError() {
        when(notificationRepository.findByNotificationId("1")).thenReturn(Mono.empty());

        Mono<Void> result = notificationService.deleteNotificationByNotificationId("1");

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("Notification not found"))
                .verify();
    }


    @Test
    void sendAdminEmail_ShouldSendEmailAndSaveNotification() throws Exception {
        // Given test data
        String to = "admin@example.com";
        String packageName = "New York Adventure Package";
        String packageId = "4f3a6bde-bc68-4b1e-835a-1e5aaf7b752d";
        String availableSeats = "3";
        String description = "Experience the thrill of New York City.";
        String startDate = "2026-05-15";
        String endDate = "2027-05-22";
        String priceSingle = "1800";

        // Expected email subject
        String expectedSubject = "🚨 Low Quantity of Available Seats for " + packageName + " (" + packageId + ")!";

        // Mock email sending (do nothing)
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Mock repository save (simulate successful save)
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // When
        StepVerifier.create(notificationService.sendAdminEmail(to, packageName, packageId, availableSeats, description, startDate, endDate, priceSingle))
                .expectComplete()  // Expect Mono<Void> to complete successfully
                .verify();

        // Then - Verify email sending
        verify(mailSender, times(1)).send(any(MimeMessage.class));

        // Then - Verify notification saved
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(1)).save(notificationCaptor.capture());

        Notification savedNotification = notificationCaptor.getValue();
        assertEquals(expectedSubject, savedNotification.getSubject());
    }


    @Test
    void sendCustomerCancellationEmail_shouldSendEmailAndSaveNotification() {
        // Arrange
        String to = "customer@example.com";
        String firstName = "John";
        String lastName = "Doe";
        String name = "New York Adventure Package";
        String description = "Experience the thrill of New York City!";
        String startDate = "2026-05-15";
        String endDate = "2027-05-22";
        String priceSingle = "1800.00";

        // Mocking the save to NotificationRepository
        Notification notification = new Notification(UUID.randomUUID().toString(), to, "Package Cancellation Notification for New York Adventure Package", "<html>...</html>");
        when(notificationRepository.save(any(Notification.class))).thenReturn(Mono.just(notification));

        // Act
        Mono<Void> result = notificationService.sendCustomerCancellationEmail(to, firstName, lastName, name, description, startDate, endDate, priceSingle);

        // Assert
        StepVerifier.create(result)
                .expectComplete()  // Expect completion of the Mono
                .verify();

        // Verify email sending logic
        verify(mailSender, times(1)).send(mimeMessage);
        verify(notificationRepository, times(1)).save(any(Notification.class));  // Verifying notification was saved
    }

    @Test
    void sendLimitedSpotsEmail_ShouldSendEmail() {
        String to = "customer@example.com";
        String userName = "John Doe";
        String packageName = "Bali Adventure";
        String description = "Enjoy the tropical paradise of Bali.";
        String startDate = "2025-07-01";
        String endDate = "2025-07-10";
        String price = "2500.00";
        String availableSeats = "5";
        String bookingLink = "https://booknow.example.com";

        String expectedSubject = "🚨 Limited Seats Remaining for " + packageName + "!";

        doNothing().when(mailSender).send(any(MimeMessage.class));

        StepVerifier.create(notificationService.sendLimitedSpotsEmail(to, userName, packageName, description, startDate, endDate, price, availableSeats, bookingLink))
                .verifyComplete();

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }


}
