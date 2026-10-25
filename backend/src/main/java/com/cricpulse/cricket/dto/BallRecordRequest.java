package com.cricpulse.cricket.dto;

public class BallRecordRequest {
    private Integer runs = 0;
    private Boolean isWicket = false;
    private String wicketType; // BOWLED, CAUGHT, LBW, RUN_OUT, STUMPED
    private Boolean isExtra = false;
    private String extraType; // WIDE, NO_BALL, BYE, LEG_BYE
    private Integer extraRuns = 0;
    private String customCommentary;

    public BallRecordRequest() {}

    public BallRecordRequest(Integer runs, Boolean isWicket, String wicketType, Boolean isExtra, String extraType, Integer extraRuns, String customCommentary) {
        this.runs = runs;
        this.isWicket = isWicket;
        this.wicketType = wicketType;
        this.isExtra = isExtra;
        this.extraType = extraType;
        this.extraRuns = extraRuns;
        this.customCommentary = customCommentary;
    }

    public Integer getRuns() {
        return runs;
    }

    public void setRuns(Integer runs) {
        this.runs = runs;
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

    public String getCustomCommentary() {
        return customCommentary;
    }

    public void setCustomCommentary(String customCommentary) {
        this.customCommentary = customCommentary;
    }
}
