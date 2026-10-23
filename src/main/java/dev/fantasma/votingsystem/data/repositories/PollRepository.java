package dev.fantasma.votingsystem.data.repositories;

import dev.fantasma.votingsystem.data.models.Poll;
import dev.fantasma.votingsystem.data.models.PollOption;
import dev.fantasma.votingsystem.data.models.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PollRepository extends JpaRepository<Poll, UUID> {
    Page<Poll> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Poll> findAllByStatusOrderByCreatedAtDesc(Status status, Pageable pageable);

    @Query("""
    SELECT p.id
    FROM Poll p
    WHERE p.startAt < :startAt
    AND p.status = :status
    ORDER BY p.createdAt DESC
    """)
    List<UUID> findIdsByStartAtBeforeAndStatus(
            @Param("startAt") Instant startAt,
            @Param("status") Status status);

    @Query("""
    SELECT p.id
    FROM Poll p
    WHERE p.endAt < :endAt
    AND p.status = :status
    ORDER BY p.createdAt DESC
    """)
    List<UUID> findIdsByEndAtBeforeAndStatus(
            @Param("endAt") Instant endAt,
            @Param("status") Status status);
}
