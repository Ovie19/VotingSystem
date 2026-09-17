package dev.fantasma.votingsystem.services;

import dev.fantasma.votingsystem.data.repositories.UserRepository;
import dev.fantasma.votingsystem.dtos.requests.CreateUserRequest;
import dev.fantasma.votingsystem.dtos.responses.UserResponse;
import dev.fantasma.votingsystem.exceptions.InvalidRequestException;
import dev.fantasma.votingsystem.exceptions.VotingSystemException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthenticationServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationService authenticationService;

    private CreateUserRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateUserRequest();
        request.setFirstName("Nicolas");
        request.setLastName("Jackson");
        request.setEmail("testemail@gmail.com");
        request.setPassword("4$#5ssd46as");
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_validRequest_returnsUserResponseTest() throws VotingSystemException {
        UserResponse response = authenticationService.createUser(request);
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals("Nicolas Jackson", response.getName());
    }

    @Test
    void createUser_emailAlreadyExists_throwsExceptionTest() throws VotingSystemException {
        authenticationService.createUser(request);
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> authenticationService.createUser(request)
        );
        assertEquals("Email already exists", exception.getMessage());
    }
}
