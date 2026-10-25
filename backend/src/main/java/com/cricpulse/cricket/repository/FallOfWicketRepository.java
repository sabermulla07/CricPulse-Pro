package com.cricpulse.cricket.repository;

import com.cricpulse.cricket.model.FallOfWicket;
import com.cricpulse.cricket.model.MatchInnings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FallOfWicketRepository extends JpaRepository<FallOfWicket, Long> {
    List<FallOfWicket> findByInningsOrderByWicketNumberAsc(MatchInnings innings);
    List<FallOfWicket> findByInningsIdOrderByWicketNumberAsc(Long inningsId);
}
