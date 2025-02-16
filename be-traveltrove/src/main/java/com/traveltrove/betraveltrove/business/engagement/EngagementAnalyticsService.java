package com.traveltrove.betraveltrove.business.engagement;


import com.traveltrove.betraveltrove.presentation.engagement.EngagementAnalyticsResponseModel;
import reactor.core.publisher.Flux;

public interface EngagementAnalyticsService {
    Flux<EngagementAnalyticsResponseModel> getEngagementAnalytics();
}