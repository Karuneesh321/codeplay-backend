package com.codeplay.service;

import com.codeplay.model.Puzzle;
import com.codeplay.model.User;
import com.codeplay.repository.PuzzleRepository;
import com.codeplay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PuzzleService {

    private final PuzzleRepository puzzleRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public List<Puzzle> getAllPuzzles() {
        return puzzleRepository.findByActiveOrderByLevelNumberAsc(true);
    }

    public List<Puzzle> getUnlockedPuzzles(String clerkId) {
        User user = userService.getUserByClerkId(clerkId);
        List<Puzzle> all = puzzleRepository.findByActiveOrderByLevelNumberAsc(true);
        return all.stream()
            .filter(p -> p.getUnlockRequirement() == 0
                      || user.getUnlockedPuzzles().contains(p.getId())
                      || user.getTotalPoints() >= p.getUnlockRequirement())
            .collect(Collectors.toList());
    }

    public Puzzle getPuzzleById(String id) {
        return puzzleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Puzzle not found"));
    }

    public boolean submitSolution(String clerkId, String puzzleId, String solution) {
        Puzzle puzzle = getPuzzleById(puzzleId);
        boolean correct = puzzle.getSolution().trim().equalsIgnoreCase(solution.trim());

        if (correct) {
            // Step 1: add points and streak (UserService saves the user internally)
            userService.addPoints(clerkId, puzzle.getPoints());

            // Step 2: re-fetch AFTER addPoints so we have the saved state,
            // then add to completedLevels and save again.
            // THE BUG WAS: old code mutated completedLevels in-memory but NEVER called save().
            User user = userService.getUserByClerkId(clerkId);
            if (!user.getCompletedLevels().contains(puzzleId)) {
                user.getCompletedLevels().add(puzzleId);
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
                log.info("Saved completedLevel={} for user={}. totalCompleted={}",
                    puzzle.getLevelNumber(), clerkId, user.getCompletedLevels().size());
            }

            // Step 3: unlock any puzzles the user now qualifies for with new points total
            autoUnlockEligiblePuzzles(clerkId);
        }

        return correct;
    }

    private void autoUnlockEligiblePuzzles(String clerkId) {
        User user = userService.getUserByClerkId(clerkId);
        List<Puzzle> all = puzzleRepository.findByActiveOrderByLevelNumberAsc(true);

        boolean changed = false;
        for (Puzzle p : all) {
            if (!user.getUnlockedPuzzles().contains(p.getId())
                    && user.getTotalPoints() >= p.getUnlockRequirement()) {
                user.getUnlockedPuzzles().add(p.getId());
                changed = true;
                log.info("Auto-unlocked Level={} for user={} (points={})",
                    p.getLevelNumber(), clerkId, user.getTotalPoints());
            }
        }

        if (changed) {
            userRepository.save(user);
        }
    }

    public void deletePuzzle(String id) {
        puzzleRepository.deleteById(id);
    }

    public Puzzle createPuzzle(Puzzle puzzle) {
        puzzle.setCreatedAt(LocalDateTime.now());
        puzzle.setActive(true);
        return puzzleRepository.save(puzzle);
    }
}