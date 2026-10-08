package dev.fantasma.votingsystem.services;

import dev.fantasma.votingsystem.dtos.responses.VoteResponse;
import dev.fantasma.votingsystem.exceptions.VotingSystemException;

import java.util.UUID;

public interface VoteService {
    VoteResponse castVote(UUID pollId, UUID pollOptionId, UUID userId) throws VotingSystemException;
}
