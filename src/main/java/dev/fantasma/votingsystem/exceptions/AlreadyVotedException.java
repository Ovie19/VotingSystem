package dev.fantasma.votingsystem.exceptions;

public class AlreadyVotedException extends VotingSystemException {
    public AlreadyVotedException(String message) {
        super(message);
    }
}
