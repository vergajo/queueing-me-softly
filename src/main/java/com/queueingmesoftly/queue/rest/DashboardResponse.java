package com.queueingmesoftly.queue.rest;

import java.util.List;

record DashboardResponse(
        List<PlayerResponse> waitingQueue,
        List<CourtResponse> courts,
        List<PlayerResponse> allPlayers,
        List<PlayerResponse> restingPlayers,
        List<PlayerResponse> leaderboard,
        int gamesCompleted,
        int activeCourtCount,
        String averageWaitTime
) {}

