package com.cricpulse.cricket.repository;

import com.cricpulse.cricket.model.Player;
import com.cricpulse.cricket.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByTeam(Team team);
    List<Player> findByTeamId(Long teamId);
    Optional<Player> findByName(String name);

    // Top run scorers
    @Query("SELECT p FROM Player p ORDER BY p.runsScored DESC")
    List<Player> findTopRunScorers();

    // Top wicket takers
    @Query("SELECT p FROM Player p ORDER BY p.wicketsTaken DESC")
    List<Player> findTopWicketTakers();
}
