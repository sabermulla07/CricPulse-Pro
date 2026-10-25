package com.cricpulse.cricket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlayerRole role;

    private String battingStyle;

    private String bowlingStyle;

    private Integer jerseyNumber;

    private Boolean isCaptain = false;

    private Boolean isWicketKeeper = false;

    // Career / Tournament aggregated stats
    private Integer matchesPlayed = 0;
    private Integer runsScored = 0;
    private Integer highestScore = 0;
    private Double battingAverage = 0.0;
    private Double battingStrikeRate = 0.0;
    private Integer centuries = 0;
    private Integer fifties = 0;
    private Integer wicketsTaken = 0;
    private Double bowlingAverage = 0.0;
    private Double bowlingEconomy = 0.0;
    private String bestBowling = "0/0";

    public Player() {}

    public Player(String name, Team team, PlayerRole role, String battingStyle, String bowlingStyle, Integer jerseyNumber) {
        this.name = name;
        this.team = team;
        this.role = role;
        this.battingStyle = battingStyle;
        this.bowlingStyle = bowlingStyle;
        this.jerseyNumber = jerseyNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public PlayerRole getRole() {
        return role;
    }

    public void setRole(PlayerRole role) {
        this.role = role;
    }

    public String getBattingStyle() {
        return battingStyle;
    }

    public void setBattingStyle(String battingStyle) {
        this.battingStyle = battingStyle;
    }

    public String getBowlingStyle() {
        return bowlingStyle;
    }

    public void setBowlingStyle(String bowlingStyle) {
        this.bowlingStyle = bowlingStyle;
    }

    public Integer getJerseyNumber() {
        return jerseyNumber;
    }

    public void setJerseyNumber(Integer jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }

    public Boolean getIsCaptain() {
        return isCaptain;
    }

    public void setIsCaptain(Boolean captain) {
        isCaptain = captain;
    }

    public Boolean getIsWicketKeeper() {
        return isWicketKeeper;
    }

    public void setIsWicketKeeper(Boolean wicketKeeper) {
        isWicketKeeper = wicketKeeper;
    }

    public Integer getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(Integer matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public Integer getRunsScored() {
        return runsScored;
    }

    public void setRunsScored(Integer runsScored) {
        this.runsScored = runsScored;
    }

    public Integer getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(Integer highestScore) {
        this.highestScore = highestScore;
    }

    public Double getBattingAverage() {
        return battingAverage;
    }

    public void setBattingAverage(Double battingAverage) {
        this.battingAverage = battingAverage;
    }

    public Double getBattingStrikeRate() {
        return battingStrikeRate;
    }

    public void setBattingStrikeRate(Double battingStrikeRate) {
        this.battingStrikeRate = battingStrikeRate;
    }

    public Integer getCenturies() {
        return centuries;
    }

    public void setCenturies(Integer centuries) {
        this.centuries = centuries;
    }

    public Integer getFifties() {
        return fifties;
    }

    public void setFifties(Integer fifties) {
        this.fifties = fifties;
    }

    public Integer getWicketsTaken() {
        return wicketsTaken;
    }

    public void setWicketsTaken(Integer wicketsTaken) {
        this.wicketsTaken = wicketsTaken;
    }

    public Double getBowlingAverage() {
        return bowlingAverage;
    }

    public void setBowlingAverage(Double bowlingAverage) {
        this.bowlingAverage = bowlingAverage;
    }

    public Double getBowlingEconomy() {
        return bowlingEconomy;
    }

    public void setBowlingEconomy(Double bowlingEconomy) {
        this.bowlingEconomy = bowlingEconomy;
    }

    public String getBestBowling() {
        return bestBowling;
    }

    public void setBestBowling(String bestBowling) {
        this.bestBowling = bestBowling;
    }
}
