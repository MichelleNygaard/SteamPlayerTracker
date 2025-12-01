package com.steamtracker.dto;

import java.time.LocalDateTime;

public class PlayerCountData {
    private LocalDateTime timestamp;
    private Integer playerCount;

    // Constructors
    public PlayerCountData() {}

    public PlayerCountData(LocalDateTime timestamp, Integer playerCount) {
        this.timestamp = timestamp;
        this.playerCount = playerCount;
    }

    // Getters and Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(Integer playerCount) {
        this.playerCount = playerCount;
    }
}