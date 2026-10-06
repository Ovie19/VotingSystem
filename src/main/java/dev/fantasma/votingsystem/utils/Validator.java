package dev.fantasma.votingsystem.utils;

import dev.fantasma.votingsystem.dtos.requests.CreatePollOptionRequest;
import dev.fantasma.votingsystem.dtos.requests.CreatePollRequest;
import dev.fantasma.votingsystem.dtos.requests.CreateUserRequest;
import dev.fantasma.votingsystem.dtos.requests.LoginRequest;
import dev.fantasma.votingsystem.exceptions.InvalidPollScheduleException;
import dev.fantasma.votingsystem.exceptions.InvalidRequestException;
import dev.fantasma.votingsystem.exceptions.VotingSystemException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Validator {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z]{2,}$";
    private static final String NAME_REGEX = "^\\p{L}+(?:[ '-]\\p{L}+)*$";
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MIN_FIELD_LENGTH = 4;
    private static final int MIN_OPTION_LENGTH = 2;
    private static final Duration START_AT_GRACE_PERIOD = Duration.ofMinutes(1);
    private static final Duration MIN_POLL_DURATION = Duration.ofMinutes(5);

    public static void validate(CreateUserRequest request) throws VotingSystemException {
        if (request == null) throw new InvalidRequestException("Request cannot be null");
        validateName(request.getFirstName(), "first name");
        validateName(request.getLastName(), "last name");
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());
    }

    public static void validate(LoginRequest request) throws VotingSystemException {
        if (request == null) throw new InvalidRequestException("Request cannot be null");
        validateEmail(request.getEmail());

        boolean isInvalidPassword = request.getPassword() == null || request.getPassword().isBlank();
        if (isInvalidPassword) throw new InvalidRequestException("Invalid Password");
    }
    
    public static void validate(CreatePollRequest request) throws VotingSystemException {
        validateField(request.getTitle(), "poll title");
        validateStartAt(request.getStartAt());
        validateEndAt(request.getStartAt(), request.getEndAt());
        validateOption(request.getOptions());
    }

    private static void validateOption(List<CreatePollOptionRequest> options) throws VotingSystemException {
        if (options == null || options.size() < MIN_OPTION_LENGTH)
            throw new InvalidRequestException("A poll must have at least two options");

        Set<String> seen = new HashSet<>();
        for (CreatePollOptionRequest option : options) {
            String normalized = option.getLabel().strip().toLowerCase();
            if (!seen.add(normalized))
                throw new InvalidRequestException("Duplicate poll option: \"" + option.getLabel().strip() + "\"");
        }
    }

    private static void validateStartAt(Instant startAt) throws VotingSystemException {
        if (startAt == null) return;

        if (startAt.isBefore(Instant.now().minus(START_AT_GRACE_PERIOD)))
            throw new InvalidPollScheduleException(
                    "Start date cannot be more than " + START_AT_GRACE_PERIOD.toMinutes()
                            + " minute(s) in the past"
            );
    }

    private static void validateEndAt(Instant startAt, Instant endAt) throws VotingSystemException {
        if (endAt == null) return;

        Instant effectiveStartAt = startAt == null ? Instant.now() : startAt;

        if (!endAt.isAfter(effectiveStartAt))
            throw new InvalidPollScheduleException("End date cannot be before start date");

        Duration pollDuration = Duration.between(effectiveStartAt, endAt);
        if (pollDuration.compareTo(MIN_POLL_DURATION) < 0)
            throw new InvalidPollScheduleException(
                    "Poll duration cannot be less than " + MIN_POLL_DURATION.toMinutes()
                        + " minutes"
            );
    }

    private static void validateField(String field, String fieldName) throws InvalidRequestException {
        if (field == null || field.strip().length() <  MIN_FIELD_LENGTH)
            throw new InvalidRequestException("Invalid " + fieldName);
    }

    private static void validatePassword(String password) throws InvalidRequestException {

        if (password == null || password.isBlank() || password.length() < MIN_PASSWORD_LENGTH)
            throw new InvalidRequestException("Password length should be at least " + MIN_PASSWORD_LENGTH + " characters");
    }

    private static void validateEmail(String email) throws VotingSystemException {
        if (email == null) throw new InvalidRequestException("Invalid email");
        if (!email.matches(EMAIL_REGEX)) throw new InvalidRequestException("Invalid email");
    }

    private static void validateName(String field, String fieldName) throws VotingSystemException {
        if (field == null || field.isBlank()) throw new InvalidRequestException("Invalid " + fieldName);
        if (field.strip().length() < MIN_NAME_LENGTH) throw new InvalidRequestException("Invalid " + fieldName);
        if (!field.matches(NAME_REGEX)) throw new InvalidRequestException("Invalid " + fieldName);
    }
}
