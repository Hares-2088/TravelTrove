package com.traveltrove.betraveltrove.business.engagement;

import com.traveltrove.betraveltrove.dataaccess.engagement.Engagement;
import reactor.core.publisher.Mono;

public interface EngagementService {
    Mono<Engagement> trackEngagement(String packageId, String userId, String action);
    Mono<Engagement> getEngagementByPackage(String packageId);
}