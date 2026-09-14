package dev.fantasma.votingsystem.services;

import dev.fantasma.votingsystem.data.models.User;
import dev.fantasma.votingsystem.data.repositories.UserRepository;
import dev.fantasma.votingsystem.dtos.requests.CreateUserRequest;
import dev.fantasma.votingsystem.dtos.responses.UserResponse;
import dev.fantasma.votingsystem.exceptions.InvalidRequestException;
import dev.fantasma.votingsystem.exceptions.VotingSystemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private CreateUserRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateUserRequest();
        request.setFirstName("Nicolas");
        request.setLastName("Jackson");
        request.setEmail("testemail@gmail.com");
        request.setPassword("4$#5ssd46as");
    }

    @Test
    void nullRequest_throwsExceptionTest() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> authenticationService.createUser(null)
        );
        assertEquals("Request cannot be null", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            " ", "   ",
            "a", "A",
            "ad  e",
            "john--marie",
            "John -Doe",
            "123", "John123",
            "John@", "John!",
            "John\nDoe",
    })
    void invalidFirstName_throwsExceptionTest(String firstName) {
        request.setFirstName(firstName);
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> authenticationService.createUser(request)
        );
        assertEquals("Invalid first name", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            " ", "   ",
            "a", "A",
            "ad  e",
            "john--marie",
            "John -Doe",
            "123", "John123",
            "John@", "John!",
            "John\nDoe",
    })
    void invalidLastName_throwsExceptionTest(String lastName) {
        request.setLastName(lastName);
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> authenticationService.createUser(request)
        );
        assertEquals("Invalid last name", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "a@b.c",
            "user@.com",
            "user@domain",
            "user@domain..com",
            "user@dom@ain.com",
            "user@@domain.com",
            "@domain.com"
    })
    void invalidEmail_throwsExceptionTest(String email) {
        request.setEmail(email);
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> authenticationService.createUser(request)
        );
        assertEquals("Invalid email", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "passwor",
            "p",
            "pass"
    })
    void invalidPassword_throwsExceptionTest(String password) {
        request.setPassword(password);
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> authenticationService.createUser(request)
        );
        assertEquals("Password length should be at least 8 characters", exception.getMessage());
    }

    @Test
    void registerUserSuccessfullyTest() throws VotingSystemException {
        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setName(request.getFirstName() + " " + request.getLastName());
        savedUser.setEmail(request.getEmail());
        savedUser.setPassword(request.getPassword());

        when(userRepository.save(any())).thenReturn(savedUser);
        UserResponse response = authenticationService.createUser(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals("Nicolas Jackson", response.getName());

        verify(userRepository).save(any());
    }

    @Test
    void emailExists_registerUserWithSameEmail_throwsExceptionTest() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> authenticationService.createUser(request)
        );
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
}