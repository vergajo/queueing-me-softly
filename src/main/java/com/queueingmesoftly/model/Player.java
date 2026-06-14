package com.queueingmesoftly.model;

import java.time.Instant;

public class Player {

    private String id;
    private String name;
    private int gamesPlayed;
    private Instant joinedQueueAt;
    private SkillLevel skillLevel;
    private PlayerStatus status;

    public Player() {}

    public Player(String id, String name, SkillLevel skillLevel) {
        this.id = id;
        this.name = name;
        this.skillLevel = skillLevel;
        this.gamesPlayed = 0;
        this.joinedQueueAt = Instant.now();
        this.status = PlayerStatus.WAITING;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getGamesPlayed() { return gamesPlayed; }
    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }
    public Instant getJoinedQueueAt() { return joinedQueueAt; }
    public void setJoinedQueueAt(Instant joinedQueueAt) { this.joinedQueueAt = joinedQueueAt; }
    public SkillLevel getSkillLevel() { return skillLevel; }
    public void setSkillLevel(SkillLevel skillLevel) { this.skillLevel = skillLevel; }
    public PlayerStatus getStatus() { return status; }
    public void setStatus(PlayerStatus status) { this.status = status; }
}

