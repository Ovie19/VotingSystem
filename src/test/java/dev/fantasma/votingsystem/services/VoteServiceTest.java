package dev.fantasma.votingsystem.services;

import dev.fantasma.votingsystem.data.models.Poll;
import dev.fantasma.votingsystem.data.models.PollOption;
import dev.fantasma.votingsystem.data.models.Status;
import dev.fantasma.votingsystem.data.models.Vote;
import dev.fantasma.votingsystem.data.repositories.PollOptionRepository;
import dev.fantasma.votingsystem.data.repositories.PollRepository;
import dev.fantasma.votingsystem.data.repositories.VoteRepository;
import dev.fantasma.votingsystem.dtos.responses.VoteResponse;
import dev.fantasma.votingsystem.exceptions.AlreadyVotedException;
import dev.fantasma.votingsystem.exceptions.InvalidPollStateException;
import dev.fantasma.votingsystem.exceptions.ResourceNotFoundException;
import dev.fantasma.votingsystem.exceptions.VotingSystemException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {
    @Mock
    private VoteRepository voteRepository;
    @Mock
    private PollRepository pollRepository;
    @Mock
    private PollOptionRepository pollOptionRepository;
    @Mock
    private EntityManager entityManager;
    @InjectMocks
    private VotingServiceImpl votingService;

    private final UUID userId = UUID.randomUUID();
    private final UUID pollId = UUID.randomUUID();
    private final UUID pollOptionId = UUID.randomUUID();

    @Test
    void invalidPollId_castVoteThrowsException() {
        UUID invalidPollId = UUID.randomUUID();
        when(pollRepository.findById(invalidPollId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> votingService.castVote(invalidPollId, pollOptionId, userId)
        );
        assertEquals("Poll not found", exception.getMessage());

        verify(pollRepository).findById(invalidPollId);
        verifyNoInteractions(pollOptionRepository);
        verifyNoInteractions(voteRepository);
    }

    @Test
    void pollStatusIsSCHEDULED_castVoteThrowsException() {
        Poll poll = buildPoll(Status.SCHEDULED);
        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));

        InvalidPollStateException exception = assertThrows(
                InvalidPollStateException.class,
                () -> votingService.castVote(pollId, pollOptionId, userId)
        );
        assertEquals("This poll has not started yet", exception.getMessage());

        verify(pollRepository).findById(pollId);
        verifyNoInteractions(pollOptionRepository);
        verifyNoInteractions(voteRepository);
    }

    @Test
    void pollStatusIsCOMPLETED_castVoteThrowsException() {
        Poll poll = buildPoll(Status.COMPLETED);
        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));

        InvalidPollStateException exception = assertThrows(
                InvalidPollStateException.class,
                () -> votingService.castVote(pollId, pollOptionId, userId)
        );
        assertEquals("This poll has ended", exception.getMessage());

        verify(pollRepository).findById(pollId);
        verifyNoInteractions(pollOptionRepository);
        verifyNoInteractions(voteRepository);
    }

    @Test
    void pollStatusIsCANCELED_castVoteThrowsException() {
        Poll poll = buildPoll(Status.CANCELED);
        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));

        InvalidPollStateException exception = assertThrows(
                InvalidPollStateException.class,
                () -> votingService.castVote(pollId, pollOptionId, userId)
        );
        assertEquals("This poll has been canceled", exception.getMessage());

        verify(pollRepository).findById(pollId);
        verifyNoInteractions(pollOptionRepository);
        verifyNoInteractions(voteRepository);
    }

    @Test
    void invalidPollOptionId_castVoteThrowsException() {
        Poll poll = buildPoll(Status.ACTIVE);
        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(pollOptionRepository.findByIdAndPoll(pollOptionId, poll))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> votingService.castVote(pollId, pollOptionId, userId)
        );
        assertEquals("Poll option not found", exception.getMessage());

        verify(pollRepository).findById(pollId);
        verify(pollOptionRepository).findByIdAndPoll(pollOptionId, poll);
        verifyNoInteractions(voteRepository);
    }

    @Test
    void userHasAlreadyVoted_castVoteThrowsException() {
        Poll poll = buildPoll(Status.ACTIVE);
        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(pollOptionRepository.findByIdAndPoll(pollOptionId, poll))
                .thenReturn(Optional.of(new PollOption()));
        when(voteRepository.existsByUserIdAndPollId(userId, pollId)).thenReturn(true);

        AlreadyVotedException exception = assertThrows(
                AlreadyVotedException.class,
                () -> votingService.castVote(pollId, pollOptionId, userId)
        );
        assertEquals("You have already voted", exception.getMessage());

        verify(pollRepository).findById(pollId);
        verify(pollOptionRepository).findByIdAndPoll(pollOptionId, poll);
        verify(voteRepository).existsByUserIdAndPollId(userId, pollId);
        verify(voteRepository, never()).saveAndFlush(any(Vote.class));
        verifyNoMoreInteractions(voteRepository);
    }

    @Test
    void duplicateVote_databaseConstraintViolation_throwsAlreadyVotedException() {
        Poll poll = buildPoll(Status.ACTIVE);

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(pollOptionRepository.findByIdAndPoll(pollOptionId, poll)).thenReturn(Optional.of(new PollOption()));
        when(voteRepository.existsByUserIdAndPollId(userId, pollId)).thenReturn(false);
        when(voteRepository.saveAndFlush(any(Vote.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        AlreadyVotedException exception = assertThrows(
                AlreadyVotedException.class,
                () -> votingService.castVote(pollId, pollOptionId, userId)
        );
        assertEquals("You have already voted", exception.getMessage());

        verify(pollRepository).findById(pollId);
        verify(pollOptionRepository).findByIdAndPoll(pollOptionId, poll);
        verify(voteRepository).existsByUserIdAndPollId(userId, pollId);
        verify(voteRepository).saveAndFlush(any(Vote.class));
    }

    @Test
    void castVoteSuccessfullyTest() throws VotingSystemException {
        Poll poll = buildPoll(Status.ACTIVE);
        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(pollOptionRepository.findByIdAndPoll(pollOptionId, poll)).thenReturn(Optional.of(new PollOption()));
        when(voteRepository.existsByUserIdAndPollId(userId, pollId)).thenReturn(false);
        when(voteRepository.saveAndFlush(any(Vote.class))).thenAnswer(i -> i.getArgument(0));

        VoteResponse response = votingService.castVote(pollId, pollOptionId, userId);

        assertNotNull(response);
        assertEquals(pollId, response.getPollId());

        verify(pollRepository).findById(pollId);
        verify(pollOptionRepository).findByIdAndPoll(pollOptionId, poll);
        verify(voteRepository).existsByUserIdAndPollId(userId, pollId);
        verify(voteRepository).saveAndFlush(any(Vote.class));
        verifyNoMoreInteractions(pollRepository, pollOptionRepository, voteRepository);
    }

    private Poll buildPoll(Status status) {
        Poll poll = new Poll();
        poll.setId(pollId);
        poll.setTitle("Test poll");
        poll.setStatus(status);
        return poll;
    }
}