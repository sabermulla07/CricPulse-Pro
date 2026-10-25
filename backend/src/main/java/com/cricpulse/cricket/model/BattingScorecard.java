package com.cricpulse.cricket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "batting_scorecards")
public class BattingScorecard {

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

    private Integer battingOrder;

    private Integer runs = 0;

    private Integer balls = 0;

    private Integer fours = 0;

    private Integer sixes = 0;

    private Double strikeRate = 0.0;

    private Boolean isOut = false;

    private String dismissal = "not out";

    private String dismissalType; // BOWLED, CAUGHT, LBW, RUN_OUT, STUMPED, etc.

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bowler_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Player bowler;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fielder_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Player fielder;

    private Boolean isOnCrease = false;

    private Boolean isStriker = false;

    public BattingScorecard() {}

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

    public Integer getBattingOrder() {
        return battingOrder;
    }

    public void setBattingOrder(Integer battingOrder) {
        this.battingOrder = battingOrder;
    }

    public Integer getRuns() {
        return runs;
    }

    public void setRuns(Integer runs) {
        this.runs = runs;
    }

    public Integer getBalls() {
        return balls;
    }

    public void setBalls(Integer balls) {
        this.balls = balls;
    }

    public Integer getFours() {
        return fours;
    }

    public void setFours(Integer fours) {
        this.fours = fours;
    }

    public Integer getSixes() {
        return sixes;
    }

    public void setSixes(Integer sixes) {
        this.sixes = sixes;
    }

    public Double getStrikeRate() {
        return strikeRate;
    }

    public void setStrikeRate(Double strikeRate) {
        this.strikeRate = strikeRate;
    }

    public Boolean getIsOut() {
        return isOut;
    }

    public void setIsOut(Boolean out) {
        isOut = out;
    }

    public String getDismissal() {
        return dismissal;
    }

    public void setDismissal(String dismissal) {
        this.dismissal = dismissal;
    }

    public String getDismissalType() {
        return dismissalType;
    }

    public void setDismissalType(String dismissalType) {
        this.dismissalType = dismissalType;
    }

    public Player getBowler() {
        return bowler;
    }

    public void setBowler(Player bowler) {
        this.bowler = bowler;
    }

    public Player getFielder() {
        return fielder;
    }

    public void setFielder(Player fielder) {
        this.fielder = fielder;
    }

    public Boolean getIsOnCrease() {
        return isOnCrease;
    }

    public void setIsOnCrease(Boolean onCrease) {
        isOnCrease = onCrease;
    }

    public Boolean getIsStriker() {
        return isStriker;
    }

    public void setIsStriker(Boolean striker) {
        isStriker = striker;
    }
}
