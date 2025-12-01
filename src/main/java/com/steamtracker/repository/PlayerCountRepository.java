package com.steamtracker.repository;

import com.steamtracker.entity.PlayerCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlayerCountRepository extends JpaRepository<PlayerCount, Long> {

    @Query("SELECT pc FROM PlayerCount pc WHERE pc.game.appId = :appId ORDER BY pc.recordedAt DESC")
    List<PlayerCount> findByGameAppIdOrderByRecordedAtDesc(@Param("appId") Long appId);

    @Query("SELECT pc FROM PlayerCount pc WHERE pc.game.appId = :appId AND pc.recordedAt >= :since ORDER BY pc.recordedAt ASC")
    List<PlayerCount> findByGameAppIdAndRecordedAtAfterOrderByRecordedAtAsc(
            @Param("appId") Long appId,
            @Param("since") LocalDateTime since
    );

    @Query("SELECT COUNT(pc) FROM PlayerCount pc WHERE pc.game.appId = :appId")
    long countByGameAppId(@Param("appId") Long appId);
}