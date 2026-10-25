package com.cricpulse.cricket.repository;

import com.cricpulse.cricket.model.BallEvent;
import com.cricpulse.cricket.model.Match;
import com.cricpulse.cricket.model.MatchInnings;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BallEventRepository extends JpaRepository<BallEvent, Long> {
    List<BallEvent> findByMatchOrderByTimestampDesc(Match match);
    List<BallEvent> findByMatchIdOrderByTimestampDesc(Long matchId);
    List<BallEvent> findByMatchIdOrderByTimestampDesc(Long matchId, Pageable pageable);
    List<BallEvent> findByInningsOrderByTimestampDesc(MatchInnings innings);
    List<BallEvent> findByInningsIdOrderByTimestampDesc(Long inningsId);
    List<BallEvent> findByInningsIdOrderByTimestampAsc(Long inningsId);

    // Filter by wickets or boundaries
    List<BallEvent> findByInningsIdAndIsWicketTrueOrderByTimestampDesc(Long inningsId);

    @Query("SELECT b FROM BallEvent b WHERE b.innings.id = :inningsId AND (b.runsScored = 4 OR b.runsScored = 6) ORDER BY b.timestamp DESC")
    List<BallEvent> findBoundariesByInningsId(@Param("inningsId") Long inningsId);

    // Recent balls in current over
    @Query("SELECT b FROM BallEvent b WHERE b.innings.id = :inningsId AND b.overNumber = :overNumber ORDER BY b.timestamp ASC")
    List<BallEvent> findBallsInOver(@Param("inningsId") Long inningsId, @Param("overNumber") Integer overNumber);
}
