package dev.fantasma.votingsystem.dtos.responses;

import java.util.UUID;

public interface OptionVoteCount {
    UUID getOptionId();
    String getLabel();
    long getVotes();
}
