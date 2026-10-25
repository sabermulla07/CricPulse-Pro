package com.cricpulse.cricket.dto;

public class SimulationControlRequest {
    private String action; // START, PAUSE, STEP, RESET
    private Integer speedMs;

    public SimulationControlRequest() {}

    public SimulationControlRequest(String action, Integer speedMs) {
        this.action = action;
        this.speedMs = speedMs;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getSpeedMs() {
        return speedMs;
    }

    public void setSpeedMs(Integer speedMs) {
        this.speedMs = speedMs;
    }
}
