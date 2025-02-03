package com.traveltrove.betraveltrove.presentation.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationContactUsRequestModel {
    private String to;
    private String firstName;
    private String lastName;
    private String email;
    private String subject;
    private String messageContent;
}
