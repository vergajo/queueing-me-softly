package com.queueingmesoftly.queue.domain;

import com.queueingmesoftly.shared.BaseEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "players")
public class PlayerEntity extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_level", nullable = false)
    private SkillLevel skillLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PlayerStatus status;

    @Column(name = "games_played", nullable = false)
    private int gamesPlayed;

    @Column(name = "joined_queue_at")
    private Instant joinedQueueAt;

    @Column(name = "court_id")
    private Long courtId;

    @Column(name = "team")
    private String team;

    protected PlayerEntity() {}

    public PlayerEntity(String name, SkillLevel skillLevel) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.name = name;
        this.skillLevel = skillLevel;
        this.status = PlayerStatus.WAITING;
        this.gamesPlayed = 0;
        this.joinedQueueAt = Instant.now();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public SkillLevel getSkillLevel() { return skillLevel; }
    public PlayerStatus getStatus() { return status; }
    public int getGamesPlayed() { return gamesPlayed; }
    public Instant getJoinedQueueAt() { return joinedQueueAt; }
    public Long getCourtId() { return courtId; }
    public String getTeam() { return team; }

    public void setStatus(PlayerStatus status) { this.status = status; }
    public void setJoinedQueueAt(Instant joinedQueueAt) { this.joinedQueueAt = joinedQueueAt; }
    public void setCourtId(Long courtId) { this.courtId = courtId; }
    public void setTeam(String team) { this.team = team; }

    public void incrementGamesPlayed() {
        this.gamesPlayed++;
    }

    public void joinQueue() {
        this.status = PlayerStatus.WAITING;
        this.joinedQueueAt = Instant.now();
        this.courtId = null;
        this.team = null;
    }

    public void leaveQueue() {
        this.status = PlayerStatus.RESTING;
        this.courtId = null;
        this.team = null;
    }

    public void assignToCourt(Long courtId, String team) {
        this.courtId = courtId;
        this.team = team;
        this.status = PlayerStatus.WAITING;
    }

    public void startPlaying() {
        this.status = PlayerStatus.PLAYING;
    }

    public void endMatch() {
        this.status = PlayerStatus.RESTING;
        this.courtId = null;
        this.team = null;
        this.gamesPlayed++;
    }
}

