package com.steamtracker.repository;

import com.steamtracker.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    @Query("SELECT g FROM Game g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Game> findByNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    Optional<Game> findByAppId(Long appId);

    boolean existsByAppId(Long appId);
}