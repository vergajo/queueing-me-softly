package com.queueingmesoftly.queue.rest;

import com.queueingmesoftly.queue.domain.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
class QueueController {

    private final QueueService queueService;

    QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @GetMapping("/dashboard")
    DashboardResponse getDashboard() {
        return buildDashboard();
    }

    @PostMapping("/players")
    @ResponseStatus(HttpStatus.CREATED)
    PlayerResponse addPlayer(@Valid @RequestBody AddPlayerRequest request) {
        PlayerEntity player = queueService.addPlayer(request.name(), request.skillLevel());
        return toPlayerResponse(player);
    }

    @DeleteMapping("/players/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removePlayer(@PathVariable String id) {
        queueService.removePlayer(id);
    }

    @PostMapping("/players/{id}/join-queue")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void joinQueue(@PathVariable String id) {
        queueService.joinQueue(id);
    }

    @PostMapping("/players/{id}/leave-queue")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void leaveQueue(@PathVariable String id) {
        queueService.leaveQueue(id);
    }

    @PostMapping("/courts/{courtNumber}/generate-match")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void generateMatch(@PathVariable int courtNumber) {
        queueService.generateMatch(courtNumber);
    }

    @PostMapping("/courts/{courtNumber}/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void startMatch(@PathVariable int courtNumber) {
        queueService.startMatch(courtNumber);
    }

    @PostMapping("/courts/{courtNumber}/end")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void endMatch(@PathVariable int courtNumber) {
        queueService.endMatch(courtNumber);
    }

    @PostMapping("/courts/auto-assign")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void autoAssign() {
        queueService.autoAssign();
    }

    private DashboardResponse buildDashboard() {
        List<PlayerResponse> waitingQueue = queueService.getWaitingQueue().stream()
                .map(this::toPlayerResponse).toList();
        List<CourtResponse> courts = queueService.getCourts().stream()
                .map(this::toCourtResponse).toList();
        List<PlayerResponse> allPlayers = queueService.getAllPlayers().stream()
                .map(this::toPlayerResponse).toList();
        List<PlayerResponse> restingPlayers = queueService.getRestingPlayers().stream()
                .map(this::toPlayerResponse).toList();
        List<PlayerResponse> leaderboard = queueService.getLeaderboard().stream()
                .map(this::toPlayerResponse).toList();

        return new DashboardResponse(
                waitingQueue,
                courts,
                allPlayers,
                restingPlayers,
                leaderboard,
                queueService.getGamesCompleted(),
                queueService.getActiveCourtCount(),
                queueService.getAverageWaitTime()
        );
    }

    private PlayerResponse toPlayerResponse(PlayerEntity player) {
        return new PlayerResponse(
                player.getId(),
                player.getName(),
                player.getSkillLevel(),
                player.getStatus(),
                player.getGamesPlayed(),
                queueService.getWaitTime(player),
                player.getCourtId(),
                player.getTeam()
        );
    }

    private CourtResponse toCourtResponse(CourtEntity court) {
        List<PlayerResponse> teamA = queueService.getPlayersOnCourt(court.getId(), "A").stream()
                .map(this::toPlayerResponse).toList();
        List<PlayerResponse> teamB = queueService.getPlayersOnCourt(court.getId(), "B").stream()
                .map(this::toPlayerResponse).toList();
        return new CourtResponse(
                court.getId(),
                court.getCourtNumber(),
                court.getStatus(),
                court.getMatchStartedAt(),
                teamA,
                teamB
        );
    }
}

