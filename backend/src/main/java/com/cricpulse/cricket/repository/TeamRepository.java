package com.cricpulse.cricket.repository;

import com.cricpulse.cricket.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByShortCode(String shortCode);
    Optional<Team> findByName(String name);
}
