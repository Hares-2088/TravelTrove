package com.traveltrove.betraveltrove.business.engagement;

import com.traveltrove.betraveltrove.dataaccess.tourpackage.PackageRepository;
import com.traveltrove.betraveltrove.dataaccess.tourpackage.SubscriptionRepository;
import com.traveltrove.betraveltrove.presentation.engagement.PackageSuggestionResponseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
@RequiredArgsConstructor
public class PackageSuggestionServiceImpl implements PackageSuggestionService {
    private final SubscriptionRepository subscriptionRepository;
    private final PackageRepository packageRepository;



    @Override
    public Flux<PackageSuggestionResponseModel> getUserPackageSuggestions(String userId) {
        return subscriptionRepository.findByUserId(userId)
                .flatMap(subscription -> packageRepository.findById(subscription.getPackageId()))
                .map(tourPackage -> PackageSuggestionResponseModel.builder()
                        .packageId(tourPackage.getId())
                        .packageName(tourPackage.getName())
                        .build());
    }
}