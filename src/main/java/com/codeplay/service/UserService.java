package com.codeplay.service;

import com.codeplay.dto.*;
import com.codeplay.model.User;
import com.codeplay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createOrUpdateUser(CreateUserRequest req) {
        return userRepository.findByClerkId(req.getClerkId())
            .map(existing -> {
                existing.setName(req.getName());
                existing.setEmail(req.getEmail());
                if (req.getAvatarUrl() != null) existing.setAvatarUrl(req.getAvatarUrl());
                existing.setUpdatedAt(LocalDateTime.now());
                return userRepository.save(existing);
            })
            .orElseGet(() -> {
                User user = User.builder()
                    .clerkId(req.getClerkId())
                    .name(req.getName())
                    .email(req.getEmail())
                    .avatarUrl(req.getAvatarUrl())
                    .role("USER")
                    .totalPoints(0)
                    .streak(0)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
                return userRepository.save(user);
            });
    }

    public User getUserByClerkId(String clerkId) {
        return userRepository.findByClerkId(clerkId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserDTO getUserProfile(String clerkId) {
        User user = getUserByClerkId(clerkId);
        return toDTO(user);
    }

    public void addPoints(String clerkId, int points) {
        User user = getUserByClerkId(clerkId);
        user.setTotalPoints(user.getTotalPoints() + points);
        user.setUpdatedAt(LocalDateTime.now());
        updateStreak(user);
        userRepository.save(user);
        recalculateRanks();
    }

    private void updateStreak(User user) {
        LocalDate today = LocalDate.now();
        if (user.getLastActiveDate() != null) {
            LocalDate lastActive = user.getLastActiveDate().toLocalDate();
            if (lastActive.equals(today.minusDays(1))) {
                user.setStreak(user.getStreak() + 1);
            } else if (!lastActive.equals(today)) {
                user.setStreak(1);
            }
        } else {
            user.setStreak(1);
        }
        user.setLastActiveDate(LocalDateTime.now());
    }

    public void addQuizAttempt(String clerkId, User.QuizAttempt attempt) {
        User user = getUserByClerkId(clerkId);
        user.getQuizHistory().add(attempt);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void unlockPuzzle(String clerkId, String puzzleId) {
        User user = getUserByClerkId(clerkId);
        if (!user.getUnlockedPuzzles().contains(puzzleId)) {
            user.getUnlockedPuzzles().add(puzzleId);
            userRepository.save(user);
        }
    }

    public List<LeaderboardEntry> getLeaderboard(int page, int size) {
        Page<User> users = userRepository.findAllByOrderByTotalPointsDesc(PageRequest.of(page, size));
        List<User> userList = users.getContent();
        int offset = page * size;
        return IntStream.range(0, userList.size())
            .mapToObj(i -> {
                User u = userList.get(i);
                return LeaderboardEntry.builder()
                    .rank(offset + i + 1)
                    .userId(u.getId())
                    .name(u.getName())
                    .avatarUrl(u.getAvatarUrl())
                    .totalPoints(u.getTotalPoints())
                    .completedLevels(u.getCompletedLevels().size())
                    .streak(u.getStreak())
                    .build();
            })
            .collect(Collectors.toList());
    }

    private void recalculateRanks() {
        List<User> users = userRepository.findAllByOrderByTotalPointsDesc();
        for (int i = 0; i < users.size(); i++) {
            users.get(i).setRank(i + 1);
        }
        userRepository.saveAll(users);
    }

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
            .id(user.getId())
            .clerkId(user.getClerkId())
            .name(user.getName())
            .email(user.getEmail())
            .avatarUrl(user.getAvatarUrl())
            .role(user.getRole())
            .totalPoints(user.getTotalPoints())
            .rank(user.getRank())
            .streak(user.getStreak())
            .completedLevels(user.getCompletedLevels())
            .unlockedPuzzles(user.getUnlockedPuzzles())
            .quizAttempts(user.getQuizHistory().size())
            .build();
    }
}
