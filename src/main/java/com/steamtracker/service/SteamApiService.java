package com.steamtracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SteamApiService {
    private static final Logger logger = LoggerFactory.getLogger(SteamApiService.class);

    @Value("${steam.api.base-url}")
    private String baseUrl;

    @Value("${steam.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // Pre-populated popular games for faster search
    private static final Map<String, GameInfo> POPULAR_GAMES = new HashMap<>();

    static {
        // Add popular games for instant search results
        POPULAR_GAMES.put("counter-strike 2", new GameInfo(730L, "Counter-Strike 2"));
        POPULAR_GAMES.put("cs2", new GameInfo(730L, "Counter-Strike 2"));
        POPULAR_GAMES.put("counter-strike", new GameInfo(10L, "Counter-Strike"));
        POPULAR_GAMES.put("cs", new GameInfo(10L, "Counter-Strike"));
        POPULAR_GAMES.put("dota 2", new GameInfo(570L, "Dota 2"));
        POPULAR_GAMES.put("dota", new GameInfo(570L, "Dota 2"));
        POPULAR_GAMES.put("team fortress 2", new GameInfo(440L, "Team Fortress 2"));
        POPULAR_GAMES.put("tf2", new GameInfo(440L, "Team Fortress 2"));
        POPULAR_GAMES.put("left 4 dead 2", new GameInfo(550L, "Left 4 Dead 2"));
        POPULAR_GAMES.put("l4d2", new GameInfo(550L, "Left 4 Dead 2"));
        POPULAR_GAMES.put("garry's mod", new GameInfo(4000L, "Garry's Mod"));
        POPULAR_GAMES.put("gmod", new GameInfo(4000L, "Garry's Mod"));
        POPULAR_GAMES.put("rust", new GameInfo(252490L, "Rust"));
        POPULAR_GAMES.put("apex legends", new GameInfo(1172470L, "Apex Legends"));
        POPULAR_GAMES.put("apex", new GameInfo(1172470L, "Apex Legends"));
        POPULAR_GAMES.put("pubg", new GameInfo(578080L, "PLAYERUNKNOWN'S BATTLEGROUNDS"));
        POPULAR_GAMES.put("playerunknown's battlegrounds", new GameInfo(578080L, "PLAYERUNKNOWN'S BATTLEGROUNDS"));
        POPULAR_GAMES.put("grand theft auto v", new GameInfo(271590L, "Grand Theft Auto V"));
        POPULAR_GAMES.put("gta v", new GameInfo(271590L, "Grand Theft Auto V"));
        POPULAR_GAMES.put("gta 5", new GameInfo(271590L, "Grand Theft Auto V"));
        POPULAR_GAMES.put("rocket league", new GameInfo(252950L, "Rocket League"));
        POPULAR_GAMES.put("among us", new GameInfo(945360L, "Among Us"));
        POPULAR_GAMES.put("fall guys", new GameInfo(1097150L, "Fall Guys"));
        POPULAR_GAMES.put("destiny 2", new GameInfo(1085660L, "Destiny 2"));
        POPULAR_GAMES.put("warframe", new GameInfo(230410L, "Warframe"));
        POPULAR_GAMES.put("terraria", new GameInfo(105600L, "Terraria"));
        POPULAR_GAMES.put("stardew valley", new GameInfo(413150L, "Stardew Valley"));
        POPULAR_GAMES.put("civilization vi", new GameInfo(289070L, "Sid Meier's Civilization VI"));
        POPULAR_GAMES.put("civ 6", new GameInfo(289070L, "Sid Meier's Civilization VI"));
        POPULAR_GAMES.put("factorio", new GameInfo(427520L, "Factorio"));
        POPULAR_GAMES.put("minecraft", new GameInfo(1086940L, "Minecraft"));
        POPULAR_GAMES.put("valheim", new GameInfo(892970L, "Valheim"));
        POPULAR_GAMES.put("rimworld", new GameInfo(294100L, "RimWorld"));
        POPULAR_GAMES.put("dead by daylight", new GameInfo(381210L, "Dead by Daylight"));
        POPULAR_GAMES.put("dbd", new GameInfo(381210L, "Dead by Daylight"));
        POPULAR_GAMES.put("cities skylines", new GameInfo(255710L, "Cities: Skylines"));
        POPULAR_GAMES.put("euro truck simulator 2", new GameInfo(227300L, "Euro Truck Simulator 2"));
        POPULAR_GAMES.put("ets2", new GameInfo(227300L, "Euro Truck Simulator 2"));
        POPULAR_GAMES.put("cyberpunk 2077", new GameInfo(1091500L, "Cyberpunk 2077"));
        POPULAR_GAMES.put("witcher 3", new GameInfo(292030L, "The Witcher 3: Wild Hunt"));
        POPULAR_GAMES.put("skyrim", new GameInfo(489830L, "The Elder Scrolls V: Skyrim Special Edition"));
        POPULAR_GAMES.put("fallout 4", new GameInfo(377160L, "Fallout 4"));
    }

    public SteamApiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public Integer getCurrentPlayerCount(Long appId) {
        try {
            String url = baseUrl + "/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid=" + appId;
            logger.debug("Fetching player count for app ID: {}", appId);

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            if (root.has("response") && root.get("response").has("player_count")) {
                Integer playerCount = root.get("response").get("player_count").asInt();
                logger.debug("Player count for app ID {}: {}", appId, playerCount);
                return playerCount;
            }

            logger.warn("No player count data found for app ID: {}", appId);
            return null;
        } catch (Exception e) {
            logger.error("Error fetching player count for app ID {}: {}", appId, e.getMessage());
            return null;
        }
    }

    public List<GameInfo> searchGames(String searchTerm) {
        logger.debug("Searching for games with term: {}", searchTerm);

        List<GameInfo> results = new ArrayList<>();
        String lowerSearchTerm = searchTerm.toLowerCase().trim();

        // First, search in our popular games for instant results
        for (Map.Entry<String, GameInfo> entry : POPULAR_GAMES.entrySet()) {
            if (entry.getKey().contains(lowerSearchTerm) ||
                    entry.getValue().getName().toLowerCase().contains(lowerSearchTerm)) {
                results.add(entry.getValue());
                if (results.size() >= 10) break;
            }
        }

        // If we have some results from popular games, return them immediately
        if (!results.isEmpty()) {
            logger.debug("Found {} popular games matching search term: {}", results.size(), searchTerm);
            return results;
        }

        // If no popular games match, you could add more sophisticated search here
        // For now, we'll just return the empty list to keep it fast
        logger.debug("No popular games found for search term: {}", searchTerm);
        return results;
    }

    // Method to add a custom game by App ID (for manual testing)
    public GameInfo getGameInfo(Long appId, String gameName) {
        return new GameInfo(appId, gameName);
    }

    // Inner class for game information
    public static class GameInfo {
        private Long appId;
        private String name;

        public GameInfo(Long appId, String name) {
            this.appId = appId;
            this.name = name;
        }

        public Long getAppId() {
            return appId;
        }

        public String getName() {
            return name;
        }
    }
}