package dev.fantasma.votingsystem.dtos.responses;

import dev.fantasma.votingsystem.data.models.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ClosePollResponse {
    private UUID id;
    private String title;
    private Status status;
    private Instant startAt;
    private Instant endAt;
    private long totalVotes;
    private List<PollOptionResultResponse> options;
}
