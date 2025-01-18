package com.traveltrove.betraveltrove.dataaccess.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "notifications")
@Builder
@AllArgsConstructor
public class Notification {
    @Id
    private String id;
    private String notificationId;
    private String to;
    private String subject;
    private String messageContent;
    private LocalDateTime sentAt;

    public Notification(String notificationId, String to, String subject, String messageContent) {
        this.notificationId = notificationId;
        this.to = to;
        this.subject = subject;
        this.messageContent = messageContent;
        this.sentAt = LocalDateTime.now();
    }
}
