package com.traveltrove.betraveltrove.utils.entitymodelyutils;

import com.traveltrove.betraveltrove.dataaccess.tourpackage.Subscription;
import com.traveltrove.betraveltrove.presentation.tourpackage.SubscriptionResponseModel;

public class SubscriptionEntityModelUtil {

    public static SubscriptionResponseModel toSubscriptionResponseModel(Subscription subscription) {
        return SubscriptionResponseModel.builder()
                .userId(subscription.getUserId())
                .packageId(subscription.getPackageId())
                .subscribedAt(subscription.getSubscribedAt())
                .build();
    }
}
