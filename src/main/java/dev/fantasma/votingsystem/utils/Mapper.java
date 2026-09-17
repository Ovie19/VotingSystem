package dev.fantasma.votingsystem.utils;

import dev.fantasma.votingsystem.data.models.User;
import dev.fantasma.votingsystem.dtos.requests.CreateUserRequest;
import dev.fantasma.votingsystem.dtos.responses.UserResponse;

public class Mapper {

    public static User map(CreateUserRequest request) {
        User user = new User();
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(request.getPassword());
        user.setName(request.getFirstName().trim() + " " + request.getLastName().trim());
        return user;
    }

    public static UserResponse map(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        return response;
    }
}
