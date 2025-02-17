package com.traveltrove.betraveltrove.business.engagement;

import static org.junit.jupiter.api.Assertions.*;
import com.traveltrove.betraveltrove.dataaccess.engagement.Engagement;
import com.traveltrove.betraveltrove.dataaccess.engagement.EngagementRepository;
import com.traveltrove.betraveltrove.presentation.engagement.EngagementAnalyticsResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.mockito.Mockito.when;
class EngagementAnalyticsServiceUnitTest {
    @Mock
    private EngagementRepository engagementRepository;

    @InjectMocks
    private EngagementAnalyticsServiceImpl engagementAnalyticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEngagementAnalytics() {
        Engagement engagement1 = new Engagement(null, "package1", "user1", 50, 20, 15, null);
        Engagement engagement2 = new Engagement(null, "package2", "user2", 30, 10, 5, null);
        Engagement engagement3 = new Engagement(null, "package3", "user3", 70, 40, 25, null);

        when(engagementRepository.findAll()).thenReturn(Flux.fromIterable(Arrays.asList(engagement1, engagement2, engagement3)));

        StepVerifier.create(engagementAnalyticsService.getEngagementAnalytics())
                .expectNextMatches(response -> response.getPackageId().equals("package1") && response.getTotalViews() == 50)
                .expectNextMatches(response -> response.getPackageId().equals("package2") && response.getTotalWishlists() == 10)
                .expectNextMatches(response -> response.getPackageId().equals("package3") && response.getTotalShares() == 25)
                .verifyComplete();
    }
}