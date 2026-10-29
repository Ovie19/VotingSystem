package dev.fantasma.votingsystem.data.repositories;

import dev.fantasma.votingsystem.data.models.Poll;
import dev.fantasma.votingsystem.data.models.Status;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PollRepository extends JpaRepository<Poll, UUID> {
    Page<Poll> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Poll> findAllByStatusOrderByCreatedAtDesc(Status status, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select p from Poll p where p.id = :id")
    Optional<Poll> findByIdForShare(@Param("id") UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Poll p where p.id = :id")
    Optional<Poll> findByIdForUpdate(@Param("id") UUID id);

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
