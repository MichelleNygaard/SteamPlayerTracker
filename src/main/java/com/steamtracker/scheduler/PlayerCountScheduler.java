package com.steamtracker.scheduler;

import com.steamtracker.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PlayerCountScheduler {
    private static final Logger logger = LoggerFactory.getLogger(PlayerCountScheduler.class);

    @Autowired
    private GameService gameService;

    // Run every 30 seconds for demo purposes
    // Change this to 30 * 60 * 1000 (30 minutes) for production
     @Scheduled(fixedRate = 30 * 1000) // For collecting data every 30 seconds.
//    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void collectPlayerCounts() {
        logger.info("Scheduled player count collection started");
        gameService.collectPlayerCountsForAllGames();
        logger.info("Scheduled player count collection completed");
    }
}