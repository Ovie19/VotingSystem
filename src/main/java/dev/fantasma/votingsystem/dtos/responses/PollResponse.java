package dev.fantasma.votingsystem.dtos.responses;

import dev.fantasma.votingsystem.data.models.Status;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class PollResponse {
    private UUID id;
    private String title;
    private Instant startAt;
    private Instant endAt;
    private Status status;
    private List<PollOptionResponse> options;
}
