package dev.fantasma.votingsystem.services;

import dev.fantasma.votingsystem.data.models.*;
import dev.fantasma.votingsystem.data.repositories.PollOptionRepository;
import dev.fantasma.votingsystem.data.repositories.PollRepository;
import dev.fantasma.votingsystem.data.repositories.VoteRepository;
import dev.fantasma.votingsystem.dtos.responses.VoteResponse;
import dev.fantasma.votingsystem.exceptions.AlreadyVotedException;
import dev.fantasma.votingsystem.exceptions.ResourceNotFoundException;
import dev.fantasma.votingsystem.exceptions.VotingSystemException;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static dev.fantasma.votingsystem.utils.Mapper.map;
import static dev.fantasma.votingsystem.utils.Validator.validatePollEndAt;

@Service
@AllArgsConstructor
public class VotingServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final EntityManager entityManager;

    @Override
    public VoteResponse castVote(UUID pollId, UUID pollOptionId, UUID userId) throws VotingSystemException {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found"));
        poll.getStatus().validatePollState();

        PollOption pollOption = pollOptionRepository.findByIdAndPoll(pollOptionId, poll)
                .orElseThrow(() -> new ResourceNotFoundException("Poll option not found"));
        boolean hasVotedAlready = voteRepository.existsByUserIdAndPollId(userId, pollId);
        if (hasVotedAlready) throw new AlreadyVotedException("You have already voted");

        validatePollEndAt(poll.getEndAt());

        User user = entityManager.find(User.class, userId);
        Vote vote = map(user, poll, pollOption);
        try {
            vote = voteRepository.saveAndFlush(vote);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyVotedException("You have already voted");
        }
        return map(vote);
    }
}
