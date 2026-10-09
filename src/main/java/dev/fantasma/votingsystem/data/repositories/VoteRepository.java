package dev.fantasma.votingsystem.data.repositories;

import dev.fantasma.votingsystem.data.models.Vote;
import dev.fantasma.votingsystem.dtos.responses.OptionVoteCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {
    boolean existsByUserIdAndPollId(UUID userId, UUID pollId);

    @Query("""
        select v.option.id as optionId, count(v) as votes
        from Vote v
        where v.poll.id = :pollId
        group by v.option.id
        """)
    List<OptionVoteCount> countVotesByOption(@Param("pollId") UUID pollId);
}
