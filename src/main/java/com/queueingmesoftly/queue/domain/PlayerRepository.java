package com.queueingmesoftly.queue.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

interface PlayerRepository extends JpaRepository<PlayerEntity, String> {

    List<PlayerEntity> findByStatusOrderByJoinedQueueAtAsc(PlayerStatus status);

    List<PlayerEntity> findByCourtIdAndTeam(Long courtId, String team);

    List<PlayerEntity> findByCourtId(Long courtId);

    @Query("SELECT p FROM PlayerEntity p ORDER BY p.gamesPlayed DESC LIMIT 5")
    List<PlayerEntity> findTopByGamesPlayed();

    List<PlayerEntity> findByStatus(PlayerStatus status);
}

