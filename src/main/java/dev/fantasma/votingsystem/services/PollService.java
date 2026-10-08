package dev.fantasma.votingsystem.services;

import dev.fantasma.votingsystem.data.models.Status;
import dev.fantasma.votingsystem.dtos.requests.CreatePollRequest;
import dev.fantasma.votingsystem.dtos.responses.PollResponse;
import dev.fantasma.votingsystem.exceptions.VotingSystemException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PollService {
    PollResponse createPoll(CreatePollRequest request) throws VotingSystemException;
    Page<PollResponse> getAllPolls(Pageable pageable);
    Page<PollResponse> getPollsByStatus(Status status, Pageable pageable);
    List<UUID> getScheduledPollIdsBefore(Instant startAt);
    void startPoll(UUID pollId);
    List<UUID> getActivePollIdsBefore(Instant startAt);
    void endPoll(UUID pollId);
}
