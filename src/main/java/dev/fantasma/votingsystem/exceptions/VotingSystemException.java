package dev.fantasma.votingsystem.exceptions;

public class VotingSystemException extends RuntimeException {
    public VotingSystemException(String message) {
        super(message);
    }

    public VotingSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public VotingSystemException(Throwable cause) {
        super(cause);
    }
}
