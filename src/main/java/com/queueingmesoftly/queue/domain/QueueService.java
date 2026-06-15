package com.queueingmesoftly.queue.domain;

import com.queueingmesoftly.shared.DomainException;
import com.queueingmesoftly.shared.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class QueueService {

    private final PlayerRepository playerRepository;
    private final CourtRepository courtRepository;

    public QueueService(PlayerRepository playerRepository, CourtRepository courtRepository) {
        this.playerRepository = playerRepository;
        this.courtRepository = courtRepository;
    }

    @Transactional
    public void initializeCourts() {
        if (courtRepository.count() == 0) {
            for (int i = 1; i <= 4; i++) {
                courtRepository.save(new CourtEntity(i));
            }
        }
    }

    @Transactional
    public PlayerEntity addPlayer(String name, SkillLevel skillLevel) {
        PlayerEntity player = new PlayerEntity(name, skillLevel);
        return playerRepository.save(player);
    }

    @Transactional
    public void removePlayer(String playerId) {
        playerRepository.deleteById(playerId);
    }

    @Transactional
    public void joinQueue(String playerId) {
        PlayerEntity player = getPlayerOrThrow(playerId);
        player.joinQueue();
        playerRepository.save(player);
    }

    @Transactional
    public void leaveQueue(String playerId) {
        PlayerEntity player = getPlayerOrThrow(playerId);
        player.leaveQueue();
        playerRepository.save(player);
    }

    @Transactional(readOnly = true)
    public List<PlayerEntity> getWaitingQueue() {
        return playerRepository.findByStatusOrderByJoinedQueueAtAsc(PlayerStatus.WAITING)
                .stream()
                .filter(p -> p.getCourtId() == null)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CourtEntity> getCourts() {
        return courtRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PlayerEntity> getAllPlayers() {
        return playerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PlayerEntity> getPlayersOnCourt(Long courtId, String team) {
        return playerRepository.findByCourtIdAndTeam(courtId, team);
    }

    @Transactional
    public void startMatch(int courtNumber) {
        CourtEntity court = getCourtOrThrow(courtNumber);
        List<PlayerEntity> courtPlayers = playerRepository.findByCourtId(court.getId());
        if (courtPlayers.isEmpty()) {
            throw new DomainException("No players assigned to court " + courtNumber);
        }
        court.startMatch();
        courtPlayers.forEach(PlayerEntity::startPlaying);
        courtRepository.save(court);
        playerRepository.saveAll(courtPlayers);
    }

    @Transactional
    public void endMatch(int courtNumber) {
        CourtEntity court = getCourtOrThrow(courtNumber);
        if (court.getStatus() != CourtStatus.IN_PLAY) {
            throw new DomainException("Court " + courtNumber + " is not in play");
        }
        List<PlayerEntity> courtPlayers = playerRepository.findByCourtId(court.getId());
        courtPlayers.forEach(PlayerEntity::endMatch);
        court.endMatch();
        courtRepository.save(court);
        playerRepository.saveAll(courtPlayers);
    }

    @Transactional
    public void generateMatch(int courtNumber) {
        CourtEntity court = getCourtOrThrow(courtNumber);
        if (court.getStatus() == CourtStatus.IN_PLAY) {
            throw new DomainException("Court " + courtNumber + " is already in play");
        }
        List<PlayerEntity> waiting = getWaitingQueue();
        if (waiting.size() < 4) {
            throw new DomainException("Not enough players in queue (need 4, have " + waiting.size() + ")");
        }

        List<PlayerEntity> selected = waiting.stream().limit(4).toList();
        selected.get(0).assignToCourt(court.getId(), "A");
        selected.get(1).assignToCourt(court.getId(), "A");
        selected.get(2).assignToCourt(court.getId(), "B");
        selected.get(3).assignToCourt(court.getId(), "B");
        playerRepository.saveAll(selected);
    }

    @Transactional
    public void autoAssign() {
        List<CourtEntity> courts = courtRepository.findAll();
        for (CourtEntity court : courts) {
            if (court.isAvailable()) {
                List<PlayerEntity> courtPlayers = playerRepository.findByCourtId(court.getId());
                if (courtPlayers.isEmpty()) {
                    List<PlayerEntity> waiting = getWaitingQueue();
                    if (waiting.size() >= 4) {
                        List<PlayerEntity> selected = waiting.stream().limit(4).toList();
                        selected.get(0).assignToCourt(court.getId(), "A");
                        selected.get(1).assignToCourt(court.getId(), "A");
                        selected.get(2).assignToCourt(court.getId(), "B");
                        selected.get(3).assignToCourt(court.getId(), "B");
                        playerRepository.saveAll(selected);
                    }
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public int getGamesCompleted() {
        return playerRepository.findAll().stream()
                .mapToInt(PlayerEntity::getGamesPlayed)
                .sum() / 4; // Each game involves 4 players
    }

    @Transactional(readOnly = true)
    public int getActiveCourtCount() {
        return (int) courtRepository.findAll().stream()
                .filter(c -> c.getStatus() == CourtStatus.IN_PLAY)
                .count();
    }

    @Transactional(readOnly = true)
    public String getAverageWaitTime() {
        List<PlayerEntity> waiting = getWaitingQueue();
        if (waiting.isEmpty()) return "0m";
        long avgSeconds = waiting.stream()
                .mapToLong(p -> Duration.between(p.getJoinedQueueAt(), Instant.now()).getSeconds())
                .sum() / waiting.size();
        return avgSeconds / 60 + "m " + avgSeconds % 60 + "s";
    }

    @Transactional(readOnly = true)
    public List<PlayerEntity> getLeaderboard() {
        return playerRepository.findTopByGamesPlayed();
    }

    @Transactional(readOnly = true)
    public List<PlayerEntity> getRestingPlayers() {
        return playerRepository.findByStatus(PlayerStatus.RESTING);
    }

    public String getWaitTime(PlayerEntity player) {
        if (player.getJoinedQueueAt() == null) return "0s";
        long seconds = Duration.between(player.getJoinedQueueAt(), Instant.now()).getSeconds();
        if (seconds < 60) return seconds + "s";
        return seconds / 60 + "m " + seconds % 60 + "s";
    }

    private PlayerEntity getPlayerOrThrow(String playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + playerId));
    }

    private CourtEntity getCourtOrThrow(int courtNumber) {
        return courtRepository.findByCourtNumber(courtNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Court not found: " + courtNumber));
    }
}

