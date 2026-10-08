package dev.fantasma.votingsystem.scheduler;

import dev.fantasma.votingsystem.services.PollService;
import lombok.AllArgsConstructor;
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
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private final PollService pollService;

    @Scheduled(fixedRate = 60000)
    public void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()));
    }

    @Scheduled(fixedRate = 60000)
    public void startScheduledTaskBeforeNow() {
        List<UUID> pollIds = pollService.getScheduledPollIdsBefore(Instant.now());
        pollIds.forEach(pollService::startPoll);
    }

    @Scheduled(fixedRate = 60000)
    public void endActiveTaskBeforeNow() {
        List<UUID> pollIds = pollService.getActivePollIdsBefore(Instant.now());
        pollIds.forEach(pollService::endPoll);
    }
}
