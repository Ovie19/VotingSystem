package dev.fantasma.votingsystem.services;

import dev.fantasma.votingsystem.data.models.User;
import dev.fantasma.votingsystem.data.repositories.UserRepository;
import dev.fantasma.votingsystem.dtos.requests.CreateUserRequest;
import dev.fantasma.votingsystem.dtos.responses.UserResponse;
import dev.fantasma.votingsystem.exceptions.InvalidRequestException;
import dev.fantasma.votingsystem.exceptions.VotingSystemException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static dev.fantasma.votingsystem.utils.Mapper.map;
import static dev.fantasma.votingsystem.utils.Validator.validate;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    @Override
    public UserResponse createUser(CreateUserRequest request) throws VotingSystemException {
        validate(request);
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase()))
            throw new InvalidRequestException("Email already exists");

        User savedUser = userRepository.save(map(request));
        return map(savedUser);
    }
}
