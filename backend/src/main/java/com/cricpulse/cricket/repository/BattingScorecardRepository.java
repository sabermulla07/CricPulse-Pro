package com.cricpulse.cricket.repository;

import com.cricpulse.cricket.model.BattingScorecard;
import com.cricpulse.cricket.model.MatchInnings;
import com.cricpulse.cricket.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BattingScorecardRepository extends JpaRepository<BattingScorecard, Long> {
    List<BattingScorecard> findByInningsOrderByBattingOrderAsc(MatchInnings innings);
    List<BattingScorecard> findByInningsIdOrderByBattingOrderAsc(Long inningsId);
    Optional<BattingScorecard> findByInningsAndPlayer(MatchInnings innings, Player player);
    Optional<BattingScorecard> findByInningsAndIsStrikerTrue(MatchInnings innings);
    List<BattingScorecard> findByInningsAndIsOnCreaseTrue(MatchInnings innings);
    Optional<BattingScorecard> findFirstByInningsAndIsOnCreaseFalseAndIsOutFalseOrderByBattingOrderAsc(MatchInnings innings);
}
