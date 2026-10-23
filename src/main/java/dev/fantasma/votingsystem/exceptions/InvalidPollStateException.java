package dev.fantasma.votingsystem.exceptions;

public class InvalidPollStateException extends VotingSystemException {
    public InvalidPollStateException(String message) {
        super(message);
    }
}
