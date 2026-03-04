package com.codeplay.service;

import com.codeplay.dto.LeaderboardDTO;
import com.codeplay.model.LeaderboardEntry;
import com.codeplay.model.User;
import com.codeplay.repository.LeaderboardRepository;
import com.codeplay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;
    private final UserRepository userRepository;

    public LeaderboardDTO.LeaderboardResponse getLeaderboard(int page, int size, String currentUserClerkId) {
        Page<LeaderboardEntry> entries = leaderboardRepository
            .findAllByOrderByTotalPointsDesc(PageRequest.of(page, size));

        AtomicInteger rankCounter = new AtomicInteger((page * size) + 1);

        List<LeaderboardDTO.LeaderboardEntryResponse> responseList = entries.getContent().stream()
            .map(entry -> LeaderboardDTO.LeaderboardEntryResponse.builder()
                .rank(rankCounter.getAndIncrement())
                .userId(entry.getUserId())
                .name(entry.getName())
                .totalPoints(entry.getTotalPoints())
                .streak(entry.getStreak())
                .completedLevelsCount(entry.getCompletedLevelsCount())
                .topCategory(entry.getTopCategory())
                .isCurrentUser(entry.getClerkId() != null && entry.getClerkId().equals(currentUserClerkId))
                .build())
            .collect(Collectors.toList());

        return LeaderboardDTO.LeaderboardResponse.builder()
            .entries(responseList)
            .totalUsers((int) entries.getTotalElements())
            .currentPage(page)
            .totalPages(entries.getTotalPages())
            .build();
    }

    public List<LeaderboardDTO.LeaderboardEntryResponse> getTop10() {
        List<LeaderboardEntry> entries = leaderboardRepository.findTop10ByOrderByTotalPointsDesc();
        AtomicInteger rank = new AtomicInteger(1);

        return entries.stream()
            .map(entry -> LeaderboardDTO.LeaderboardEntryResponse.builder()
                .rank(rank.getAndIncrement())
                .userId(entry.getUserId())
                .name(entry.getName())
                .totalPoints(entry.getTotalPoints())
                .streak(entry.getStreak())
                .completedLevelsCount(entry.getCompletedLevelsCount())
                .build())
            .collect(Collectors.toList());
    }

    public LeaderboardDTO.UserRankResponse getUserRank(String clerkId) {
        LeaderboardEntry entry = leaderboardRepository.findByClerkId(clerkId).orElse(null);
        long totalUsers = leaderboardRepository.count();

        if (entry == null) {
            return LeaderboardDTO.UserRankResponse.builder()
                .globalRank((int) totalUsers)
                .totalPoints(0)
                .totalUsers((int) totalUsers)
                .percentile(0)
                .build();
        }

        // Count users with more points
        long usersAhead = leaderboardRepository.findAllByOrderByTotalPointsDesc(
            PageRequest.of(0, Integer.MAX_VALUE))
            .getContent().stream()
            .filter(e -> e.getTotalPoints() > entry.getTotalPoints())
            .count();

        int globalRank = (int) usersAhead + 1;
        double percentile = totalUsers > 0 ? ((double)(totalUsers - globalRank) / totalUsers) * 100 : 0;

        return LeaderboardDTO.UserRankResponse.builder()
            .globalRank(globalRank)
            .totalPoints(entry.getTotalPoints())
            .totalUsers((int) totalUsers)
            .percentile(Math.round(percentile * 10.0) / 10.0)
            .build();
    }

    public void updateLeaderboard(User user) {
        LeaderboardEntry entry = leaderboardRepository.findByUserId(user.getId())
            .orElse(LeaderboardEntry.builder()
                .userId(user.getId())
                .clerkId(user.getClerkId())
                .build());

        entry.setName(user.getName());
        entry.setTotalPoints(user.getTotalPoints());
        entry.setStreak(user.getStreak());
        entry.setCompletedLevelsCount(user.getCompletedLevels().size());
        entry.setUpdatedAt(LocalDateTime.now());

        // Determine top category
        if (!user.getQuizHistory().isEmpty()) {
            String topCat = user.getQuizHistory().stream()
                .collect(Collectors.groupingBy(User.QuizAttempt::getCategory, Collectors.summingInt(User.QuizAttempt::getScore)))
                .entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse("CORE_CS");
            entry.setTopCategory(topCat);
        }

        leaderboardRepository.save(entry);

        // Update rank for all users
        rebuildRanks();
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void rebuildRanks() {
        List<LeaderboardEntry> all = new ArrayList<>(
            leaderboardRepository.findAllByOrderByTotalPointsDesc(PageRequest.of(0, Integer.MAX_VALUE)).getContent()
        );

        for (int i = 0; i < all.size(); i++) {
            LeaderboardEntry entry = all.get(i);
            entry.setRank(i + 1);

            // Also update user rank
            userRepository.findById(entry.getUserId()).ifPresent(user -> {
                user.setRank(entry.getRank());
                userRepository.save(user);
            });
        }

        leaderboardRepository.saveAll(all);
        log.debug("Rebuilt leaderboard ranks for {} users", all.size());
    }
}