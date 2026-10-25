package com.cricpulse.cricket.repository;

import com.cricpulse.cricket.model.Match;
import com.cricpulse.cricket.model.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByStatus(MatchStatus status);
    List<Match> findAllByOrderByCreatedAtDesc();
    List<Match> findByIsSimulationActiveTrue();
}
