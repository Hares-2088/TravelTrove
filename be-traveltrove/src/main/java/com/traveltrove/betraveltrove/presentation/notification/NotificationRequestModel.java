package com.traveltrove.betraveltrove.presentation.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestModel {
    private String to;
    private String subject;
    private String messageContent;
}

