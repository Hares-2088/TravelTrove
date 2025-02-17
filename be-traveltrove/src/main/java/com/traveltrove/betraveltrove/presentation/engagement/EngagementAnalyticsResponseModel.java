package com.traveltrove.betraveltrove.presentation.engagement;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EngagementAnalyticsResponseModel {
    private String packageId;
    private int totalViews;
    private int totalWishlists;
    private int totalShares;
}
