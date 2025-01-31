package com.traveltrove.betraveltrove.presentation.tourpackage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponseModel {
    private String userId;
    private String packageId;
    private LocalDateTime subscribedAt;
}