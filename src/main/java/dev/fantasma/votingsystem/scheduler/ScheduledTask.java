package dev.fantasma.votingsystem.scheduler;

import dev.fantasma.votingsystem.exceptions.VotingSystemException;
import dev.fantasma.votingsystem.services.PollService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ScheduledTask {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);
    private final PollService pollService;

    @Scheduled(fixedRate = 60000)
    public void startScheduledTaskBeforeNow() {
        List<UUID> pollIds = pollService.getScheduledPollIdsBefore(Instant.now());
        for (UUID pollId : pollIds) {
            try {
                pollService.startPoll(pollId);
            } catch(VotingSystemException e) {
                log.error("Error starting poll {}, reason: {}", pollId, e.getMessage());
            }
        }
    }

    @Scheduled(fixedRate = 60_000)
    public void closeOverdueActivePolls() {
        List<UUID> pollIds = pollService.getActivePollIdsBefore(Instant.now());
        for (UUID pollId : pollIds) {
            try {
                pollService.endPoll(pollId);
            } catch (VotingSystemException e) {
                log.error("Error ending poll {}, reason: {}", pollId, e.getMessage());
            }
        }
    }
}
