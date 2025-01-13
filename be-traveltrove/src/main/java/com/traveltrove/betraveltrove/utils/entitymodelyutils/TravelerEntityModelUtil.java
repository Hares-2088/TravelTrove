package com.traveltrove.betraveltrove.utils.entitymodelyutils;

import com.traveltrove.betraveltrove.dataaccess.traveler.Traveler;
import com.traveltrove.betraveltrove.presentation.traveler.TravelerRequestModel;
import com.traveltrove.betraveltrove.presentation.traveler.TravelerResponseModel;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

public class TravelerEntityModelUtil {

    // Method to convert a Traveler object to a TravelerResponseModel object
    public static TravelerResponseModel toTravelerResponseModel(Traveler traveler) {
        TravelerResponseModel travelerResponseModel = new TravelerResponseModel();
        BeanUtils.copyProperties(traveler, travelerResponseModel);
        return travelerResponseModel;
    }

    // Method to map a TravelerRequestModel to a Traveler entity
    public static Traveler toTravelerEntity(TravelerRequestModel travelerRequestModel) {
        return Traveler.builder()
                .travelerId(generateUUIDString()) // Generate a unique travelerId
                .seq(travelerRequestModel.getSeq())
                .firstName(travelerRequestModel.getFirstName())
                .lastName(travelerRequestModel.getLastName())
                .addressLine1(travelerRequestModel.getAddressLine1())
                .addressLine2(travelerRequestModel.getAddressLine2())
                .city(travelerRequestModel.getCity())
                .state(travelerRequestModel.getState())
                .email(travelerRequestModel.getEmail())
                .countryId(travelerRequestModel.getCountryId())
                .build();
    }

    // Utility method to generate a UUID string
    private static String generateUUIDString() {
        return UUID.randomUUID().toString();
    }
}

