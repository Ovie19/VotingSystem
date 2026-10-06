package dev.fantasma.votingsystem.utils;

import dev.fantasma.votingsystem.data.models.Poll;
import dev.fantasma.votingsystem.data.models.PollOption;
import dev.fantasma.votingsystem.data.models.Role;
import dev.fantasma.votingsystem.data.models.User;
import dev.fantasma.votingsystem.dtos.requests.CreatePollOptionRequest;
import dev.fantasma.votingsystem.dtos.requests.CreatePollRequest;
import dev.fantasma.votingsystem.dtos.requests.CreateUserRequest;
import dev.fantasma.votingsystem.dtos.requests.LoginRequest;
import dev.fantasma.votingsystem.dtos.responses.LoginResponse;
import dev.fantasma.votingsystem.dtos.responses.PollOptionResponse;
import dev.fantasma.votingsystem.dtos.responses.PollResponse;
import dev.fantasma.votingsystem.dtos.responses.UserResponse;

import java.util.ArrayList;
import java.util.List;

public class Mapper {

    public static User map(CreateUserRequest request) {
        User user = new User();
        user.setEmail(request.getEmail().strip().toLowerCase());
        user.setPassword(request.getPassword());
        user.setName(request.getFirstName().strip() + " " + request.getLastName().strip());
        user.getRoles().add(Role.ROLE_VOTER);
        return user;
    }

    public static UserResponse map(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        return response;
    }

    public static LoginResponse map(String token, User user) {
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setRoles(user.getRoles());
        return response;
    }

    public static Poll map(CreatePollRequest request) {
        Poll poll = new Poll();
        poll.setTitle(request.getTitle().strip().toLowerCase());
        poll.setStartAt(request.getStartAt());
        poll.setEndAt(request.getEndAt());
        return poll;
    }

    public static PollResponse map(Poll poll) {
        PollResponse response = new PollResponse();
        response.setId(poll.getId());
        response.setTitle(poll.getTitle());
        response.setStartAt(poll.getStartAt());
        response.setEndAt(poll.getEndAt());
        response.setStatus(poll.getStatus());
        response.setOptions(poll.getOptions().stream().map(Mapper::map).toList());
        return response;
    }

    public static PollOptionResponse map(PollOption pollOption) {
        PollOptionResponse optionResponse = new PollOptionResponse();
        optionResponse.setId(pollOption.getId());
        optionResponse.setLabel(pollOption.getLabel());
        return optionResponse;
    }
}
