package com.codeplay.service;

import com.codeplay.dto.*;
import com.codeplay.model.*;
import com.codeplay.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final PuzzleRepository puzzleRepository;
    private final UserService userService;
    private final DailyChallengeRepository dailyChallengeRepository;

    public List<QuizDTO> getQuizzesByCategory(String category) {
        return quizRepository.findByCategoryAndActive(category, true)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<QuizDTO> getAllCategories() {
        return quizRepository.findByActive(true)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public QuizSubmitResponse submitAnswer(String clerkId, QuizSubmitRequest req) {
        Quiz quiz = quizRepository.findById(req.getQuizId())
            .orElseThrow(() -> new RuntimeException("Quiz not found"));

        boolean correct = quiz.getCorrectAnswerIndex() == req.getSelectedAnswer();
        int pointsEarned = 0;

        if (correct) {
            pointsEarned = quiz.getPoints();

            // Bonus for fast completion
            if (req.getTimeTaken() < quiz.getTimeLimit() / 2) {
                pointsEarned += (int)(quiz.getPoints() * 0.5);
            }

            // Daily challenge double points
            if (req.isDailyChallenge()) {
                pointsEarned *= 2;
            }

            userService.addPoints(clerkId, pointsEarned);
        }

        // Record quiz attempt
        User.QuizAttempt attempt = new User.QuizAttempt(
            quiz.getId(), quiz.getCategory(), correct ? quiz.getPoints() : 0,
            req.getTimeTaken(), LocalDateTime.now(), correct
        );
        userService.addQuizAttempt(clerkId, attempt);

        // Check if any puzzle unlocked
        User user = userService.getUserByClerkId(clerkId);
        String unlockedPuzzleId = null;
        boolean levelUnlocked = false;

        if (correct) {
            Optional<Puzzle> unlockable = puzzleRepository.findByUnlockRequirementLessThanEqual(user.getTotalPoints())
                .stream()
                .filter(p -> !user.getUnlockedPuzzles().contains(p.getId()))
                .findFirst();

            if (unlockable.isPresent()) {
                Puzzle puzzle = unlockable.get();
                userService.unlockPuzzle(clerkId, puzzle.getId());
                unlockedPuzzleId = puzzle.getId();
                levelUnlocked = true;
            }
        }

        return QuizSubmitResponse.builder()
            .correct(correct)
            .explanation(quiz.getExplanation())
            .pointsEarned(pointsEarned)
            .totalPoints(user.getTotalPoints() + pointsEarned)
            .newRank(user.getRank())
            .levelUnlocked(levelUnlocked)
            .unlockedPuzzleId(unlockedPuzzleId)
            .currentStreak(user.getStreak())
            .build();
    }

    public QuizDTO getDailyChallenge() {
        DailyChallenge dc = dailyChallengeRepository
            .findByChallengeDateAndActive(LocalDate.now(), true)
            .orElseThrow(() -> new RuntimeException("No daily challenge today"));

        Quiz quiz = quizRepository.findById(dc.getQuizId())
            .orElseThrow(() -> new RuntimeException("Quiz not found"));

        QuizDTO dto = toDTO(quiz);
        dto.setDailyChallenge(true);
        return dto;
    }

    public void deleteQuiz(String id) {
        quizRepository.deleteById(id);
    }

    public Quiz createQuiz(Quiz quiz) {
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setUpdatedAt(LocalDateTime.now());
        quiz.setActive(true);
        quiz.setPoints(calculatePoints(quiz.getDifficulty()));
        return quizRepository.save(quiz);
    }

    private int calculatePoints(String difficulty) {
        return switch (difficulty.toUpperCase()) {
            case "EASY" -> 5;
            case "MEDIUM" -> 10;
            case "HARD" -> 20;
            default -> 5;
        };
    }

    private QuizDTO toDTO(Quiz quiz) {
        return QuizDTO.builder()
            .id(quiz.getId())
            .category(quiz.getCategory())
            .difficulty(quiz.getDifficulty())
            .question(quiz.getQuestion())
            .options(quiz.getOptions())
            .points(quiz.getPoints())
            .timeLimit(quiz.getTimeLimit())
            .isDailyChallenge(quiz.isDailyChallenge())
            .build();
    }
}