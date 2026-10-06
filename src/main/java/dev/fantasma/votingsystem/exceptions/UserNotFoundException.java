package dev.fantasma.votingsystem.exceptions;

public class UserNotFoundException extends VotingSystemException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
