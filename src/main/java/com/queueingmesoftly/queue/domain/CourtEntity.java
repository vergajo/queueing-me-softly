package com.queueingmesoftly.queue.domain;

import com.queueingmesoftly.shared.BaseEntity;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "courts")
public class CourtEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "court_number", nullable = false, unique = true)
    private int courtNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CourtStatus status;

    @Column(name = "match_started_at")
    private Instant matchStartedAt;

    protected CourtEntity() {}

    public CourtEntity(int courtNumber) {
        this.courtNumber = courtNumber;
        this.status = CourtStatus.AVAILABLE;
    }

    public Long getId() { return id; }
    public int getCourtNumber() { return courtNumber; }
    public CourtStatus getStatus() { return status; }
    public Instant getMatchStartedAt() { return matchStartedAt; }

    public void startMatch() {
        this.status = CourtStatus.IN_PLAY;
        this.matchStartedAt = Instant.now();
    }

    public void endMatch() {
        this.status = CourtStatus.AVAILABLE;
        this.matchStartedAt = null;
    }

    public boolean isAvailable() {
        return this.status == CourtStatus.AVAILABLE;
    }
}

