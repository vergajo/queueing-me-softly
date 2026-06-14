package com.queueingmesoftly.service;

import com.queueingmesoftly.model.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class QueueService {

    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private final List<Player> waitingQueue = new CopyOnWriteArrayList<>();
    private final List<Court> courts = new CopyOnWriteArrayList<>();
    private int gamesCompleted = 0;

    public QueueService() {
        // Initialize with 4 courts by default
        for (int i = 1; i <= 4; i++) {
            courts.add(new Court(i));
        }
    }

    public Player addPlayer(String name, SkillLevel skillLevel) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        Player player = new Player(id, name, skillLevel);
        players.put(id, player);
        waitingQueue.add(player);
        return player;
    }

    public void removePlayer(String playerId) {
        Player player = players.remove(playerId);
        if (player != null) {
            waitingQueue.remove(player);
        }
    }

    public void joinQueue(String playerId) {
        Player player = players.get(playerId);
        if (player != null && player.getStatus() != PlayerStatus.WAITING) {
            player.setStatus(PlayerStatus.WAITING);
            player.setJoinedQueueAt(Instant.now());
            waitingQueue.add(player);
        }
    }

    public void leaveQueue(String playerId) {
        Player player = players.get(playerId);
        if (player != null) {
            player.setStatus(PlayerStatus.RESTING);
            waitingQueue.remove(player);
        }
    }

    public List<Player> getWaitingQueue() {
        return Collections.unmodifiableList(waitingQueue);
    }

    public List<Court> getCourts() {
        return Collections.unmodifiableList(courts);
    }

    public List<Player> getAllPlayers() {
        return new ArrayList<>(players.values());
    }

    public Court getCourt(int courtNumber) {
        return courts.stream()
                .filter(c -> c.getCourtNumber() == courtNumber)
                .findFirst()
                .orElse(null);
    }

    public void startMatch(int courtNumber) {
        Court court = getCourt(courtNumber);
        if (court != null && !court.getTeamA().isEmpty() && !court.getTeamB().isEmpty()) {
            court.setStatus(CourtStatus.IN_PLAY);
            court.setMatchStartedAt(Instant.now());
            court.getTeamA().forEach(p -> p.setStatus(PlayerStatus.PLAYING));
            court.getTeamB().forEach(p -> p.setStatus(PlayerStatus.PLAYING));
        }
    }

    public void endMatch(int courtNumber) {
        Court court = getCourt(courtNumber);
        if (court != null && court.getStatus() == CourtStatus.IN_PLAY) {
            court.getTeamA().forEach(p -> {
                p.setStatus(PlayerStatus.RESTING);
                p.setGamesPlayed(p.getGamesPlayed() + 1);
            });
            court.getTeamB().forEach(p -> {
                p.setStatus(PlayerStatus.RESTING);
                p.setGamesPlayed(p.getGamesPlayed() + 1);
            });
            court.setTeamA(new ArrayList<>());
            court.setTeamB(new ArrayList<>());
            court.setStatus(CourtStatus.AVAILABLE);
            court.setMatchStartedAt(null);
            gamesCompleted++;
        }
    }

    public void generateMatch(int courtNumber) {
        Court court = getCourt(courtNumber);
        if (court == null || court.getStatus() == CourtStatus.IN_PLAY) return;
        if (waitingQueue.size() < 4) return;

        // Take the 4 longest-waiting players
        List<Player> selected = waitingQueue.stream()
                .limit(4)
                .collect(Collectors.toList());

        court.setTeamA(new ArrayList<>(List.of(selected.get(0), selected.get(1))));
        court.setTeamB(new ArrayList<>(List.of(selected.get(2), selected.get(3))));
        selected.forEach(waitingQueue::remove);
    }

    public void autoAssign() {
        for (Court court : courts) {
            if (court.getStatus() == CourtStatus.AVAILABLE && court.getTeamA().isEmpty()) {
                if (waitingQueue.size() >= 4) {
                    generateMatch(court.getCourtNumber());
                }
            }
        }
    }

    public int getGamesCompleted() { return gamesCompleted; }

    public int getActiveCourtCount() {
        return (int) courts.stream().filter(c -> c.getStatus() == CourtStatus.IN_PLAY).count();
    }

    public String getAverageWaitTime() {
        if (waitingQueue.isEmpty()) return "0m";
        long avgSeconds = waitingQueue.stream()
                .mapToLong(p -> Duration.between(p.getJoinedQueueAt(), Instant.now()).getSeconds())
                .sum() / waitingQueue.size();
        return avgSeconds / 60 + "m " + avgSeconds % 60 + "s";
    }

    public List<Player> getLeaderboard() {
        return players.values().stream()
                .sorted(Comparator.comparingInt(Player::getGamesPlayed).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    public String getWaitTime(Player player) {
        long seconds = Duration.between(player.getJoinedQueueAt(), Instant.now()).getSeconds();
        if (seconds < 60) return seconds + "s";
        return seconds / 60 + "m " + seconds % 60 + "s";
    }
}

