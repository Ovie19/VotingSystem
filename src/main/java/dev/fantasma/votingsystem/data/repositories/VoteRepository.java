package dev.fantasma.votingsystem.data.repositories;

import dev.fantasma.votingsystem.data.models.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {
    boolean existsByUserIdAndPollId(UUID userId, UUID pollId);
}
