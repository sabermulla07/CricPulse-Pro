package com.cricpulse.cricket.controller;

import com.cricpulse.cricket.dto.BallRecordRequest;
import com.cricpulse.cricket.dto.CommentaryDTO;
import com.cricpulse.cricket.dto.MatchDetailDTO;
import com.cricpulse.cricket.dto.ScorecardResponseDTO;
import com.cricpulse.cricket.model.Match;
import com.cricpulse.cricket.service.MatchService;
import com.cricpulse.cricket.service.MatchSimulationService;
import com.cricpulse.cricket.service.ScoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
public class MatchController {

    private final MatchService matchService;
    private final ScoringService scoringService;
    private final MatchSimulationService simulationService;

    public MatchController(MatchService matchService,
                           ScoringService scoringService,
                           MatchSimulationService simulationService) {
        this.matchService = matchService;
        this.scoringService = scoringService;
        this.simulationService = simulationService;
    }

    @GetMapping
    public ResponseEntity<List<Match>> getAllMatches(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(matchService.getAllMatches(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchDetailDTO> getMatchDetails(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getMatchDetails(id));
    }

    @GetMapping("/{id}/scorecard")
    public ResponseEntity<ScorecardResponseDTO> getMatchScorecard(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getFullScorecard(id));
    }

    @GetMapping("/{id}/commentary")
    public ResponseEntity<List<CommentaryDTO>> getCommentary(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "ALL") String filter,
            @RequestParam(required = false, defaultValue = "30") int limit) {
        return ResponseEntity.ok(matchService.getCommentary(id, filter, limit));
    }

    @PostMapping("/{id}/ball")
    public ResponseEntity<MatchDetailDTO> recordBall(
            @PathVariable Long id,
            @RequestBody BallRecordRequest request) {
        MatchDetailDTO updated = scoringService.recordBall(id, request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/simulate/start")
    public ResponseEntity<MatchDetailDTO> startSimulation(
            @PathVariable Long id,
            @RequestParam(required = false) Integer speedMs) {
        MatchDetailDTO updated = simulationService.startSimulation(id, speedMs);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/simulate/pause")
    public ResponseEntity<MatchDetailDTO> pauseSimulation(@PathVariable Long id) {
        MatchDetailDTO updated = simulationService.pauseSimulation(id);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/simulate/step")
    public ResponseEntity<MatchDetailDTO> stepSimulation(@PathVariable Long id) {
        MatchDetailDTO updated = simulationService.stepMatchSimulation(id);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/simulate/speed")
    public ResponseEntity<MatchDetailDTO> setSimulationSpeed(
            @PathVariable Long id,
            @RequestParam Integer speedMs) {
        MatchDetailDTO updated = simulationService.setSpeed(id, speedMs);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/reset")
    public ResponseEntity<MatchDetailDTO> resetMatch(@PathVariable Long id) {
        MatchDetailDTO updated = simulationService.resetMatch(id);
        return ResponseEntity.ok(updated);
    }
}
