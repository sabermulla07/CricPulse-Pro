package com.cricpulse.cricket.service;

import com.cricpulse.cricket.dto.BallRecordRequest;
import com.cricpulse.cricket.dto.MatchDetailDTO;
import com.cricpulse.cricket.model.*;
import com.cricpulse.cricket.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MatchSimulationService {

    private static final Logger log = LoggerFactory.getLogger(MatchSimulationService.class);

    private final MatchRepository matchRepository;
    private final MatchInningsRepository inningsRepository;
    private final BattingScorecardRepository battingRepository;
    private final BowlingScorecardRepository bowlingRepository;
    private final BallEventRepository ballEventRepository;
    private final FallOfWicketRepository fowRepository;
    private final PlayerRepository playerRepository;
    private final ScoringService scoringService;
    private final MatchService matchService;

    private final Random random = new Random();
    private final Map<Long, Long> lastSimulatedTime = new ConcurrentHashMap<>();

    public MatchSimulationService(MatchRepository matchRepository,
                                  MatchInningsRepository inningsRepository,
                                  BattingScorecardRepository battingRepository,
                                  BowlingScorecardRepository bowlingRepository,
                                  BallEventRepository ballEventRepository,
                                  FallOfWicketRepository fowRepository,
                                  PlayerRepository playerRepository,
                                  ScoringService scoringService,
                                  MatchService matchService) {
        this.matchRepository = matchRepository;
        this.inningsRepository = inningsRepository;
        this.battingRepository = battingRepository;
        this.bowlingRepository = bowlingRepository;
        this.ballEventRepository = ballEventRepository;
        this.fowRepository = fowRepository;
        this.playerRepository = playerRepository;
        this.scoringService = scoringService;
        this.matchService = matchService;
    }

    @Scheduled(fixedDelay = 1000)
    public void runLiveSimulationTick() {
        List<Match> activeMatches = matchRepository.findByIsSimulationActiveTrue();
        if (activeMatches.isEmpty()) {
            return;
        }

        long now = System.currentTimeMillis();
        for (Match match : activeMatches) {
            if (match.getStatus() == MatchStatus.COMPLETED || match.getStatus() == MatchStatus.ABANDONED) {
                match.setIsSimulationActive(false);
                matchRepository.save(match);
                continue;
            }

            int speedMs = match.getSimulationSpeedMs() != null ? match.getSimulationSpeedMs() : 2000;
            Long lastTime = lastSimulatedTime.get(match.getId());
            if (lastTime == null || (now - lastTime) >= speedMs) {
                try {
                    stepMatchSimulation(match.getId());
                    lastSimulatedTime.put(match.getId(), now);
                } catch (Exception e) {
                    log.error("Error running simulation step for match {}: {}", match.getId(), e.getMessage());
                }
            }
        }
    }

    @Transactional
    public MatchDetailDTO stepMatchSimulation(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));

        if (match.getStatus() == MatchStatus.COMPLETED) {
            return matchService.getMatchDetails(matchId);
        }

        if (match.getStatus() == MatchStatus.UPCOMING) {
            match.setStatus(MatchStatus.LIVE);
            matchRepository.save(match);
        }

        BallRecordRequest ball = generateRealisticBall();
        return scoringService.recordBall(matchId, ball);
    }

    @Transactional
    public MatchDetailDTO startSimulation(Long matchId, Integer speedMs) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));

        if (match.getStatus() == MatchStatus.COMPLETED) {
            throw new IllegalStateException("Match has already completed. Reset match to simulate again.");
        }

        match.setIsSimulationActive(true);
        match.setStatus(MatchStatus.LIVE);
        if (speedMs != null && speedMs >= 500) {
            match.setSimulationSpeedMs(speedMs);
        }
        matchRepository.save(match);
        return matchService.getMatchDetails(matchId);
    }

    @Transactional
    public MatchDetailDTO pauseSimulation(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));

        match.setIsSimulationActive(false);
        matchRepository.save(match);
        return matchService.getMatchDetails(matchId);
    }

    @Transactional
    public MatchDetailDTO setSpeed(Long matchId, Integer speedMs) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));

        if (speedMs != null && speedMs >= 200) {
            match.setSimulationSpeedMs(speedMs);
            matchRepository.save(match);
        }
        return matchService.getMatchDetails(matchId);
    }

    @Transactional
    public MatchDetailDTO resetMatch(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));

        match.setIsSimulationActive(false);
        match.setStatus(MatchStatus.LIVE);
        match.setCurrentInnings(1);
        match.setTarget(0);
        match.setResultSummary(null);
        match.setPlayerOfTheMatch(null);

        // Delete ball events and fall of wickets
        ballEventRepository.deleteAll(ballEventRepository.findByMatchOrderByTimestampDesc(match));

        List<MatchInnings> inningsList = inningsRepository.findByMatchOrderByInningsNumberAsc(match);
        for (MatchInnings inn : inningsList) {
            fowRepository.deleteAll(fowRepository.findByInningsOrderByWicketNumberAsc(inn));

            inn.setTotalRuns(0);
            inn.setTotalWickets(0);
            inn.setLegalBalls(0);
            inn.setOversBowled(0.0);
            inn.setExtras(0);
            inn.setWides(0);
            inn.setNoBalls(0);
            inn.setByes(0);
            inn.setLegByes(0);
            inn.setCurrentRunRate(0.0);
            inn.setRequiredRunRate(0.0);
            inn.setIsCompleted(false);
            inningsRepository.save(inn);

            // Reset Batting scorecards
            List<BattingScorecard> batList = battingRepository.findByInningsOrderByBattingOrderAsc(inn);
            for (BattingScorecard b : batList) {
                b.setRuns(0);
                b.setBalls(0);
                b.setFours(0);
                b.setSixes(0);
                b.setStrikeRate(0.0);
                b.setIsOut(false);
                b.setDismissal("not out");
                b.setDismissalType(null);
                b.setBowler(null);
                b.setFielder(null);

                // Set openers on crease
                if (b.getBattingOrder() != null && b.getBattingOrder() == 1) {
                    b.setIsOnCrease(true);
                    b.setIsStriker(true);
                } else if (b.getBattingOrder() != null && b.getBattingOrder() == 2) {
                    b.setIsOnCrease(true);
                    b.setIsStriker(false);
                } else {
                    b.setIsOnCrease(false);
                    b.setIsStriker(false);
                }
                battingRepository.save(b);
            }

            // Reset Bowling scorecards
            List<BowlingScorecard> bowList = bowlingRepository.findByInningsOrderByBowlingOrderAsc(inn);
            for (BowlingScorecard bow : bowList) {
                bow.setOvers(0.0);
                bow.setLegalBalls(0);
                bow.setMaidens(0);
                bow.setRunsConceded(0);
                bow.setWickets(0);
                bow.setEconomy(0.0);
                bow.setDots(0);
                bow.setFoursConceded(0);
                bow.setSixesConceded(0);
                bow.setWides(0);
                bow.setNoBalls(0);
                bow.setIsCurrentBowler(bow.getBowlingOrder() != null && bow.getBowlingOrder() == 1);
                bowlingRepository.save(bow);
            }
        }

        matchRepository.save(match);
        return matchService.getMatchDetails(matchId);
    }

    private BallRecordRequest generateRealisticBall() {
        int roll = random.nextInt(100);

        // Realistic T20 probability breakdown
        if (roll < 34) {
            // Dot ball (34%)
            return new BallRecordRequest(0, false, null, false, null, 0, null);
        } else if (roll < 66) {
            // 1 run (32%)
            return new BallRecordRequest(1, false, null, false, null, 0, null);
        } else if (roll < 76) {
            // 2 runs (10%)
            return new BallRecordRequest(2, false, null, false, null, 0, null);
        } else if (roll < 77) {
            // 3 runs (1%)
            return new BallRecordRequest(3, false, null, false, null, 0, null);
        } else if (roll < 88) {
            // Four (11%)
            return new BallRecordRequest(4, false, null, false, null, 0, null);
        } else if (roll < 93) {
            // Six (5%)
            return new BallRecordRequest(6, false, null, false, null, 0, null);
        } else if (roll < 97) {
            // Wicket (4%)
            String[] wTypes = {"CAUGHT", "BOWLED", "LBW", "RUN_OUT", "CAUGHT"};
            String wType = wTypes[random.nextInt(wTypes.length)];
            return new BallRecordRequest(0, true, wType, false, null, 0, null);
        } else {
            // Extra (3%) - Wide or No Ball
            boolean isWide = random.nextBoolean();
            return new BallRecordRequest(0, false, null, true, isWide ? "WIDE" : "NO_BALL", 1, null);
        }
    }
}
