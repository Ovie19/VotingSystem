package dev.fantasma.votingsystem.controllers;

import dev.fantasma.votingsystem.dtos.requests.CreatePollRequest;
import dev.fantasma.votingsystem.dtos.responses.ApiResponse;
import dev.fantasma.votingsystem.dtos.responses.ClosePollResponse;
import dev.fantasma.votingsystem.dtos.responses.PollResponse;
import dev.fantasma.votingsystem.exceptions.VotingSystemException;
import dev.fantasma.votingsystem.services.PollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/polls")
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPoll(@Valid @RequestBody CreatePollRequest request) throws VotingSystemException {
        PollResponse response = pollService.createPoll(request);
        return new ResponseEntity<>(ApiResponse.success(response), CREATED);
    }

    @PostMapping("/{pollId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> closePoll(@PathVariable UUID pollId) throws VotingSystemException {
        ClosePollResponse response = pollService.closePoll(pollId);
        return new ResponseEntity<>(ApiResponse.success(response), OK);
    }
}