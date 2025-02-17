package com.traveltrove.betraveltrove.presentation.engagement;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PackageSuggestionResponseModel {
    private String packageId;
    private String packageName;
}
