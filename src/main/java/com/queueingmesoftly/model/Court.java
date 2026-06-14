package com.queueingmesoftly.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Court {

    private int courtNumber;
    private CourtStatus status;
    private List<Player> teamA;
    private List<Player> teamB;
    private Instant matchStartedAt;

    public Court() {
        this.teamA = new ArrayList<>();
        this.teamB = new ArrayList<>();
        this.status = CourtStatus.AVAILABLE;
    }

    public Court(int courtNumber) {
        this();
        this.courtNumber = courtNumber;
    }

    public int getCourtNumber() { return courtNumber; }
    public void setCourtNumber(int courtNumber) { this.courtNumber = courtNumber; }
    public CourtStatus getStatus() { return status; }
    public void setStatus(CourtStatus status) { this.status = status; }
    public List<Player> getTeamA() { return teamA; }
    public void setTeamA(List<Player> teamA) { this.teamA = teamA; }
    public List<Player> getTeamB() { return teamB; }
    public void setTeamB(List<Player> teamB) { this.teamB = teamB; }
    public Instant getMatchStartedAt() { return matchStartedAt; }
    public void setMatchStartedAt(Instant matchStartedAt) { this.matchStartedAt = matchStartedAt; }
}

