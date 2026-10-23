package dev.fantasma.votingsystem.services;

import dev.fantasma.votingsystem.data.models.Poll;
import dev.fantasma.votingsystem.data.models.PollOption;
import dev.fantasma.votingsystem.data.models.Status;
import dev.fantasma.votingsystem.data.repositories.PollRepository;
import dev.fantasma.votingsystem.data.repositories.VoteRepository;
import dev.fantasma.votingsystem.dtos.requests.CreatePollOptionRequest;
import dev.fantasma.votingsystem.dtos.requests.CreatePollRequest;
import dev.fantasma.votingsystem.dtos.responses.*;
import dev.fantasma.votingsystem.exceptions.InvalidPollStateException;
import dev.fantasma.votingsystem.exceptions.ResourceNotFoundException;
import dev.fantasma.votingsystem.exceptions.VotingSystemException;
import dev.fantasma.votingsystem.utils.Mapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static dev.fantasma.votingsystem.data.models.Status.*;
import static dev.fantasma.votingsystem.utils.Mapper.map;
import static dev.fantasma.votingsystem.utils.Validator.validate;

@Service
@AllArgsConstructor
public class PollServiceImpl implements PollService {

    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public PollResponse createPoll(CreatePollRequest request) throws VotingSystemException {
        validate(request);
        Poll poll = map(request);

        if (request.getStartAt() == null) poll.setStartAt(Instant.now());

        boolean isActive = !poll.getStartAt().isAfter(Instant.now());
        Status status = isActive ? ACTIVE : SCHEDULED;
        poll.setStatus(status);

        for (CreatePollOptionRequest optionRequest : request.getOptions()) {
            PollOption pollOption = new PollOption();
            pollOption.setLabel(optionRequest.getLabel().strip().toLowerCase());
            pollOption.setPoll(poll);
            poll.getOptions().add(pollOption);
        }

        return map(pollRepository.save(poll));
    }

    @Override
    public Page<PollResponse> getAllPolls(Pageable pageable) {
        return pollRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(Mapper::map);
    }

    @Override
    public Page<PollResponse> getPollsByStatus(Status status, Pageable pageable) {
        return pollRepository.findAllByStatusOrderByCreatedAtDesc(status, pageable)
                .map(Mapper::map);
    }

    @Override
    public List<UUID> getScheduledPollIdsBefore(Instant startAt) {
        return pollRepository.findIdsByStartAtBeforeAndStatus(startAt, SCHEDULED);
    }

    @Override
    @Transactional
    public void startPoll(UUID pollId) throws VotingSystemException {
        updatePollStatus(SCHEDULED, ACTIVE, pollId);
    }

    @Override
    public List<UUID> getActivePollIdsBefore(Instant endAt) {
        return pollRepository.findIdsByEndAtBeforeAndStatus(endAt, ACTIVE);
    }

    @Override
    @Transactional
    public void endPoll(UUID pollId) throws VotingSystemException {
        updatePollStatus(ACTIVE, COMPLETED, pollId);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ClosePollResponse closePoll(UUID pollId) throws VotingSystemException {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found"));
        poll.getStatus().validatePollState();
        poll.setEndAt(Instant.now());
        poll.setStatus(COMPLETED);

        List<PollOptionResultResponse> options =
                voteRepository.countVotesByOption(pollId)
                        .stream()
                        .map(Mapper::map)
                        .toList();

        long totalVotes = options.stream()
                .mapToLong(PollOptionResultResponse::getVotes)
                .sum();

        return map(poll, options, totalVotes);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public CancelPollResponse cancelPoll(UUID pollId) throws VotingSystemException {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found"));
        poll.getStatus().validateCancelPollState();
        poll.setStatus(CANCELED);
        poll.setEndAt(Instant.now());

        CancelPollResponse response = new CancelPollResponse();
        map(response, poll);
        return response;
    }

    private void updatePollStatus(Status expectedCurrentStatus, Status newStatus, UUID id) throws VotingSystemException {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found"));

        if (poll.getStatus() != expectedCurrentStatus) {
            throw new InvalidPollStateException(
                    "Cannot change poll " + id + " from " + poll.getStatus() + " to " + newStatus);
        }
        poll.setStatus(newStatus);
    }
}
