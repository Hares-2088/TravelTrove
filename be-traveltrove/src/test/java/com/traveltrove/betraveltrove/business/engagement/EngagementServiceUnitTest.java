package com.traveltrove.betraveltrove.business.engagement;
import com.traveltrove.betraveltrove.dataaccess.engagement.Engagement;
import com.traveltrove.betraveltrove.dataaccess.engagement.EngagementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EngagementServiceUnitTest {

    @Mock
    private EngagementRepository engagementRepository;

    @InjectMocks
    private EngagementServiceImpl engagementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTrackEngagement_ViewAction() {
        String packageId = "123";
        String userId = "user1";
        Engagement existingEngagement = new Engagement(null, packageId, userId, 2, 1, 1, Instant.now());
        Engagement updatedEngagement = new Engagement(null, packageId, userId, 3, 1, 1, Instant.now());

        when(engagementRepository.findByPackageId(packageId)).thenReturn(Mono.just(existingEngagement));
        when(engagementRepository.save(any(Engagement.class))).thenReturn(Mono.just(updatedEngagement));

        StepVerifier.create(engagementService.trackEngagement(packageId, userId, "view"))
                .expectNextMatches(engagement -> engagement.getViewCount() == 3)
                .verifyComplete();

        verify(engagementRepository, times(1)).save(any(Engagement.class));
    }

    @Test
    void testTrackEngagement_WishlistAction() {
        String packageId = "456";
        String userId = "user2";
        Engagement existingEngagement = new Engagement(null, packageId, userId, 5, 2, 3, Instant.now());
        Engagement updatedEngagement = new Engagement(null, packageId, userId, 5, 3, 3, Instant.now());

        when(engagementRepository.findByPackageId(packageId)).thenReturn(Mono.just(existingEngagement));
        when(engagementRepository.save(any(Engagement.class))).thenReturn(Mono.just(updatedEngagement));

        StepVerifier.create(engagementService.trackEngagement(packageId, userId, "wishlist"))
                .expectNextMatches(engagement -> engagement.getWishlistCount() == 3)
                .verifyComplete();

        verify(engagementRepository, times(1)).save(any(Engagement.class));
    }

    @Test
    void testGetEngagementByPackage() {
        String packageId = "123";
        Engagement engagement = new Engagement(null, packageId, "user1", 10, 5, 2, Instant.now());

        when(engagementRepository.findByPackageId(packageId)).thenReturn(Mono.just(engagement));

        StepVerifier.create(engagementService.getEngagementByPackage(packageId))
                .expectNextMatches(e -> e.getPackageId().equals(packageId) && e.getViewCount() == 10)
                .verifyComplete();

        verify(engagementRepository, times(1)).findByPackageId(packageId);
    }
}