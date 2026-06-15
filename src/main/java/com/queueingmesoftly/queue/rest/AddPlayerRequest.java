package com.queueingmesoftly.queue.rest;

import com.queueingmesoftly.queue.domain.SkillLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

record AddPlayerRequest(
        @NotBlank(message = "Player name is required") String name,
        @NotNull(message = "Skill level is required") SkillLevel skillLevel
) {}

