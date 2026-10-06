package dev.fantasma.votingsystem.services;

import dev.fantasma.votingsystem.data.models.Status;
import dev.fantasma.votingsystem.dtos.requests.CreatePollRequest;
import dev.fantasma.votingsystem.dtos.responses.PollResponse;
import dev.fantasma.votingsystem.exceptions.VotingSystemException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VotingService {
    PollResponse createPoll(CreatePollRequest request) throws VotingSystemException;
    Page<PollResponse> getAllPolls(Pageable pageable);
    Page<PollResponse> getPollsByStatus(Status status, Pageable pageable);
}
