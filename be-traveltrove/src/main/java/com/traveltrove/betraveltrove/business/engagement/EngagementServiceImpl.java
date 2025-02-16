package com.traveltrove.betraveltrove.business.engagement;

import com.traveltrove.betraveltrove.dataaccess.engagement.Engagement;
import com.traveltrove.betraveltrove.dataaccess.engagement.EngagementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class EngagementServiceImpl implements EngagementService {

    private final EngagementRepository engagementRepository;

    @Override
    public Mono<Engagement> trackEngagement(String packageId, String userId, String action) {
        return engagementRepository.findByPackageId(packageId)
                .defaultIfEmpty(new Engagement(null, packageId, userId, 0, 0, 0, Instant.now()))
                .flatMap(engagement -> {
                    switch (action.toLowerCase()) {
                        case "view":
                            engagement.setViewCount(engagement.getViewCount() + 1);
                            break;
                        case "wishlist":
                            engagement.setWishlistCount(engagement.getWishlistCount() + 1);
                            break;
                        case "share":
                            engagement.setShareCount(engagement.getShareCount() + 1);
                            break;
                        default:
                            return Mono.error(new IllegalArgumentException("Invalid action: " + action));
                    }
                    engagement.setLastUpdated(Instant.now());
                    return engagementRepository.save(engagement);
                });
    }

    @Override
    public Mono<Engagement> getEngagementByPackage(String packageId) {
        return engagementRepository.findByPackageId(packageId);
    }
}