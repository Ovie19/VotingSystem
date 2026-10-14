package dev.fantasma.votingsystem.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
public class PollOptionResultResponse {
    private UUID id;
    private String label;
    private long votes;
}
