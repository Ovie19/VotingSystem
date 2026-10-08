package dev.fantasma.votingsystem.services;

import dev.fantasma.votingsystem.data.models.User;
import dev.fantasma.votingsystem.data.repositories.UserRepository;
import dev.fantasma.votingsystem.dtos.requests.CreateUserRequest;
import dev.fantasma.votingsystem.dtos.requests.LoginRequest;
import dev.fantasma.votingsystem.dtos.responses.LoginResponse;
import dev.fantasma.votingsystem.dtos.responses.UserResponse;
import dev.fantasma.votingsystem.exceptions.InvalidRequestException;
import dev.fantasma.votingsystem.exceptions.VotingSystemException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static dev.fantasma.votingsystem.utils.Mapper.map;
import static dev.fantasma.votingsystem.utils.Validator.validate;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public UserResponse createUser(CreateUserRequest request) throws VotingSystemException {
        validate(request);
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase()))
            throw new InvalidRequestException("Email already exists");

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(map(request));
        return map(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) throws VotingSystemException {
        validate(request);
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new VotingSystemException("Unexpected authentication principal");
        }

        String token = jwtService.generateToken(user);
        return map(token, user);
    }
}
