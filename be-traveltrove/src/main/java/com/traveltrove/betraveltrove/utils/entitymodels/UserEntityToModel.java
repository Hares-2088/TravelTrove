package com.traveltrove.betraveltrove.utils.entitymodels;

import com.traveltrove.betraveltrove.dataaccess.user.User;
import com.traveltrove.betraveltrove.presentation.user.UserResponseModel;

public class UserEntityToModel {

    // Converts MongoDB User Entity to UserResponseModel
    public static UserResponseModel toUserResponseModel(User user) {
        UserResponseModel model = new UserResponseModel();
        model.setUserId(user.getUserId());
        model.setEmail(user.getEmail());
        model.setFirstName(user.getFirstName());
        model.setLastName(user.getLastName());
        model.setRoles(user.getRoles());
        model.setPermissions(user.getPermissions());
        model.setTravelerId(user.getTravelerId());
        return model;
    }
}
