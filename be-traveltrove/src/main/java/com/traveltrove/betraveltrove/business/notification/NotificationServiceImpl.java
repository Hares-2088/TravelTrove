package com.traveltrove.betraveltrove.business.notification;

import com.traveltrove.betraveltrove.dataaccess.notification.Notification;
import com.traveltrove.betraveltrove.dataaccess.notification.NotificationRepository;
import com.traveltrove.betraveltrove.presentation.notification.NotificationResponseModel;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(JavaMailSender mailSender, NotificationRepository notificationRepository) {
        this.mailSender = mailSender;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void sendEmail(String to, String subject, String templateName, String... templateArgs) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            String htmlContent = loadHtmlTemplate(templateName, templateArgs);

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom("traveltrove.notifications@gmail.com");

            mailSender.send(message);
            System.out.println("HTML email sent successfully to " + to);
        } catch (Exception e) {
            System.err.println("Error sending HTML email: " + e.getMessage());
        }
    }

    @Override
    public Mono<Void> sendCustomEmail(String to, String subject, String messageContent) {
        return Mono.fromCallable(() -> {
            MimeMessage message = mailSender.createMimeMessage();
            String htmlContent = loadHtmlTemplate("custom-email.html", subject, messageContent);

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom("traveltrove.notifications@gmail.com");

            mailSender.send(message);

            String notificationId = UUID.randomUUID().toString();

            return new Notification(notificationId, to, subject, messageContent);
        }).flatMap(notificationRepository::save).then();
    }

    @Override
    public Mono<Void> sendPostTourReviewEmail(String to, String userName, String packageTitle,
                                              String description, String startDate, String endDate, String reviewLink) {
        return Mono.fromCallable(() -> {
            String subject = "Share Your Experience with " + packageTitle;
            String htmlContent = loadHtmlTemplate("post-trip-review-email.html", userName, packageTitle, description, startDate, endDate, reviewLink);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom("traveltrove.notifications@gmail.com");

            mailSender.send(message);
            return new Notification(UUID.randomUUID().toString(), to, subject, htmlContent);
        }).flatMap(notificationRepository::save).then();
    }

    @Override
    public Mono<Void> sendLimitedSpotsEmail(String to, String userName, String packageName,
                                            String description, String startDate, String endDate,
                                            String price, String availableSeats, String bookingLink) {
        return Mono.fromCallable(() -> {
            String subject = "🚨 Limited Seats Remaining for " + packageName + "!";

            String htmlContent = loadHtmlTemplate("limited-spots-email.html",
                    userName, packageName, availableSeats, description, startDate, endDate, price, bookingLink);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom("traveltrove.notifications@gmail.com");

            mailSender.send(message);

            return new Notification(UUID.randomUUID().toString(), to, subject, htmlContent);
        }).flatMap(notificationRepository::save).then();
    }

    @Override
    public Flux<NotificationResponseModel> getAllNotifications() {
        return notificationRepository.findAll()
                .map(notification -> NotificationResponseModel.builder()
                        .notificationId(notification.getNotificationId())
                        .to(notification.getTo())
                        .subject(notification.getSubject())
                        .messageContent(notification.getMessageContent())
                        .sentAt(notification.getSentAt())
                        .build());
    }

    @Override
    public Mono<NotificationResponseModel> getNotificationByNotificationId(String notificationId) {
        return notificationRepository.findByNotificationId(notificationId)
                .map(notification -> NotificationResponseModel.builder()
                        .notificationId(notification.getNotificationId())
                        .to(notification.getTo())
                        .subject(notification.getSubject())
                        .messageContent(notification.getMessageContent())
                        .sentAt(notification.getSentAt())
                        .build())
                .switchIfEmpty(Mono.error(new NotFoundException("Notification not found with ID: " + notificationId)));
    }

    @Override
    public Mono<Void> deleteNotificationByNotificationId(String notificationId) {
        return notificationRepository.findByNotificationId(notificationId)
                .switchIfEmpty(Mono.error(new NotFoundException("Notification not found with ID: " + notificationId)))
                .flatMap(notificationRepository::delete);
    }

    private String loadHtmlTemplate(String templateName, String... templateArgs) {
        ClassPathResource resource = new ClassPathResource("email-templates/" + templateName);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            String htmlContent = content.toString();
            for (int i = 0; i < templateArgs.length; i++) {
                htmlContent = htmlContent.replace("{{arg" + i + "}}", templateArgs[i]);
            }

            return htmlContent;
        } catch (IOException e) {
            throw new RuntimeException("Error loading email template: " + templateName, e);
        }
    }
}
