package com.cricpulse.cricket.service;

import com.cricpulse.cricket.dto.LeaderboardDTO;
import com.cricpulse.cricket.model.Player;
import com.cricpulse.cricket.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PlayerStatsService {

    private final PlayerRepository playerRepository;

    public PlayerStatsService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Optional<Player> getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    public List<Player> getPlayersByTeam(Long teamId) {
        return playerRepository.findByTeamId(teamId);
    }

    public LeaderboardDTO getTournamentLeaderboard() {
        List<Player> allPlayers = playerRepository.findAll();
        LeaderboardDTO dto = new LeaderboardDTO();

        // Top 5 Run Scorers (Orange Cap)
        List<Player> topBatsmen = allPlayers.stream()
                .filter(p -> p.getRunsScored() != null && p.getRunsScored() > 0)
                .sorted(Comparator.comparingInt(Player::getRunsScored).reversed())
                .limit(5)
                .collect(Collectors.toList());
        dto.setTopBatsmen(topBatsmen);

        // Top 5 Wicket Takers (Purple Cap)
        List<Player> topBowlers = allPlayers.stream()
                .filter(p -> p.getWicketsTaken() != null && p.getWicketsTaken() > 0)
                .sorted(Comparator.comparingInt(Player::getWicketsTaken).reversed())
                .limit(5)
                .collect(Collectors.toList());
        dto.setTopBowlers(topBowlers);

        // Highest Strike Rates (min 50 runs)
        List<Player> topStrikeRates = allPlayers.stream()
                .filter(p -> p.getRunsScored() != null && p.getRunsScored() >= 50 && p.getBattingStrikeRate() != null)
                .sorted(Comparator.comparingDouble(Player::getBattingStrikeRate).reversed())
                .limit(5)
                .collect(Collectors.toList());
        dto.setHighestStrikeRates(topStrikeRates);

        // Best Economy Bowlers (min 3 wickets)
        List<Player> bestEconomy = allPlayers.stream()
                .filter(p -> p.getWicketsTaken() != null && p.getWicketsTaken() >= 2 && p.getBowlingEconomy() != null && p.getBowlingEconomy() > 0)
                .sorted(Comparator.comparingDouble(Player::getBowlingEconomy))
                .limit(5)
                .collect(Collectors.toList());
        dto.setBestEconomyBowlers(bestEconomy);

        return dto;
    }
}
