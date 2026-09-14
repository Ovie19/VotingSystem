package dev.fantasma.votingsystem.exceptions;

public class VotingSystemException extends Exception {
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
