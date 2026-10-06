package dev.fantasma.votingsystem.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class CreatePollRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotEmpty(message = "At least two options are required")
    @Size(min = 2, message = "A poll must have at least two options")
    @Valid
    private List<CreatePollOptionRequest> options;

    private Instant startAt;
    private Instant endAt;
}
