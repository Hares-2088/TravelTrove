package com.traveltrove.betraveltrove.dataaccess.notification;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {
    Mono<Notification> findByNotificationId(String notificationId);
}

