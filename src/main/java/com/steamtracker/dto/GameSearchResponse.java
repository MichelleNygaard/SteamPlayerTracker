package com.steamtracker.dto;

import java.time.LocalDateTime;

public class GameSearchResponse {
    private Long appId;
    private String name;
    private Integer currentPlayerCount;
    private LocalDateTime lastUpdated;
    private boolean isTracked;

    // Constructors
    public GameSearchResponse() {}

    public GameSearchResponse(Long appId, String name, Integer currentPlayerCount,
                              LocalDateTime lastUpdated, boolean isTracked) {
        this.appId = appId;
        this.name = name;
        this.currentPlayerCount = currentPlayerCount;
        this.lastUpdated = lastUpdated;
        this.isTracked = isTracked;
    }

    // Getters and Setters
    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCurrentPlayerCount() {
        return currentPlayerCount;
    }

    public void setCurrentPlayerCount(Integer currentPlayerCount) {
        this.currentPlayerCount = currentPlayerCount;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isTracked() {
        return isTracked;
    }

    public void setTracked(boolean tracked) {
        isTracked = tracked;
    }
}