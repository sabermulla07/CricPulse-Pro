package com.cricpulse.cricket.repository;

import com.cricpulse.cricket.model.BowlingScorecard;
import com.cricpulse.cricket.model.MatchInnings;
import com.cricpulse.cricket.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BowlingScorecardRepository extends JpaRepository<BowlingScorecard, Long> {
    List<BowlingScorecard> findByInningsOrderByBowlingOrderAsc(MatchInnings innings);
    List<BowlingScorecard> findByInningsIdOrderByBowlingOrderAsc(Long inningsId);
    Optional<BowlingScorecard> findByInningsAndPlayer(MatchInnings innings, Player player);
    Optional<BowlingScorecard> findByInningsAndIsCurrentBowlerTrue(MatchInnings innings);
}
