package com.traveltrove.betraveltrove.business.engagement;

import com.traveltrove.betraveltrove.presentation.engagement.PackageSuggestionResponseModel;
import reactor.core.publisher.Flux;

public interface PackageSuggestionService {
    Flux<PackageSuggestionResponseModel> getUserPackageSuggestions(String userId);

}
