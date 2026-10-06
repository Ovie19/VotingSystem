package dev.fantasma.votingsystem.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePollOptionRequest {
    @NotBlank(message = "Option label is required")
    @Size(max = 255, message = "Option label must not exceed 255 characters")
    private String label;
}
