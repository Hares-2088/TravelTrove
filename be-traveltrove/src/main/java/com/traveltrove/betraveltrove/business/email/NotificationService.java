package com.traveltrove.betraveltrove.business.email;

import reactor.core.publisher.Mono;

public interface NotificationService {

    void sendEmail(String to, String subject, String templateName, String... templateArgs);
    Mono<Void> sendCustomEmail(String to, String subject, String message);
}
