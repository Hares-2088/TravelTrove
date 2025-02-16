package com.traveltrove.betraveltrove.business.engagement;

import com.traveltrove.betraveltrove.dataaccess.engagement.EngagementRepository;
import com.traveltrove.betraveltrove.presentation.engagement.EngagementAnalyticsResponseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
@RequiredArgsConstructor
public class EngagementAnalyticsServiceImpl implements EngagementAnalyticsService {

    private final EngagementRepository engagementRepository;

    @Override
    public Flux<EngagementAnalyticsResponseModel> getEngagementAnalytics() {
        return engagementRepository.findAll()
                .map(engagement -> EngagementAnalyticsResponseModel.builder()
                        .packageId(engagement.getPackageId())
                        .totalViews(engagement.getViewCount())
                        .totalWishlists(engagement.getWishlistCount())
                        .totalShares(engagement.getShareCount())
                        .build());
    }
}
