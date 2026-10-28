package dev.fantasma.votingsystem.services;

import dev.fantasma.votingsystem.data.models.Status;
import dev.fantasma.votingsystem.dtos.requests.CreatePollRequest;
import dev.fantasma.votingsystem.dtos.responses.CancelPollResponse;
import dev.fantasma.votingsystem.dtos.responses.ClosePollResponse;
import dev.fantasma.votingsystem.dtos.responses.DeletePollResponse;
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
    void startPoll(UUID pollId) throws VotingSystemException;
    List<UUID> getActivePollIdsBefore(Instant endAt);
    void endPoll(UUID pollId) throws VotingSystemException;
    ClosePollResponse closePoll(UUID pollId) throws VotingSystemException;
    CancelPollResponse cancelPoll(UUID pollId) throws VotingSystemException;
    DeletePollResponse deletePoll(UUID pollId) throws VotingSystemException;
}
