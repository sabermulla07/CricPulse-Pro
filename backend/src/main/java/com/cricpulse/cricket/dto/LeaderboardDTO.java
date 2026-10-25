package com.cricpulse.cricket.dto;

import com.cricpulse.cricket.model.Player;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardDTO {

    private List<Player> topBatsmen = new ArrayList<>();
    private List<Player> topBowlers = new ArrayList<>();
    private List<Player> highestStrikeRates = new ArrayList<>();
    private List<Player> bestEconomyBowlers = new ArrayList<>();

    public LeaderboardDTO() {}

    public List<Player> getTopBatsmen() {
        return topBatsmen;
    }

    public void setTopBatsmen(List<Player> topBatsmen) {
        this.topBatsmen = topBatsmen;
    }

    public List<Player> getTopBowlers() {
        return topBowlers;
    }

    public void setTopBowlers(List<Player> topBowlers) {
        this.topBowlers = topBowlers;
    }

    public List<Player> getHighestStrikeRates() {
        return highestStrikeRates;
    }

    public void setHighestStrikeRates(List<Player> highestStrikeRates) {
        this.highestStrikeRates = highestStrikeRates;
    }

    public List<Player> getBestEconomyBowlers() {
        return bestEconomyBowlers;
    }

    public void setBestEconomyBowlers(List<Player> bestEconomyBowlers) {
        this.bestEconomyBowlers = bestEconomyBowlers;
    }
}
