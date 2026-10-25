package com.cricpulse.cricket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ball_events")
public class BallEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "match_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Match match;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "innings_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private MatchInnings innings;

    private Integer overNumber; // 0 to 19

    private Integer ballNumberInOver; // 1 to 6 (or more if extras)

    private String ballDisplay; // e.g. "14.3"

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batsman_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Player batsman;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "non_striker_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Player nonStriker;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bowler_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Player bowler;

    private Integer runsScored = 0;

    private Boolean isWicket = false;

    private String wicketType; // BOWLED, CAUGHT, LBW, RUN_OUT, STUMPED

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "out_player_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Player outPlayer;

    private Boolean isExtra = false;

    private String extraType; // WIDE, NO_BALL, BYE, LEG_BYE

    private Integer extraRuns = 0;

    private Integer totalBallRuns = 0;

    @Column(length = 1000)
    private String commentary;

    private String scoreAfterBall; // e.g. "142/3"

    private LocalDateTime timestamp = LocalDateTime.now();

    public BallEvent() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public MatchInnings getInnings() {
        return innings;
    }

    public void setInnings(MatchInnings innings) {
        this.innings = innings;
    }

    public Integer getOverNumber() {
        return overNumber;
    }

    public void setOverNumber(Integer overNumber) {
        this.overNumber = overNumber;
    }

    public Integer getBallNumberInOver() {
        return ballNumberInOver;
    }

    public void setBallNumberInOver(Integer ballNumberInOver) {
        this.ballNumberInOver = ballNumberInOver;
    }

    public String getBallDisplay() {
        return ballDisplay;
    }

    public void setBallDisplay(String ballDisplay) {
        this.ballDisplay = ballDisplay;
    }

    public Player getBatsman() {
        return batsman;
    }

    public void setBatsman(Player batsman) {
        this.batsman = batsman;
    }

    public Player getNonStriker() {
        return nonStriker;
    }

    public void setNonStriker(Player nonStriker) {
        this.nonStriker = nonStriker;
    }

    public Player getBowler() {
        return bowler;
    }

    public void setBowler(Player bowler) {
        this.bowler = bowler;
    }

    public Integer getRunsScored() {
        return runsScored;
    }

    public void setRunsScored(Integer runsScored) {
        this.runsScored = runsScored;
    }

    public Boolean getIsWicket() {
        return isWicket;
    }

    public void setIsWicket(Boolean wicket) {
        isWicket = wicket;
    }

    public String getWicketType() {
        return wicketType;
    }

    public void setWicketType(String wicketType) {
        this.wicketType = wicketType;
    }

    public Player getOutPlayer() {
        return outPlayer;
    }

    public void setOutPlayer(Player outPlayer) {
        this.outPlayer = outPlayer;
    }

    public Boolean getIsExtra() {
        return isExtra;
    }

    public void setIsExtra(Boolean extra) {
        isExtra = extra;
    }

    public String getExtraType() {
        return extraType;
    }

    public void setExtraType(String extraType) {
        this.extraType = extraType;
    }

    public Integer getExtraRuns() {
        return extraRuns;
    }

    public void setExtraRuns(Integer extraRuns) {
        this.extraRuns = extraRuns;
    }

    public Integer getTotalBallRuns() {
        return totalBallRuns;
    }

    public void setTotalBallRuns(Integer totalBallRuns) {
        this.totalBallRuns = totalBallRuns;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public String getScoreAfterBall() {
        return scoreAfterBall;
    }

    public void setScoreAfterBall(String scoreAfterBall) {
        this.scoreAfterBall = scoreAfterBall;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
