package dev.fantasma.votingsystem.dtos.responses;

import dev.fantasma.votingsystem.data.models.Status;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class CancelPollResponse {
    private UUID pollId;
    private Status status;
    private Instant cancelledAt;
    private String title;
}
