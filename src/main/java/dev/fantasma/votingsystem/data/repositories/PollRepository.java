package dev.fantasma.votingsystem.data.repositories;

import dev.fantasma.votingsystem.data.models.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PollRepository extends JpaRepository<PollOption, UUID> {
}
