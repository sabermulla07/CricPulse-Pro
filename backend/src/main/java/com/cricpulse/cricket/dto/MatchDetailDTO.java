package com.cricpulse.cricket.dto;

import com.cricpulse.cricket.model.MatchStatus;
import com.cricpulse.cricket.model.Team;
import java.util.ArrayList;
import java.util.List;

public class MatchDetailDTO {

    private Long id;
    private String matchTitle;
    private String seriesName;
    private String matchType;
    private String venue;
    private MatchStatus status;
    private Team teamA;
    private Team teamB;
    private String tossWinner;
    private String tossDecision;
    private Integer totalOvers;
    private Integer currentInnings;
    private Integer target;
    private String resultSummary;
    private String playerOfTheMatch;
    private Boolean isSimulationActive;
    private Integer simulationSpeedMs;

    // Current Innings details
    private Team battingTeam;
    private Team bowlingTeam;
    private Integer totalRuns = 0;
    private Integer totalWickets = 0;
    private String oversDisplay = "0.0";
    private Integer legalBalls = 0;
    private Integer extras = 0;
    private Double currentRunRate = 0.0;
    private Double requiredRunRate = 0.0;
    private String targetEquation;

    // Inning score ribbons
    private String firstInningsScore;
    private String secondInningsScore;

    // Active Batters
    private BatterLiveDTO striker;
    private BatterLiveDTO nonStriker;

    // Active Bowler
    private BowlerLiveDTO currentBowler;

    // Current Over Ball Pills (e.g. ["0", "1", "4", "W", "6"])
    private List<BallPillDTO> currentOverBalls = new ArrayList<>();

    public MatchDetailDTO() {}

    // Nested classes for live batter and bowler
    public static class BatterLiveDTO {
        private Long playerId;
        private String name;
        private Integer runs = 0;
        private Integer balls = 0;
        private Integer fours = 0;
        private Integer sixes = 0;
        private Double strikeRate = 0.0;
        private Boolean isStriker = false;

        public BatterLiveDTO() {}

        public BatterLiveDTO(Long playerId, String name, Integer runs, Integer balls, Integer fours, Integer sixes, Double strikeRate, Boolean isStriker) {
            this.playerId = playerId;
            this.name = name;
            this.runs = runs;
            this.balls = balls;
            this.fours = fours;
            this.sixes = sixes;
            this.strikeRate = strikeRate;
            this.isStriker = isStriker;
        }

        public Long getPlayerId() { return playerId; }
        public void setPlayerId(Long playerId) { this.playerId = playerId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getRuns() { return runs; }
        public void setRuns(Integer runs) { this.runs = runs; }
        public Integer getBalls() { return balls; }
        public void setBalls(Integer balls) { this.balls = balls; }
        public Integer getFours() { return fours; }
        public void setFours(Integer fours) { this.fours = fours; }
        public Integer getSixes() { return sixes; }
        public void setSixes(Integer sixes) { this.sixes = sixes; }
        public Double getStrikeRate() { return strikeRate; }
        public void setStrikeRate(Double strikeRate) { this.strikeRate = strikeRate; }
        public Boolean getIsStriker() { return isStriker; }
        public void setIsStriker(Boolean striker) { isStriker = striker; }
    }

    public static class BowlerLiveDTO {
        private Long playerId;
        private String name;
        private String overs = "0.0";
        private Integer maidens = 0;
        private Integer runsConceded = 0;
        private Integer wickets = 0;
        private Double economy = 0.0;

        public BowlerLiveDTO() {}

        public BowlerLiveDTO(Long playerId, String name, String overs, Integer maidens, Integer runsConceded, Integer wickets, Double economy) {
            this.playerId = playerId;
            this.name = name;
            this.overs = overs;
            this.maidens = maidens;
            this.runsConceded = runsConceded;
            this.wickets = wickets;
            this.economy = economy;
        }

        public Long getPlayerId() { return playerId; }
        public void setPlayerId(Long playerId) { this.playerId = playerId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getOvers() { return overs; }
        public void setOvers(String overs) { this.overs = overs; }
        public Integer getMaidens() { return maidens; }
        public void setMaidens(Integer maidens) { this.maidens = maidens; }
        public Integer getRunsConceded() { return runsConceded; }
        public void setRunsConceded(Integer runsConceded) { this.runsConceded = runsConceded; }
        public Integer getWickets() { return wickets; }
        public void setWickets(Integer wickets) { this.wickets = wickets; }
        public Double getEconomy() { return economy; }
        public void setEconomy(Double economy) { this.economy = economy; }
    }

    public static class BallPillDTO {
        private String text;
        private String type; // DOT, SINGLE, FOUR, SIX, WICKET, EXTRA

