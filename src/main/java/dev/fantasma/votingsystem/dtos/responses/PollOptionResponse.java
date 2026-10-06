package dev.fantasma.votingsystem.dtos.responses;

import lombok.Data;

import java.util.UUID;

@Data
public class PollOptionResponse {
    private UUID id;
    private String label;
}
