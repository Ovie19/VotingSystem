package dev.fantasma.votingsystem.controllers;

import dev.fantasma.votingsystem.dtos.requests.CreatePollRequest;
import dev.fantasma.votingsystem.dtos.responses.PollResponse;
import dev.fantasma.votingsystem.exceptions.VotingSystemException;
import dev.fantasma.votingsystem.services.PollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/polls")
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PollResponse> createPoll(@Valid @RequestBody CreatePollRequest request)
            throws VotingSystemException {

        PollResponse response = pollService.createPoll(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}