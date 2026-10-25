package com.cricpulse.cricket.dto;

import com.cricpulse.cricket.model.Team;
import java.util.ArrayList;
import java.util.List;

public class ScorecardResponseDTO {

    private Long matchId;
    private String matchTitle;
    private String status;
    private String resultSummary;
    private List<InningsScorecardDTO> innings = new ArrayList<>();

    public ScorecardResponseDTO() {}

    public static class InningsScorecardDTO {
        private Integer inningsNumber;
        private Team battingTeam;
        private Team bowlingTeam;
        private Integer totalRuns;
        private Integer totalWickets;
        private String oversBowled;
        private Double runRate;
        private ExtrasDTO extras;
        private List<BattingEntryDTO> batting = new ArrayList<>();
        private List<BowlingEntryDTO> bowling = new ArrayList<>();
        private List<FOWEntryDTO> fallOfWickets = new ArrayList<>();

        public InningsScorecardDTO() {}

        public Integer getInningsNumber() { return inningsNumber; }
        public void setInningsNumber(Integer inningsNumber) { this.inningsNumber = inningsNumber; }
        public Team getBattingTeam() { return battingTeam; }
        public void setBattingTeam(Team battingTeam) { this.battingTeam = battingTeam; }
        public Team getBowlingTeam() { return bowlingTeam; }
        public void setBowlingTeam(Team bowlingTeam) { this.bowlingTeam = bowlingTeam; }
        public Integer getTotalRuns() { return totalRuns; }
        public void setTotalRuns(Integer totalRuns) { this.totalRuns = totalRuns; }
        public Integer getTotalWickets() { return totalWickets; }
        public void setTotalWickets(Integer totalWickets) { this.totalWickets = totalWickets; }
        public String getOversBowled() { return oversBowled; }
        public void setOversBowled(String oversBowled) { this.oversBowled = oversBowled; }
        public Double getRunRate() { return runRate; }
        public void setRunRate(Double runRate) { this.runRate = runRate; }
        public ExtrasDTO getExtras() { return extras; }
        public void setExtras(ExtrasDTO extras) { this.extras = extras; }
        public List<BattingEntryDTO> getBatting() { return batting; }
        public void setBatting(List<BattingEntryDTO> batting) { this.batting = batting; }
        public List<BowlingEntryDTO> getBowling() { return bowling; }
        public void setBowling(List<BowlingEntryDTO> bowling) { this.bowling = bowling; }
        public List<FOWEntryDTO> getFallOfWickets() { return fallOfWickets; }
        public void setFallOfWickets(List<FOWEntryDTO> fallOfWickets) { this.fallOfWickets = fallOfWickets; }
    }

    public static class ExtrasDTO {
        private Integer total;
        private Integer wides;
        private Integer noBalls;
        private Integer byes;
        private Integer legByes;

        public ExtrasDTO() {}

        public ExtrasDTO(Integer total, Integer wides, Integer noBalls, Integer byes, Integer legByes) {
            this.total = total;
            this.wides = wides;
            this.noBalls = noBalls;
            this.byes = byes;
            this.legByes = legByes;
        }

        public Integer getTotal() { return total; }
        public void setTotal(Integer total) { this.total = total; }
        public Integer getWides() { return wides; }
        public void setWides(Integer wides) { this.wides = wides; }
        public Integer getNoBalls() { return noBalls; }
        public void setNoBalls(Integer noBalls) { this.noBalls = noBalls; }
        public Integer getByes() { return byes; }
        public void setByes(Integer byes) { this.byes = byes; }
        public Integer getLegByes() { return legByes; }
        public void setLegByes(Integer legByes) { this.legByes = legByes; }
    }

    public static class BattingEntryDTO {
        private Long playerId;
        private String playerName;
        private Integer battingOrder;
        private Integer runs;
        private Integer balls;
        private Integer fours;
        private Integer sixes;
        private Double strikeRate;
        private Boolean isOut;
        private String dismissal;
        private Boolean isOnCrease;
        private Boolean isStriker;

