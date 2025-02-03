package com.traveltrove.betraveltrove.business.notification;

import com.traveltrove.betraveltrove.presentation.notification.NotificationResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationService {

    Flux<NotificationResponseModel> getAllNotifications();
    void sendEmail(String to, String subject, String templateName, String... templateArgs);
    Mono<Void> sendCustomEmail(String to, String subject, String message);
    Mono<NotificationResponseModel> getNotificationByNotificationId(String notificationId);
    Mono<Void> deleteNotificationByNotificationId(String notificationId);
    Mono<Void> sendPostTourReviewEmail(String to, String userName, String packageTitle,
                                       String description, String startDate,
                                       String endDate, String reviewLink);
    Mono<Void> sendLimitedSpotsEmail(String to, String userName, String packageName,
                                     String description, String startDate, String endDate,
                                     String price, String availableSeats, String bookingLink);
    Mono<Void> sendContactUsEmail(String to, String firstName, String lastName, String email, String subject, String message);
}
