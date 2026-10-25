package com.cricpulse.cricket.controller;

import com.cricpulse.cricket.dto.LeaderboardDTO;
import com.cricpulse.cricket.model.Player;
import com.cricpulse.cricket.service.PlayerStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PlayerController {

    private final PlayerStatsService playerStatsService;

    public PlayerController(PlayerStatsService playerStatsService) {
        this.playerStatsService = playerStatsService;
    }

    @GetMapping("/players")
    public ResponseEntity<List<Player>> getPlayers(@RequestParam(required = false) Long teamId) {
        if (teamId != null) {
            return ResponseEntity.ok(playerStatsService.getPlayersByTeam(teamId));
        }
        return ResponseEntity.ok(playerStatsService.getAllPlayers());
    }

    @GetMapping("/players/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        return playerStatsService.getPlayerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<LeaderboardDTO> getLeaderboard() {
        return ResponseEntity.ok(playerStatsService.getTournamentLeaderboard());
    }
}
