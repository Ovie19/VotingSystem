package dev.fantasma.votingsystem.exceptions;

public class InvalidRequestException extends VotingSystemException{
    public InvalidRequestException(String message) {
        super(message);
    }
}
