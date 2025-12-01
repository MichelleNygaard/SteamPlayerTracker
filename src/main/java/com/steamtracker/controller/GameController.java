package com.steamtracker.controller;

import com.steamtracker.dto.GameSearchResponse;
import com.steamtracker.dto.PlayerCountData;
import com.steamtracker.entity.Game;
import com.steamtracker.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Backend REST endpoint
@RequestMapping("/api/games")
@CrossOrigin(origins = "http://localhost:3000")
public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private GameService gameService;

    @GetMapping("/search")
    public ResponseEntity<List<GameSearchResponse>> searchGames(@RequestParam String query) {
        logger.info("Searching for games with query: {}", query);

        if (query == null || query.trim().length() < 2) {
            return ResponseEntity.badRequest().build();
        }

        List<GameSearchResponse> results = gameService.searchGames(query.trim());
        return ResponseEntity.ok(results);
    }

    @PostMapping("/{appId}/track")
    public ResponseEntity<String> startTrackingGame(
            @PathVariable Long appId,
            @RequestParam String gameName) {
        logger.info("Request to start tracking game: {} (ID: {})", gameName, appId);

        try {
            gameService.startTrackingGame(appId, gameName);
            return ResponseEntity.ok("Game tracking started successfully");
        } catch (Exception e) {
            logger.error("Error starting game tracking", e);
            return ResponseEntity.internalServerError()
                    .body("Error starting game tracking: " + e.getMessage());
        }
    }

    @GetMapping("/{appId}/history")
    public ResponseEntity<List<PlayerCountData>> getPlayerCountHistory(
            @PathVariable Long appId,
            @RequestParam(required = false) Integer days) {
        logger.info("Getting player count history for app ID: {}", appId);

        try {
            List<PlayerCountData> history = gameService.getPlayerCountHistory(appId, days);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            logger.error("Error getting player count history", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/tracked")
    public ResponseEntity<List<Game>> getAllTrackedGames() {
        logger.info("Getting all tracked games");

        try {
            List<Game> games = gameService.getAllTrackedGames();
            logger.info("Found {} tracked games", games.size());
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            logger.error("Error getting tracked games", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/collect")
    public ResponseEntity<String> collectPlayerCounts() {
        logger.info("Manual player count collection triggered");

        try {
            gameService.collectPlayerCountsForAllGames();
            return ResponseEntity.ok("Player counts collected successfully");
        } catch (Exception e) {
            logger.error("Error collecting player counts", e);
            return ResponseEntity.internalServerError()
                    .body("Error collecting player counts: " + e.getMessage());
        }
    }

    @PostMapping("/add-by-id")
    public ResponseEntity<String> addGameById(
            @RequestParam Long appId,
            @RequestParam String gameName) {
        logger.info("Request to add game by ID: {} ({})", appId, gameName);

        try {
            gameService.startTrackingGame(appId, gameName);
            return ResponseEntity.ok("Game added successfully");
        } catch (Exception e) {
            logger.error("Error adding game by ID", e);
            return ResponseEntity.internalServerError()
                    .body("Error adding game: " + e.getMessage());
        }
    }

    @DeleteMapping("/{appId}/untrack")
    public ResponseEntity<String> stopTrackingGame(@PathVariable Long appId) {
        logger.info("Request to stop tracking game with app ID: {}", appId);

        try {
            gameService.stopTrackingGame(appId);
            return ResponseEntity.ok("Game untracked successfully");
        } catch (Exception e) {
            logger.error("Error stopping game tracking", e);
            return ResponseEntity.internalServerError()
                    .body("Error stopping game tracking: " + e.getMessage());
        }
    }

    @GetMapping("/{appId}/is-tracked")
    public ResponseEntity<Boolean> isGameTracked(@PathVariable Long appId) {
        try {
            boolean isTracked = gameService.isGameTracked(appId);
            return ResponseEntity.ok(isTracked);
        } catch (Exception e) {
            logger.error("Error checking if game is tracked", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}