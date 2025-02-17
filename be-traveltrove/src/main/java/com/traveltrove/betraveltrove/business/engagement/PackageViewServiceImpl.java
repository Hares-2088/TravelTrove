package com.traveltrove.betraveltrove.business.engagement;

import com.traveltrove.betraveltrove.dataaccess.engagement.PackageView;
import com.traveltrove.betraveltrove.dataaccess.engagement.PackageViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class PackageViewServiceImpl implements PackageViewService {

    private final PackageViewRepository packageViewRepository;

    @Override
    public Mono<Void> trackPackageView(String userId, String packageId) {
        PackageView packageView = PackageView.builder()
                .userId(userId)
                .packageId(packageId)
                .viewedAt(Instant.now())
                .build();

        log.info("Tracking package view: userId={}, packageId={}", userId, packageId);
        return packageViewRepository.save(packageView).then();
    }
}
