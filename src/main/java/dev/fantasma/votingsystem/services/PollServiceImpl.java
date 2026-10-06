package dev.fantasma.votingsystem.services;

import dev.fantasma.votingsystem.data.models.Poll;
import dev.fantasma.votingsystem.data.models.PollOption;
import dev.fantasma.votingsystem.data.models.Status;
import dev.fantasma.votingsystem.data.repositories.PollRepository;
import dev.fantasma.votingsystem.dtos.requests.CreatePollOptionRequest;
import dev.fantasma.votingsystem.dtos.requests.CreatePollRequest;
import dev.fantasma.votingsystem.dtos.responses.PollResponse;
import dev.fantasma.votingsystem.exceptions.VotingSystemException;
import dev.fantasma.votingsystem.utils.Mapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static dev.fantasma.votingsystem.data.models.Status.*;
import static dev.fantasma.votingsystem.utils.Mapper.map;
import static dev.fantasma.votingsystem.utils.Validator.validate;

@Service
@AllArgsConstructor
public class PollServiceImpl implements PollService {

    private final PollRepository pollRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public PollResponse createPoll(CreatePollRequest request) throws VotingSystemException {
        validate(request);
        Poll poll = map(request);

        if (request.getStartAt() == null) poll.setStartAt(Instant.now());

        boolean isActive = request.getStartAt() == null || request.getStartAt().isBefore(Instant.now());
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
}
