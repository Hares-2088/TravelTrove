package com.traveltrove.betraveltrove.utils;

import com.traveltrove.betraveltrove.dataaccess.hotel.Hotel;
import com.traveltrove.betraveltrove.presentation.hotel.HotelRequestModel;
import com.traveltrove.betraveltrove.presentation.hotel.HotelResponseModel;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

public class HotelEntityModel {

    public static HotelResponseModel toHotelResponseModel(Hotel hotel) {
        HotelResponseModel hotelResponseModel = new HotelResponseModel();
        BeanUtils.copyProperties(hotel, hotelResponseModel);

        // Assign the Ids to the response model if they exist in the provided hotel object
        if (hotel.getHotelId() != null) {
            hotelResponseModel.setHotelId(hotel.getHotelId());
        }
        if (hotel.getCityId() != null) {
            hotelResponseModel.setCityId(hotel.getCityId());
        }

        return hotelResponseModel;
    }

    public static Hotel toHotelEntity(HotelRequestModel hotelRequestModel) {
        return Hotel.builder()
                .hotelId(generateUUIDString())
                .cityId(hotelRequestModel.getCityId())
                .name(hotelRequestModel.getName())
                .url(hotelRequestModel.getUrl())
                .build();
    }

    private static String generateUUIDString() {
        return UUID.randomUUID().toString();
    }
}
