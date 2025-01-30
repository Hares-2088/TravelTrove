package com.traveltrove.betraveltrove.presentation.tourpackage;

import com.traveltrove.betraveltrove.dataaccess.tourpackage.PackageStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PackageRequestStatus {
    private PackageStatus status;
}
