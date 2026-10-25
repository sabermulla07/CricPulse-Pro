package com.cricpulse.cricket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "bowling_scorecards")
public class BowlingScorecard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "innings_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private MatchInnings innings;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Player player;

    private Integer bowlingOrder;

    private Double overs = 0.0;

    private Integer legalBalls = 0;

    private Integer maidens = 0;

    private Integer runsConceded = 0;

    private Integer wickets = 0;

    private Double economy = 0.0;

    private Integer dots = 0;

    private Integer foursConceded = 0;

    private Integer sixesConceded = 0;

    private Integer wides = 0;

    private Integer noBalls = 0;

    private Boolean isCurrentBowler = false;

    public BowlingScorecard() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MatchInnings getInnings() {
        return innings;
    }

    public void setInnings(MatchInnings innings) {
        this.innings = innings;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Integer getBowlingOrder() {
        return bowlingOrder;
    }

    public void setBowlingOrder(Integer bowlingOrder) {
        this.bowlingOrder = bowlingOrder;
    }

    public Double getOvers() {
        return overs;
    }

    public void setOvers(Double overs) {
        this.overs = overs;
    }

    public Integer getLegalBalls() {
        return legalBalls;
    }

    public void setLegalBalls(Integer legalBalls) {
        this.legalBalls = legalBalls;
    }

    public Integer getMaidens() {
        return maidens;
    }

    public void setMaidens(Integer maidens) {
        this.maidens = maidens;
    }

    public Integer getRunsConceded() {
        return runsConceded;
    }

    public void setRunsConceded(Integer runsConceded) {
        this.runsConceded = runsConceded;
    }

    public Integer getWickets() {
        return wickets;
    }

    public void setWickets(Integer wickets) {
        this.wickets = wickets;
    }

    public Double getEconomy() {
        return economy;
    }

    public void setEconomy(Double economy) {
        this.economy = economy;
    }

    public Integer getDots() {
        return dots;
    }

    public void setDots(Integer dots) {
        this.dots = dots;
    }

    public Integer getFoursConceded() {
        return foursConceded;
    }

    public void setFoursConceded(Integer foursConceded) {
        this.foursConceded = foursConceded;
    }

    public Integer getSixesConceded() {
        return sixesConceded;
    }

    public void setSixesConceded(Integer sixesConceded) {
        this.sixesConceded = sixesConceded;
    }

    public Integer getWides() {
        return wides;
    }

    public void setWides(Integer wides) {
        this.wides = wides;
    }

    public Integer getNoBalls() {
        return noBalls;
    }

    public void setNoBalls(Integer noBalls) {
        this.noBalls = noBalls;
    }

    public Boolean getIsCurrentBowler() {
        return isCurrentBowler;
    }

    public void setIsCurrentBowler(Boolean currentBowler) {
        isCurrentBowler = currentBowler;
    }
}
