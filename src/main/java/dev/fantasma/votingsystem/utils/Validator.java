package dev.fantasma.votingsystem.utils;

import dev.fantasma.votingsystem.dtos.requests.CreateUserRequest;
import dev.fantasma.votingsystem.exceptions.InvalidRequestException;
import dev.fantasma.votingsystem.exceptions.VotingSystemException;

public class Validator {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z]{2,}$";
    private static final String NAME_REGEX = "^\\p{L}+(?:[ '-]\\p{L}+)*$";
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MIN_PASSWORD_LENGTH = 8;

    public static void validate(CreateUserRequest request) throws VotingSystemException {
        if (request == null) throw new InvalidRequestException("Request cannot be null");
        validateName(request.getFirstName(), "first name");
        validateName(request.getLastName(), "last name");
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());
    }

    private static void validatePassword(String password) throws InvalidRequestException {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH)
            throw new InvalidRequestException("Password length should be at least " + MIN_PASSWORD_LENGTH + " characters");
    }

    private static void validateEmail(String email) throws VotingSystemException {
        if (email == null) throw new InvalidRequestException("Invalid email");
        if (!email.matches(EMAIL_REGEX)) throw new InvalidRequestException("Invalid email");
    }

    private static void validateName(String field, String fieldName) throws VotingSystemException {
        if (field == null || field.isBlank()) throw new InvalidRequestException("Invalid " + fieldName);
        if (field.trim().length() < MIN_NAME_LENGTH) throw new InvalidRequestException("Invalid " + fieldName);
        if (!field.matches(NAME_REGEX)) throw new InvalidRequestException("Invalid " + fieldName);
    }
}
