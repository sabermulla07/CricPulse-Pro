package com.cricpulse.cricket.dto;

import java.time.LocalDateTime;

public class CommentaryDTO {
    private Long id;
    private String ballDisplay;
    private Integer overNumber;
    private Integer ballNumberInOver;
    private String batsmanName;
    private String bowlerName;
    private Integer runsScored;
    private Boolean isWicket;
    private String wicketType;
    private String outPlayerName;
    private Boolean isExtra;
    private String extraType;
    private Integer extraRuns;
    private String commentary;
    private String scoreAfterBall;
    private String eventTag; // "FOUR", "SIX", "WICKET", "EXTRA", "DOT", "RUN"
    private LocalDateTime timestamp;

    public CommentaryDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBallDisplay() { return ballDisplay; }
    public void setBallDisplay(String ballDisplay) { this.ballDisplay = ballDisplay; }
    public Integer getOverNumber() { return overNumber; }
    public void setOverNumber(Integer overNumber) { this.overNumber = overNumber; }
    public Integer getBallNumberInOver() { return ballNumberInOver; }
    public void setBallNumberInOver(Integer ballNumberInOver) { this.ballNumberInOver = ballNumberInOver; }
    public String getBatsmanName() { return batsmanName; }
    public void setBatsmanName(String batsmanName) { this.batsmanName = batsmanName; }
    public String getBowlerName() { return bowlerName; }
    public void setBowlerName(String bowlerName) { this.bowlerName = bowlerName; }
    public Integer getRunsScored() { return runsScored; }
    public void setRunsScored(Integer runsScored) { this.runsScored = runsScored; }
    public Boolean getIsWicket() { return isWicket; }
    public void setIsWicket(Boolean wicket) { isWicket = wicket; }
    public String getWicketType() { return wicketType; }
    public void setWicketType(String wicketType) { this.wicketType = wicketType; }
    public String getOutPlayerName() { return outPlayerName; }
    public void setOutPlayerName(String outPlayerName) { this.outPlayerName = outPlayerName; }
    public Boolean getIsExtra() { return isExtra; }
    public void setIsExtra(Boolean extra) { isExtra = extra; }
    public String getExtraType() { return extraType; }
    public void setExtraType(String extraType) { this.extraType = extraType; }
    public Integer getExtraRuns() { return extraRuns; }
    public void setExtraRuns(Integer extraRuns) { this.extraRuns = extraRuns; }
    public String getCommentary() { return commentary; }
    public void setCommentary(String commentary) { this.commentary = commentary; }
    public String getScoreAfterBall() { return scoreAfterBall; }
    public void setScoreAfterBall(String scoreAfterBall) { this.scoreAfterBall = scoreAfterBall; }
    public String getEventTag() { return eventTag; }
    public void setEventTag(String eventTag) { this.eventTag = eventTag; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
