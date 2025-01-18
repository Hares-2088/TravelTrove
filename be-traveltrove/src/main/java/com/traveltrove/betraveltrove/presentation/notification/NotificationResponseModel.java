package com.traveltrove.betraveltrove.presentation.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseModel {

    private String notificationId;
    private String to;
    private String subject;
    private String messageContent;
    private LocalDateTime sentAt;
}
