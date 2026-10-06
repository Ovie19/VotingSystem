package dev.fantasma.votingsystem.data.repositories;

import dev.fantasma.votingsystem.data.models.Poll;
import dev.fantasma.votingsystem.data.models.PollOption;
import dev.fantasma.votingsystem.data.models.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PollRepository extends JpaRepository<Poll, UUID> {
    Page<Poll> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Poll> findAllByStatusOrderByCreatedAtDesc(Status status, Pageable pageable);
}