        public BattingEntryDTO() {}

        public Long getPlayerId() { return playerId; }
        public void setPlayerId(Long playerId) { this.playerId = playerId; }
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
        public Integer getBattingOrder() { return battingOrder; }
        public void setBattingOrder(Integer battingOrder) { this.battingOrder = battingOrder; }
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
        public Boolean getIsOut() { return isOut; }
        public void setIsOut(Boolean out) { isOut = out; }
        public String getDismissal() { return dismissal; }
        public void setDismissal(String dismissal) { this.dismissal = dismissal; }
        public Boolean getIsOnCrease() { return isOnCrease; }
        public void setIsOnCrease(Boolean onCrease) { isOnCrease = onCrease; }
        public Boolean getIsStriker() { return isStriker; }
        public void setIsStriker(Boolean striker) { isStriker = striker; }
    }

    public static class BowlingEntryDTO {
        private Long playerId;
        private String playerName;
        private String overs;
        private Integer maidens;
        private Integer runsConceded;
        private Integer wickets;
        private Double economy;
        private Integer dots;
        private Integer foursConceded;
        private Integer sixesConceded;
        private Integer wides;
        private Integer noBalls;
        private Boolean isCurrentBowler;

        public BowlingEntryDTO() {}

        public Long getPlayerId() { return playerId; }
        public void setPlayerId(Long playerId) { this.playerId = playerId; }
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
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
        public Integer getDots() { return dots; }
        public void setDots(Integer dots) { this.dots = dots; }
        public Integer getFoursConceded() { return foursConceded; }
        public void setFoursConceded(Integer foursConceded) { this.foursConceded = foursConceded; }
        public Integer getSixesConceded() { return sixesConceded; }
        public void setSixesConceded(Integer sixesConceded) { this.sixesConceded = sixesConceded; }
        public Integer getWides() { return wides; }
        public void setWides(Integer wides) { this.wides = wides; }
        public Integer getNoBalls() { return noBalls; }
        public void setNoBalls(Integer noBalls) { this.noBalls = noBalls; }
        public Boolean getIsCurrentBowler() { return isCurrentBowler; }
        public void setIsCurrentBowler(Boolean currentBowler) { isCurrentBowler = currentBowler; }
    }

    public static class FOWEntryDTO {
        private Integer wicketNumber;
        private String playerName;
        private Integer scoreAtDismissal;
        private String overAtDismissal;

        public FOWEntryDTO() {}

        public FOWEntryDTO(Integer wicketNumber, String playerName, Integer scoreAtDismissal, String overAtDismissal) {
            this.wicketNumber = wicketNumber;
            this.playerName = playerName;
            this.scoreAtDismissal = scoreAtDismissal;
            this.overAtDismissal = overAtDismissal;
        }

        public Integer getWicketNumber() { return wicketNumber; }
        public void setWicketNumber(Integer wicketNumber) { this.wicketNumber = wicketNumber; }
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
        public Integer getScoreAtDismissal() { return scoreAtDismissal; }
        public void setScoreAtDismissal(Integer scoreAtDismissal) { this.scoreAtDismissal = scoreAtDismissal; }
        public String getOverAtDismissal() { return overAtDismissal; }
        public void setOverAtDismissal(String overAtDismissal) { this.overAtDismissal = overAtDismissal; }
    }

    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }
    public String getMatchTitle() { return matchTitle; }
    public void setMatchTitle(String matchTitle) { this.matchTitle = matchTitle; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getResultSummary() { return resultSummary; }
    public void setResultSummary(String resultSummary) { this.resultSummary = resultSummary; }
    public List<InningsScorecardDTO> getInnings() { return innings; }
    public void setInnings(List<InningsScorecardDTO> innings) { this.innings = innings; }
}
