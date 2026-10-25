package com.cricpulse.cricket.repository;

import com.cricpulse.cricket.model.Match;
import com.cricpulse.cricket.model.MatchInnings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchInningsRepository extends JpaRepository<MatchInnings, Long> {
    List<MatchInnings> findByMatchOrderByInningsNumberAsc(Match match);
    List<MatchInnings> findByMatchIdOrderByInningsNumberAsc(Long matchId);
    Optional<MatchInnings> findByMatchAndInningsNumber(Match match, Integer inningsNumber);
    Optional<MatchInnings> findByMatchIdAndInningsNumber(Long matchId, Integer inningsNumber);
}
