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

            return null;
        }).then();
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

            return null;
        }).then();
    }

    @Override
    public Mono<Void> sendContactUsEmail(String to, String firstName, String lastName, String email, String subject, String message) {
        return Mono.fromCallable(() -> {
            String htmlContent = loadHtmlTemplate("contact-us-email.html", firstName, lastName, email, subject, message);

            MimeMessage mailMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);
            helper.setTo(to);
            helper.setSubject("New Contact Us Submission: " + subject);
            helper.setText(htmlContent, true);
            helper.setFrom("traveltrove.notifications@gmail.com");

            mailSender.send(mailMessage);

            return null;
        }).then();
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

    @Override
    public Mono<Void> sendAdminEmail(String to, String name, String packageId, String availableSeats,
                                     String description, String startDate, String endDate, String priceSingle) {

        return Mono.fromCallable(() -> {
                    // Build email content
                    String subject = "🚨 Low Quantity of Available Seats for " + name + " (" + packageId + ")!";
                    String htmlContent = loadHtmlTemplate("admin-resource-alert-email.html",
                            name, packageId, availableSeats, description, startDate, endDate, priceSingle);

                    // Create MimeMessage
                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true);
                    helper.setTo(to);
                    helper.setSubject(subject);
                    helper.setText(htmlContent, true);  // Set the email content
                    helper.setFrom("traveltrove.notifications@gmail.com");  // Your sender email


                    // Send email
                    mailSender.send(message);  // This will throw an exception if it fails

                    // Return a notification to be saved (optional, depending on your implementation)
                    return new Notification(UUID.randomUUID().toString(), to, subject, htmlContent);
                })
                .flatMap(notificationRepository::save)  // Save notification if needed (optional)
                .then();  // Return Mono<Void> as expected
    }


    public Mono<Void> sendCustomerCancellationEmail(String to, String firstName, String lastName, String name,
                                                    String description,
                                        String startDate, String endDate, String priceSingle) {
        return Mono.fromCallable(() -> {
            String subject = "🚨 Package Cancellation Notification for " + name;
            String htmlContent = loadHtmlTemplate("cancelled-package-email.html", firstName, lastName, name, description, startDate, endDate, priceSingle);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);  // Set the email content
            helper.setFrom("traveltrove.notifications@gmail.com");  // Sender email address

            mailSender.send(message);  // Send email

            return new Notification(UUID.randomUUID().toString(), to, subject, htmlContent);
        }).flatMap(notificationRepository::save).then();
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





