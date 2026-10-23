package dev.fantasma.votingsystem.services;

import dev.fantasma.votingsystem.data.models.Poll;
import dev.fantasma.votingsystem.data.models.Status;
import dev.fantasma.votingsystem.data.repositories.PollRepository;
import dev.fantasma.votingsystem.dtos.requests.CreatePollOptionRequest;
import dev.fantasma.votingsystem.dtos.requests.CreatePollRequest;
import dev.fantasma.votingsystem.dtos.responses.CancelPollResponse;
import dev.fantasma.votingsystem.dtos.responses.PollResponse;
import dev.fantasma.votingsystem.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static dev.fantasma.votingsystem.data.models.Status.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PollServiceTest {

    @Mock
    private PollRepository pollRepository;

    @InjectMocks
    private PollServiceImpl pollService;

    private CreatePollRequest request;

    @BeforeEach
    void setUp() {
        List<String> options = List.of("Option A", "Option B", "Option C");
        List<CreatePollOptionRequest> requests = new ArrayList<>();
        for (String option : options) requests.add(newOption(option));

        request = new CreatePollRequest();
        request.setTitle("Test Title");
        request.setOptions(requests);
        request.setStartAt(Instant.now().plusSeconds(10000));
        request.setEndAt(Instant.now().plusSeconds(50000));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "aaa",
            " aaa   ",
    })
    void createPoll_invalidPollTitle_throwsExceptionTest(String pollTitle) {
        request.setTitle(pollTitle);
        assertThatThrownBy(() -> pollService.createPoll(request))
                .isInstanceOf(InvalidRequestException.class);
    }

    @ParameterizedTest
    @MethodSource("invalidInstant")
    void createPoll_startAtIsBeforeNow_throwsExceptionTest(Instant startAt) {
        request.setStartAt(startAt);
        assertThatThrownBy(() -> pollService.createPoll(request))
                .isInstanceOf(InvalidPollScheduleException.class)
                .hasMessage("Start date cannot be more than 1 minute(s) in the past");
    }

    @ParameterizedTest
    @MethodSource("invalidInstant")
    void createPoll_endAtIsBeforeNow_throwsExceptionTest(Instant endAt) {
        request.setEndAt(endAt);
        assertThatThrownBy(() -> pollService.createPoll(request))
                .isInstanceOf(InvalidPollScheduleException.class)
                .hasMessage("End date cannot be before start date");
    }

    @Test
    void createPoll_endAtIsBeforeStartAt_throwsExceptionTest() {
        request.setEndAt(Instant.now().plusSeconds(1000));
        assertThatThrownBy(() -> pollService.createPoll(request))
                .isInstanceOf(InvalidPollScheduleException.class)
                .hasMessage("End date cannot be before start date");
    }

    @ParameterizedTest
    @MethodSource("getStartAtAndEndAt")
    void createPoll_endAtIsLessThanFiveMinutesToStartAt_throwsExceptionTest(Instant startAt, Instant endAt) {
        request.setStartAt(startAt);
        request.setEndAt(endAt);
        assertThatThrownBy(() -> pollService.createPoll(request))
                .isInstanceOf(InvalidPollScheduleException.class)
                .hasMessage("Poll duration cannot be less than 5 minutes");
    }

    @ParameterizedTest
    @MethodSource("invalidOptions")
    void createPoll_optionsIsNullOrLessThanTwo_throwsExceptionTest(List<CreatePollOptionRequest> options) {
        request.setOptions(options);
        assertThatThrownBy(() -> pollService.createPoll(request))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void createPoll_optionsAreNotDistinct_throwsExceptionTest() {
        CreatePollOptionRequest pollOptionOne = new CreatePollOptionRequest();
        pollOptionOne.setLabel("Option 1");
        CreatePollOptionRequest pollOptionTwo = new CreatePollOptionRequest();
        pollOptionTwo.setLabel("  Option 1   ");

        List<CreatePollOptionRequest> options = new ArrayList<>();
        options.add(pollOptionOne);
        options.add(pollOptionTwo);

        request.setOptions(options);
        assertThatThrownBy(() -> pollService.createPoll(request))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void createPoll_startAtAndEndAtIsNull_statusIsActiveTest() throws VotingSystemException {
        request.setStartAt(null);
        request.setEndAt(null);

        when(pollRepository.save(any(Poll.class)))
                .then(invocation -> {
                    Poll poll = invocation.getArgument(0);
                    poll.setId(UUID.randomUUID());
                    return poll;
                });

        PollResponse response = pollService.createPoll(request);
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(3, response.getOptions().size());
        assertEquals(ACTIVE, response.getStatus());
        assertTrue(response.getStartAt().isBefore(Instant.now()));
    }

    @Test
    void createPoll_startAtNotNullAndFuture_statusIsScheduledTest() throws VotingSystemException {
        request.setStartAt(Instant.now().plusSeconds(50000));
        request.setEndAt(null);

        when(pollRepository.save(any(Poll.class)))
                .then(invocation -> {
                    Poll poll = invocation.getArgument(0);
                    poll.setId(UUID.randomUUID());
                    return poll;
                });

        PollResponse response = pollService.createPoll(request);
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(3, response.getOptions().size());
        assertEquals(Status.SCHEDULED, response.getStatus());
    }

    @Test
    void shouldReturnAllPollsWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);

        Poll poll1 = new Poll();
        poll1.setId(UUID.randomUUID());
        poll1.setTitle("Poll 1");

        Poll poll2 = new Poll();
        poll2.setId(UUID.randomUUID());
        poll2.setTitle("Poll 2");

        Page<Poll> pollPage = new PageImpl<>(List.of(poll1, poll2), pageable, 2);
        when(pollRepository.findAllByOrderByCreatedAtDesc(pageable)).thenReturn(pollPage);

        Page<PollResponse> result = pollService.getAllPolls(pageable);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Poll 1");
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("Poll 2");
        verify(pollRepository).findAllByOrderByCreatedAtDesc(pageable);
    }

    @Test
    void invalidPollId_closePollThrowsExceptionTest() {
        UUID invalidPollId = UUID.randomUUID();
        when(pollRepository.findById(invalidPollId)).thenReturn(Optional.empty());
        assertThrows(
                ResourceNotFoundException.class,
                () -> pollService.endPoll(invalidPollId)
        );
    }

    @ParameterizedTest
    @CsvSource({
            "SCHEDULED, This poll has not started yet",
            "COMPLETED, This poll has ended",
            "CANCELED, This poll has been canceled"
    })
    void closePoll_whenStatusNotActive_throwsException(Status status, String expectedMessage) {
        Poll poll = new Poll();
        poll.setId(UUID.randomUUID());
        poll.setStatus(status);

        when(pollRepository.findById(poll.getId())).thenReturn(Optional.of(poll));
        InvalidPollStateException ex = assertThrows(InvalidPollStateException.class,
                () -> pollService.closePoll(poll.getId()));

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void cancelPoll_invalidPollIdThrowsException() {
        UUID invalidPollId = UUID.randomUUID();
        when(pollRepository.findById(invalidPollId)).thenReturn(Optional.empty());
        assertThrows(
                ResourceNotFoundException.class,
                () -> pollService.cancelPoll(invalidPollId)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "COMPLETED",
            "CANCELED"
    })
    void cancelPoll_whenStatusNotActiveOrScheduled_throwsException(Status status) {
        Poll poll = new Poll();
        poll.setId(UUID.randomUUID());
        poll.setStatus(status);

        when(pollRepository.findById(poll.getId())).thenReturn(Optional.of(poll));
        InvalidPollStateException ex = assertThrows(InvalidPollStateException.class,
                () -> pollService.cancelPoll(poll.getId()));
        assertEquals("Only scheduled or active polls can be cancelled", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ACTIVE",
            "SCHEDULED"
    })
    void shouldCancelActiveOrScheduledPoll(Status status) throws VotingSystemException {
        UUID pollId = UUID.randomUUID();

        Poll poll = new Poll();
        poll.setId(pollId);
        poll.setTitle("Favorite programming language");
        poll.setStatus(status);

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        CancelPollResponse response = pollService.cancelPoll(pollId);

        assertEquals(pollId, response.getPollId());
        assertEquals(Status.CANCELED, response.getStatus());
        assertNotNull(response.getCancelledAt());
        assertEquals("Favorite programming language", response.getTitle());
        verify(pollRepository).findById(pollId);
    }

    private static Stream<Instant> invalidInstant() {
        return Stream.of(
                Instant.now().minus(Duration.ofMinutes(2)),
                Instant.now().minus(Duration.ofMinutes(10)),
                Instant.now().minus(Duration.ofMinutes(20))
        );
    }

    private static Stream<Arguments> getStartAtAndEndAt() {
        return Stream.of(
                arguments(null, Instant.now().plusSeconds(5)),
                arguments(Instant.now(), Instant.now().plus(Duration.ofMinutes(3))),
                arguments(Instant.now().plus(Duration.ofMinutes(5)), Instant.now().plus(Duration.ofMinutes(9)))
        );
    }

    private static Stream<List<CreatePollOptionRequest>> invalidOptions() {
        CreatePollOptionRequest pollOptionOne = new CreatePollOptionRequest();
        pollOptionOne.setLabel("Option 1");

        return Stream.of(
                null,
                List.of(),
                List.of(pollOptionOne)
        );
    }

    private CreatePollOptionRequest newOption(String label) {
        CreatePollOptionRequest option = new CreatePollOptionRequest();
        option.setLabel(label);
        return option;
    }
}