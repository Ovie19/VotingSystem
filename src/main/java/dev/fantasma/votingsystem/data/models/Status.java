package dev.fantasma.votingsystem.data.models;

import dev.fantasma.votingsystem.exceptions.InvalidPollStateException;
import dev.fantasma.votingsystem.exceptions.VotingSystemException;

public enum Status {
    ACTIVE,
    SCHEDULED,
    COMPLETED,
    CANCELED;

    public void validatePollState() throws VotingSystemException {
        switch (this) {
            case ACTIVE    -> {}
            case SCHEDULED -> throw new InvalidPollStateException("This poll has not started yet");
            case COMPLETED -> throw new InvalidPollStateException("This poll has ended");
            case CANCELED  -> throw new InvalidPollStateException("This poll has been canceled");
        }
    }

    public void validateCancelPollState()  throws VotingSystemException {
        if (this != SCHEDULED && this != ACTIVE) {
            throw new InvalidPollStateException(
                    "Only scheduled or active polls can be cancelled"
            );
        }
    }
}
