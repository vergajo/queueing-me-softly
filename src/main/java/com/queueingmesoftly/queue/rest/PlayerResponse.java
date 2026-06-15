package com.queueingmesoftly.queue.rest;

import com.queueingmesoftly.queue.domain.PlayerStatus;
import com.queueingmesoftly.queue.domain.SkillLevel;

record PlayerResponse(
        String id,
        String name,
        SkillLevel skillLevel,
        PlayerStatus status,
        int gamesPlayed,
        String waitTime,
        Long courtId,
        String team
) {}

