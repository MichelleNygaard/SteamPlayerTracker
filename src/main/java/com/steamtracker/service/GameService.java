package com.steamtracker.service;

import com.steamtracker.dto.GameSearchResponse;
import com.steamtracker.dto.PlayerCountData;
import com.steamtracker.entity.Game;
import com.steamtracker.entity.PlayerCount;
import com.steamtracker.repository.GameRepository;
import com.steamtracker.repository.PlayerCountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerCountRepository playerCountRepository;

    @Autowired
    private SteamApiService steamApiService;

    public List<GameSearchResponse> searchGames(String searchTerm) {
        logger.debug("Searching for games with term: {}", searchTerm);

        // First, search in local database
        List<Game> localGames = gameRepository.findByNameContainingIgnoreCase(searchTerm);
        List<GameSearchResponse> results = new ArrayList<>();

        // Add local games to results
        for (Game game : localGames) {
            Integer currentPlayerCount = steamApiService.getCurrentPlayerCount(game.getAppId());
            results.add(new GameSearchResponse(
                    game.getAppId(),
                    game.getName(),
                    currentPlayerCount,
                    game.getLastUpdated(),
                    true
            ));
        }

        // If we have fewer than 10 results, search Steam API
        if (results.size() < 10) {
            List<SteamApiService.GameInfo> steamGames = steamApiService.searchGames(searchTerm);

            for (SteamApiService.GameInfo steamGame : steamGames) {
                // Skip if we already have this game in results
                boolean alreadyExists = results.stream()
                        .anyMatch(r -> r.getAppId().equals(steamGame.getAppId()));

                if (!alreadyExists) {
                    Integer currentPlayerCount = steamApiService.getCurrentPlayerCount(steamGame.getAppId());
                    results.add(new GameSearchResponse(
                            steamGame.getAppId(),
                            steamGame.getName(),
                            currentPlayerCount,
                            null,
                            false
                    ));

                    if (results.size() >= 10) {
                        break;
                    }
                }
            }
        }

        logger.debug("Found {} games for search term: {}", results.size(), searchTerm);
        return results;
    }

    @Transactional
    public void startTrackingGame(Long appId, String gameName) {
        logger.info("Starting to track game: {} (ID: {})", gameName, appId);

        if (!gameRepository.existsByAppId(appId)) {
            Game game = new Game(appId, gameName);
            gameRepository.save(game);
            logger.info("Game added to tracking: {}", gameName);
        } else {
            logger.debug("Game already being tracked: {}", gameName);
        }

        // Collect initial player count
        collectPlayerCount(appId);
    }

    @Transactional
    public void collectPlayerCount(Long appId) {
        logger.debug("Collecting player count for app ID: {}", appId);

        Game game = gameRepository.findByAppId(appId).orElse(null);
        if (game == null) {
            logger.warn("Game not found for app ID: {}", appId);
            return;
        }

        Integer playerCount = steamApiService.getCurrentPlayerCount(appId);
        if (playerCount != null) {
            PlayerCount pc = new PlayerCount(game, playerCount);
            playerCountRepository.save(pc);

            // Update game's last updated timestamp
            game.setLastUpdated(LocalDateTime.now());
            gameRepository.save(game);

            logger.debug("Player count recorded for {}: {}", game.getName(), playerCount);
        } else {
            logger.warn("Failed to get player count for {}", game.getName());
        }
    }

    public List<PlayerCountData> getPlayerCountHistory(Long appId, Integer days) {
        logger.debug("Getting player count history for app ID: {} (last {} days)", appId, days);

        LocalDateTime since = LocalDateTime.now().minusDays(days != null ? days : 7);
        List<PlayerCount> playerCounts = playerCountRepository
                .findByGameAppIdAndRecordedAtAfterOrderByRecordedAtAsc(appId, since);

        return playerCounts.stream()
                .map(pc -> new PlayerCountData(pc.getRecordedAt(), pc.getPlayerCount()))
                .collect(Collectors.toList());
    }

    public List<Game> getAllTrackedGames() {
        return gameRepository.findAll();
    }

    @Transactional
    public void collectPlayerCountsForAllGames() {
        logger.info("Collecting player counts for all tracked games");

        List<Game> games = gameRepository.findAll();
        for (Game game : games) {
            collectPlayerCount(game.getAppId());
        }

        logger.info("Finished collecting player counts for {} games", games.size());
    }

    @Transactional
    public void stopTrackingGame(Long appId) {
        logger.info("Stopping tracking for game with app ID: {}", appId);

        Game game = gameRepository.findByAppId(appId).orElse(null);
        if (game == null) {
            logger.warn("Game not found for app ID: {}", appId);
            throw new RuntimeException("Game not found");
        }

        // Delete all player count data for this game
        playerCountRepository.deleteAll(game.getPlayerCounts());

        // Delete the game
        gameRepository.delete(game);

        logger.info("Successfully stopped tracking game: {}", game.getName());
    }

    public boolean isGameTracked(Long appId) {
        return gameRepository.existsByAppId(appId);
    }
}