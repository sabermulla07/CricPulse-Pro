package com.cricpulse.cricket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "fall_of_wickets")
public class FallOfWicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "innings_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private MatchInnings innings;

    private Integer wicketNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Player player;

    private Integer scoreAtDismissal;

    private String overAtDismissal; // e.g. "7.4"

    public FallOfWicket() {}

    public FallOfWicket(MatchInnings innings, Integer wicketNumber, Player player, Integer scoreAtDismissal, String overAtDismissal) {
        this.innings = innings;
        this.wicketNumber = wicketNumber;
        this.player = player;
        this.scoreAtDismissal = scoreAtDismissal;
        this.overAtDismissal = overAtDismissal;
    }

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

    public Integer getWicketNumber() {
        return wicketNumber;
    }

    public void setWicketNumber(Integer wicketNumber) {
        this.wicketNumber = wicketNumber;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Integer getScoreAtDismissal() {
        return scoreAtDismissal;
    }

    public void setScoreAtDismissal(Integer scoreAtDismissal) {
        this.scoreAtDismissal = scoreAtDismissal;
    }

    public String getOverAtDismissal() {
        return overAtDismissal;
    }

    public void setOverAtDismissal(String overAtDismissal) {
        this.overAtDismissal = overAtDismissal;
    }
}
