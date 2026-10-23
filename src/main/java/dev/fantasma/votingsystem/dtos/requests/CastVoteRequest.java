package dev.fantasma.votingsystem.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CastVoteRequest {
    @NotNull(message = "pollOptionId is required")
    private UUID pollOptionId;
}
