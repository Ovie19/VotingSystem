package dev.fantasma.votingsystem.services;

import dev.fantasma.votingsystem.dtos.requests.CreateUserRequest;
import dev.fantasma.votingsystem.dtos.responses.UserResponse;
import dev.fantasma.votingsystem.exceptions.VotingSystemException;

public interface AuthenticationService {
    UserResponse createUser(CreateUserRequest request) throws VotingSystemException;
}
