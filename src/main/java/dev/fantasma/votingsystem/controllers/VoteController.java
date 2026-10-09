package dev.fantasma.votingsystem.controllers;

import dev.fantasma.votingsystem.data.models.User;
import dev.fantasma.votingsystem.dtos.requests.CastVoteRequest;
import dev.fantasma.votingsystem.dtos.responses.ApiResponse;
import dev.fantasma.votingsystem.dtos.responses.VoteResponse;
import dev.fantasma.votingsystem.exceptions.VotingSystemException;
import dev.fantasma.votingsystem.services.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/votes")
@RequiredArgsConstructor
public class VoteController {
    private final VoteService voteService;

    @PostMapping("/{pollId}")
    public ResponseEntity<?> castVote(
            @PathVariable UUID pollId,
            @Valid @RequestBody CastVoteRequest request,
            @AuthenticationPrincipal User principal
    ) throws VotingSystemException {
        VoteResponse response = voteService.castVote(pollId, request.getPollOptionId(), principal.getId());
        return new ResponseEntity<>(ApiResponse.success(response), CREATED);
    }
}
