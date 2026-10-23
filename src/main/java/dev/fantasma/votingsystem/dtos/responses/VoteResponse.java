package dev.fantasma.votingsystem.dtos.responses;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class VoteResponse {
    private UUID voteId;
    private UUID pollId;
    private Instant votedAt;
}