        public BallPillDTO() {}

        public BallPillDTO(String text, String type) {
            this.text = text;
            this.type = type;
        }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMatchTitle() { return matchTitle; }
    public void setMatchTitle(String matchTitle) { this.matchTitle = matchTitle; }
    public String getSeriesName() { return seriesName; }
    public void setSeriesName(String seriesName) { this.seriesName = seriesName; }
    public String getMatchType() { return matchType; }
    public void setMatchType(String matchType) { this.matchType = matchType; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }
    public Team getTeamA() { return teamA; }
    public void setTeamA(Team teamA) { this.teamA = teamA; }
    public Team getTeamB() { return teamB; }
    public void setTeamB(Team teamB) { this.teamB = teamB; }
    public String getTossWinner() { return tossWinner; }
    public void setTossWinner(String tossWinner) { this.tossWinner = tossWinner; }
    public String getTossDecision() { return tossDecision; }
    public void setTossDecision(String tossDecision) { this.tossDecision = tossDecision; }
    public Integer getTotalOvers() { return totalOvers; }
    public void setTotalOvers(Integer totalOvers) { this.totalOvers = totalOvers; }
    public Integer getCurrentInnings() { return currentInnings; }
    public void setCurrentInnings(Integer currentInnings) { this.currentInnings = currentInnings; }
    public Integer getTarget() { return target; }
    public void setTarget(Integer target) { this.target = target; }
    public String getResultSummary() { return resultSummary; }
    public void setResultSummary(String resultSummary) { this.resultSummary = resultSummary; }
    public String getPlayerOfTheMatch() { return playerOfTheMatch; }
    public void setPlayerOfTheMatch(String playerOfTheMatch) { this.playerOfTheMatch = playerOfTheMatch; }
    public Boolean getIsSimulationActive() { return isSimulationActive; }
    public void setIsSimulationActive(Boolean simulationActive) { isSimulationActive = simulationActive; }
    public Integer getSimulationSpeedMs() { return simulationSpeedMs; }
    public void setSimulationSpeedMs(Integer simulationSpeedMs) { this.simulationSpeedMs = simulationSpeedMs; }
    public Team getBattingTeam() { return battingTeam; }
    public void setBattingTeam(Team battingTeam) { this.battingTeam = battingTeam; }
    public Team getBowlingTeam() { return bowlingTeam; }
    public void setBowlingTeam(Team bowlingTeam) { this.bowlingTeam = bowlingTeam; }
    public Integer getTotalRuns() { return totalRuns; }
    public void setTotalRuns(Integer totalRuns) { this.totalRuns = totalRuns; }
    public Integer getTotalWickets() { return totalWickets; }
    public void setTotalWickets(Integer totalWickets) { this.totalWickets = totalWickets; }
    public String getOversDisplay() { return oversDisplay; }
    public void setOversDisplay(String oversDisplay) { this.oversDisplay = oversDisplay; }
    public Integer getLegalBalls() { return legalBalls; }
    public void setLegalBalls(Integer legalBalls) { this.legalBalls = legalBalls; }
    public Integer getExtras() { return extras; }
    public void setExtras(Integer extras) { this.extras = extras; }
    public Double getCurrentRunRate() { return currentRunRate; }
    public void setCurrentRunRate(Double currentRunRate) { this.currentRunRate = currentRunRate; }
    public Double getRequiredRunRate() { return requiredRunRate; }
    public void setRequiredRunRate(Double requiredRunRate) { this.requiredRunRate = requiredRunRate; }
    public String getTargetEquation() { return targetEquation; }
    public void setTargetEquation(String targetEquation) { this.targetEquation = targetEquation; }
    public String getFirstInningsScore() { return firstInningsScore; }
    public void setFirstInningsScore(String firstInningsScore) { this.firstInningsScore = firstInningsScore; }
    public String getSecondInningsScore() { return secondInningsScore; }
    public void setSecondInningsScore(String secondInningsScore) { this.secondInningsScore = secondInningsScore; }
    public BatterLiveDTO getStriker() { return striker; }
    public void setStriker(BatterLiveDTO striker) { this.striker = striker; }
    public BatterLiveDTO getNonStriker() { return nonStriker; }
    public void setNonStriker(BatterLiveDTO nonStriker) { this.nonStriker = nonStriker; }
    public BowlerLiveDTO getCurrentBowler() { return currentBowler; }
    public void setCurrentBowler(BowlerLiveDTO currentBowler) { this.currentBowler = currentBowler; }
    public List<BallPillDTO> getCurrentOverBalls() { return currentOverBalls; }
    public void setCurrentOverBalls(List<BallPillDTO> currentOverBalls) { this.currentOverBalls = currentOverBalls; }
}
