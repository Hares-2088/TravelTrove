package com.traveltrove.betraveltrove.utils.entitymodelyutils;

import com.traveltrove.betraveltrove.dataaccess.tourpackage.Package;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageRequestModel;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageResponseModel;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

public class PackageEntityModelUtil {

    public static PackageResponseModel toPackageResponseModel(Package pk) {
        PackageResponseModel packageResponseModel = new PackageResponseModel();

        packageResponseModel.setPackageId(generateUUIDString());
        BeanUtils.copyProperties(pk, packageResponseModel);

        return packageResponseModel;
    }

    public static Package toPackage(PackageRequestModel packageRequestModel, String tourId, String airportId) {
        return Package.builder()
                .packageId(generateUUIDString())
                .name(packageRequestModel.getName())
                .description(packageRequestModel.getDescription())
                .airportId(airportId)
                .tourId(tourId)
                .startDate(packageRequestModel.getStartDate())
                .endDate(packageRequestModel.getEndDate())
                .priceSingle(packageRequestModel.getPriceSingle())
                .priceDouble(packageRequestModel.getPriceDouble())
                .priceTriple(packageRequestModel.getPriceTriple())
                .totalSeats(packageRequestModel.getTotalSeats())
                .build();

    }

    private static String generateUUIDString() {
        return UUID.randomUUID().toString();
    }

}
