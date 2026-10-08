package dev.fantasma.votingsystem.exceptions;

public class ResourceNotFoundException extends VotingSystemException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
