package com.cricpulse.cricket.service;

import com.cricpulse.cricket.dto.CommentaryDTO;
import com.cricpulse.cricket.dto.MatchDetailDTO;
import com.cricpulse.cricket.dto.ScorecardResponseDTO;
import com.cricpulse.cricket.model.*;
import com.cricpulse.cricket.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;
    private final MatchInningsRepository inningsRepository;
    private final BattingScorecardRepository battingRepository;
    private final BowlingScorecardRepository bowlingRepository;
    private final BallEventRepository ballEventRepository;
    private final FallOfWicketRepository fowRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public MatchService(MatchRepository matchRepository,
                        MatchInningsRepository inningsRepository,
                        BattingScorecardRepository battingRepository,
                        BowlingScorecardRepository bowlingRepository,
                        BallEventRepository ballEventRepository,
                        FallOfWicketRepository fowRepository,
                        TeamRepository teamRepository,
                        PlayerRepository playerRepository) {
        this.matchRepository = matchRepository;
        this.inningsRepository = inningsRepository;
        this.battingRepository = battingRepository;
        this.bowlingRepository = bowlingRepository;
        this.ballEventRepository = ballEventRepository;
        this.fowRepository = fowRepository;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    public List<Match> getAllMatches(String statusFilter) {
        if (statusFilter == null || statusFilter.trim().isEmpty() || "ALL".equalsIgnoreCase(statusFilter)) {
            return matchRepository.findAllByOrderByCreatedAtDesc();
        }
        try {
            MatchStatus status = MatchStatus.valueOf(statusFilter.toUpperCase());
            return matchRepository.findByStatus(status);
        } catch (IllegalArgumentException e) {
            return matchRepository.findAllByOrderByCreatedAtDesc();
        }
    }

    public Optional<Match> getMatchById(Long id) {
        return matchRepository.findById(id);
    }

    public MatchDetailDTO getMatchDetails(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + matchId));

        MatchDetailDTO dto = new MatchDetailDTO();
        dto.setId(match.getId());
        dto.setMatchTitle(match.getMatchTitle());
        dto.setSeriesName(match.getSeriesName());
        dto.setMatchType(match.getMatchType());
        dto.setVenue(match.getVenue());
        dto.setStatus(match.getStatus());
        dto.setTeamA(match.getTeamA());
        dto.setTeamB(match.getTeamB());
        dto.setTossWinner(match.getTossWinner());
        dto.setTossDecision(match.getTossDecision());
        dto.setTotalOvers(match.getTotalOvers());
        dto.setCurrentInnings(match.getCurrentInnings());
        dto.setTarget(match.getTarget());
        dto.setResultSummary(match.getResultSummary());
        dto.setPlayerOfTheMatch(match.getPlayerOfTheMatch());
        dto.setIsSimulationActive(match.getIsSimulationActive());
        dto.setSimulationSpeedMs(match.getSimulationSpeedMs());

        List<MatchInnings> inningsList = inningsRepository.findByMatchOrderByInningsNumberAsc(match);
        if (inningsList.isEmpty()) {
            return dto;
        }

        // Innings 1 Summary
        MatchInnings inn1 = inningsList.get(0);
        dto.setFirstInningsScore(inn1.getBattingTeam().getShortCode() + " " + inn1.getTotalRuns() + "/" +
                inn1.getTotalWickets() + " (" + formatOvers(inn1.getLegalBalls()) + ")");

        // Innings 2 Summary if available
        MatchInnings activeInnings = inn1;
        if (inningsList.size() > 1 && match.getCurrentInnings() == 2) {
            MatchInnings inn2 = inningsList.get(1);
            activeInnings = inn2;
            dto.setSecondInningsScore(inn2.getBattingTeam().getShortCode() + " " + inn2.getTotalRuns() + "/" +
                    inn2.getTotalWickets() + " (" + formatOvers(inn2.getLegalBalls()) + ")");
        } else if (inningsList.size() > 1) {
            MatchInnings inn2 = inningsList.get(1);
            dto.setSecondInningsScore(inn2.getBattingTeam().getShortCode() + " " + inn2.getTotalRuns() + "/" +
                    inn2.getTotalWickets() + " (" + formatOvers(inn2.getLegalBalls()) + ")");
        }

        dto.setBattingTeam(activeInnings.getBattingTeam());
        dto.setBowlingTeam(activeInnings.getBowlingTeam());
        dto.setTotalRuns(activeInnings.getTotalRuns());
        dto.setTotalWickets(activeInnings.getTotalWickets());
        dto.setLegalBalls(activeInnings.getLegalBalls());
        dto.setOversDisplay(formatOvers(activeInnings.getLegalBalls()));
        dto.setExtras(activeInnings.getExtras());
        dto.setCurrentRunRate(activeInnings.getCurrentRunRate());
        dto.setRequiredRunRate(activeInnings.getRequiredRunRate());

        // Target Equation calculation if 2nd innings
        if (match.getCurrentInnings() == 2 && match.getTarget() != null && match.getTarget() > 0) {
            int runsNeeded = match.getTarget() - activeInnings.getTotalRuns();
            int totalBalls = match.getTotalOvers() * 6;
            int ballsRemaining = Math.max(0, totalBalls - activeInnings.getLegalBalls());

            if (match.getStatus() == MatchStatus.COMPLETED) {
                dto.setTargetEquation(match.getResultSummary());
            } else if (runsNeeded <= 0) {
                dto.setTargetEquation(activeInnings.getBattingTeam().getName() + " reached the target!");
            } else {
                dto.setTargetEquation(activeInnings.getBattingTeam().getShortCode() + " need " + runsNeeded + " runs in " +
                        ballsRemaining + " balls (RRR: " + (ballsRemaining > 0 ? String.format("%.2f", (double) runsNeeded / ballsRemaining * 6.0) : "N/A") + ")");
            }
        }

        // Active Batters
        List<BattingScorecard> onCreaseBatters = battingRepository.findByInningsAndIsOnCreaseTrue(activeInnings);
        for (BattingScorecard b : onCreaseBatters) {
            MatchDetailDTO.BatterLiveDTO batterDTO = new MatchDetailDTO.BatterLiveDTO(
                    b.getPlayer().getId(),
                    b.getPlayer().getName(),
                    b.getRuns(),
                    b.getBalls(),
                    b.getFours(),
                    b.getSixes(),
                    b.getStrikeRate(),
                    b.getIsStriker()
            );
            if (Boolean.TRUE.equals(b.getIsStriker())) {
                dto.setStriker(batterDTO);
            } else {
                dto.setNonStriker(batterDTO);
            }
        }

        // If one of striker/non-striker wasn't marked, fallback
        if (dto.getStriker() == null && !onCreaseBatters.isEmpty()) {
            BattingScorecard first = onCreaseBatters.get(0);
            dto.setStriker(new MatchDetailDTO.BatterLiveDTO(
                    first.getPlayer().getId(), first.getPlayer().getName(),
                    first.getRuns(), first.getBalls(), first.getFours(), first.getSixes(), first.getStrikeRate(), true
            ));
        }

        // Active Bowler
        Optional<BowlingScorecard> currentBowler = bowlingRepository.findByInningsAndIsCurrentBowlerTrue(activeInnings);
        if (currentBowler.isPresent()) {
            BowlingScorecard bow = currentBowler.get();
            dto.setCurrentBowler(new MatchDetailDTO.BowlerLiveDTO(
                    bow.getPlayer().getId(),
                    bow.getPlayer().getName(),
                    formatOvers(bow.getLegalBalls()),
                    bow.getMaidens(),
                    bow.getRunsConceded(),
                    bow.getWickets(),
                    bow.getEconomy()
            ));
        }

        // Recent balls in the current over
        int currentOverNumber = activeInnings.getLegalBalls() / 6;
        // If exact over completed, show previous over unless 0 balls bowled
        if (activeInnings.getLegalBalls() > 0 && activeInnings.getLegalBalls() % 6 == 0) {
            currentOverNumber = currentOverNumber - 1;
        }

        List<BallEvent> overBalls = ballEventRepository.findBallsInOver(activeInnings.getId(), currentOverNumber);
        List<MatchDetailDTO.BallPillDTO> pills = new ArrayList<>();
        for (BallEvent be : overBalls) {
            String text;
            String type;
            if (Boolean.TRUE.equals(be.getIsWicket())) {
                text = "W";
                type = "WICKET";
            } else if (Boolean.TRUE.equals(be.getIsExtra())) {
                text = be.getExtraType() != null && be.getExtraType().startsWith("W") ? (be.getTotalBallRuns() + "wd") : (be.getTotalBallRuns() + "nb");
                type = "EXTRA";
            } else if (be.getRunsScored() == 4) {
                text = "4";
                type = "FOUR";
            } else if (be.getRunsScored() == 6) {
                text = "6";
                type = "SIX";
            } else if (be.getRunsScored() == 0) {
                text = "•";
                type = "DOT";
            } else {
                text = String.valueOf(be.getRunsScored());
                type = "SINGLE";
            }
            pills.add(new MatchDetailDTO.BallPillDTO(text, type));
        }
        dto.setCurrentOverBalls(pills);

        return dto;
    }

    public ScorecardResponseDTO getFullScorecard(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + matchId));

        ScorecardResponseDTO dto = new ScorecardResponseDTO();
        dto.setMatchId(match.getId());
        dto.setMatchTitle(match.getMatchTitle());
        dto.setStatus(match.getStatus().name());
        dto.setResultSummary(match.getResultSummary());

        List<MatchInnings> inningsList = inningsRepository.findByMatchOrderByInningsNumberAsc(match);
        for (MatchInnings inn : inningsList) {
            ScorecardResponseDTO.InningsScorecardDTO innDTO = new ScorecardResponseDTO.InningsScorecardDTO();
            innDTO.setInningsNumber(inn.getInningsNumber());
            innDTO.setBattingTeam(inn.getBattingTeam());
            innDTO.setBowlingTeam(inn.getBowlingTeam());
            innDTO.setTotalRuns(inn.getTotalRuns());
            innDTO.setTotalWickets(inn.getTotalWickets());
            innDTO.setOversBowled(formatOvers(inn.getLegalBalls()));
            innDTO.setRunRate(inn.getCurrentRunRate());

            innDTO.setExtras(new ScorecardResponseDTO.ExtrasDTO(
                    inn.getExtras(),
                    inn.getWides(),
                    inn.getNoBalls(),
                    inn.getByes(),
                    inn.getLegByes()
            ));

            // Batting entries
            List<BattingScorecard> battingList = battingRepository.findByInningsOrderByBattingOrderAsc(inn);
            List<ScorecardResponseDTO.BattingEntryDTO> battingDTOs = battingList.stream().map(b -> {
                ScorecardResponseDTO.BattingEntryDTO be = new ScorecardResponseDTO.BattingEntryDTO();
                be.setPlayerId(b.getPlayer().getId());
                be.setPlayerName(b.getPlayer().getName());
                be.setBattingOrder(b.getBattingOrder());
                be.setRuns(b.getRuns());
                be.setBalls(b.getBalls());
                be.setFours(b.getFours());
                be.setSixes(b.getSixes());
                be.setStrikeRate(b.getStrikeRate());
                be.setIsOut(b.getIsOut());
                be.setDismissal(b.getDismissal());
                be.setIsOnCrease(b.getIsOnCrease());
                be.setIsStriker(b.getIsStriker());
                return be;
            }).collect(Collectors.toList());
            innDTO.setBatting(battingDTOs);

            // Bowling entries
            List<BowlingScorecard> bowlingList = bowlingRepository.findByInningsOrderByBowlingOrderAsc(inn);
            List<ScorecardResponseDTO.BowlingEntryDTO> bowlingDTOs = bowlingList.stream().map(bow -> {
                ScorecardResponseDTO.BowlingEntryDTO bo = new ScorecardResponseDTO.BowlingEntryDTO();
                bo.setPlayerId(bow.getPlayer().getId());
                bo.setPlayerName(bow.getPlayer().getName());
                bo.setOvers(formatOvers(bow.getLegalBalls()));
                bo.setMaidens(bow.getMaidens());
                bo.setRunsConceded(bow.getRunsConceded());
                bo.setWickets(bow.getWickets());
                bo.setEconomy(bow.getEconomy());
                bo.setDots(bow.getDots());
                bo.setFoursConceded(bow.getFoursConceded());
                bo.setSixesConceded(bow.getSixesConceded());
                bo.setWides(bow.getWides());
                bo.setNoBalls(bow.getNoBalls());
                bo.setIsCurrentBowler(bow.getIsCurrentBowler());
                return bo;
            }).collect(Collectors.toList());
            innDTO.setBowling(bowlingDTOs);

            // Fall of Wickets
            List<FallOfWicket> fowList = fowRepository.findByInningsOrderByWicketNumberAsc(inn);
            List<ScorecardResponseDTO.FOWEntryDTO> fowDTOs = fowList.stream().map(f ->
                    new ScorecardResponseDTO.FOWEntryDTO(f.getWicketNumber(), f.getPlayer().getName(), f.getScoreAtDismissal(), f.getOverAtDismissal())
            ).collect(Collectors.toList());
            innDTO.setFallOfWickets(fowDTOs);

            dto.getInnings().add(innDTO);
        }

        return dto;
    }

    public List<CommentaryDTO> getCommentary(Long matchId, String filter, int limit) {
        int max = limit > 0 ? limit : 30;
        PageRequest pageRequest = PageRequest.of(0, max);
        List<BallEvent> events;

        if ("BOUNDARIES".equalsIgnoreCase(filter)) {
            // Get active or latest innings
            Match match = matchRepository.findById(matchId)
                    .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));
            List<MatchInnings> innList = inningsRepository.findByMatchOrderByInningsNumberAsc(match);
            if (innList.isEmpty()) return new ArrayList<>();
            MatchInnings active = (match.getCurrentInnings() == 2 && innList.size() > 1) ? innList.get(1) : innList.get(0);
            events = ballEventRepository.findBoundariesByInningsId(active.getId());
        } else if ("WICKETS".equalsIgnoreCase(filter)) {
            Match match = matchRepository.findById(matchId)
                    .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));
            List<MatchInnings> innList = inningsRepository.findByMatchOrderByInningsNumberAsc(match);
            if (innList.isEmpty()) return new ArrayList<>();
            MatchInnings active = (match.getCurrentInnings() == 2 && innList.size() > 1) ? innList.get(1) : innList.get(0);
            events = ballEventRepository.findByInningsIdAndIsWicketTrueOrderByTimestampDesc(active.getId());
        } else {
            events = ballEventRepository.findByMatchIdOrderByTimestampDesc(matchId, pageRequest);
        }

        return events.stream().map(this::mapToCommentaryDTO).collect(Collectors.toList());
    }

    private CommentaryDTO mapToCommentaryDTO(BallEvent be) {
        CommentaryDTO dto = new CommentaryDTO();
        dto.setId(be.getId());
        dto.setBallDisplay(be.getBallDisplay());
        dto.setOverNumber(be.getOverNumber());
        dto.setBallNumberInOver(be.getBallNumberInOver());
        dto.setBatsmanName(be.getBatsman() != null ? be.getBatsman().getName() : "");
        dto.setBowlerName(be.getBowler() != null ? be.getBowler().getName() : "");
        dto.setRunsScored(be.getRunsScored());
        dto.setIsWicket(be.getIsWicket());
        dto.setWicketType(be.getWicketType());
        dto.setOutPlayerName(be.getOutPlayer() != null ? be.getOutPlayer().getName() : null);
        dto.setIsExtra(be.getIsExtra());
        dto.setExtraType(be.getExtraType());
        dto.setExtraRuns(be.getExtraRuns());
        dto.setCommentary(be.getCommentary());
        dto.setScoreAfterBall(be.getScoreAfterBall());
        dto.setTimestamp(be.getTimestamp());

        if (Boolean.TRUE.equals(be.getIsWicket())) {
            dto.setEventTag("WICKET");
        } else if (Boolean.TRUE.equals(be.getIsExtra())) {
            dto.setEventTag("EXTRA");
        } else if (be.getRunsScored() == 6) {
            dto.setEventTag("SIX");
        } else if (be.getRunsScored() == 4) {
            dto.setEventTag("FOUR");
        } else if (be.getRunsScored() == 0) {
            dto.setEventTag("DOT");
        } else {
            dto.setEventTag("RUN");
        }
        return dto;
    }

    public static String formatOvers(int legalBalls) {
        int overs = legalBalls / 6;
        int balls = legalBalls % 6;
        return overs + "." + balls;
    }

    public static double calculateRunRate(int runs, int legalBalls) {
        if (legalBalls <= 0) return 0.0;
        double rr = ((double) runs / legalBalls) * 6.0;
        return Math.round(rr * 100.0) / 100.0;
    }
}
