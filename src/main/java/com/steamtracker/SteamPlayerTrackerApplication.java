package com.steamtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SteamPlayerTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SteamPlayerTrackerApplication.class, args);
    }
}