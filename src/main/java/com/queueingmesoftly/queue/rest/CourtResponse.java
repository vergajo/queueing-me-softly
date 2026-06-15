package com.queueingmesoftly.queue.rest;

import com.queueingmesoftly.queue.domain.CourtStatus;

import java.time.Instant;
import java.util.List;

record CourtResponse(
        Long id,
        int courtNumber,
        CourtStatus status,
        Instant matchStartedAt,
        List<PlayerResponse> teamA,
        List<PlayerResponse> teamB
) {}

