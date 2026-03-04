package com.codeplay.config;

import com.codeplay.model.Quiz;
import com.codeplay.model.Puzzle;
import com.codeplay.model.DailyChallenge;
import com.codeplay.repository.QuizRepository;
import com.codeplay.repository.PuzzleRepository;
import com.codeplay.repository.DailyChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final QuizRepository quizRepository;
    private final PuzzleRepository puzzleRepository;
    private final DailyChallengeRepository dailyChallengeRepository;

    @Override
    public void run(String... args) {
        if (quizRepository.count() == 0) { seedQuizzes(); log.info("Quizzes seeded"); }
        if (puzzleRepository.count() == 0) { seedPuzzles(); log.info("Puzzles seeded"); }
        ensureTodaysDailyChallenge();
    }

    private void ensureTodaysDailyChallenge() {
        LocalDate today = LocalDate.now();
        if (!dailyChallengeRepository.findByChallengeDate(today).isPresent()) {
            List<Quiz> quizzes = quizRepository.findByActive(true);
            if (!quizzes.isEmpty()) {
                Quiz picked = quizzes.get(0);
                DailyChallenge dc = DailyChallenge.builder()
                    .challengeDate(today).quizId(picked.getId())
                    .bonusMultiplier(2).timeLimit(picked.getTimeLimit())
                    .completedByUsers(new ArrayList<>()).active(true).build();
                dailyChallengeRepository.save(dc);
                log.info("Daily challenge created for {}", today);
            }
        }
    }

    private void seedQuizzes() {
        quizRepository.saveAll(List.of(
            Quiz.builder().category("DSA").difficulty("EASY").question("Time complexity of binary search?")
                .options(List.of("O(n)","O(log n)","O(n^2)","O(1)")).correctAnswerIndex(1)
                .explanation("Binary search = O(log n).").points(5).timeLimit(30)
                .active(true).isDailyChallenge(false).createdAt(LocalDateTime.now()).build(),
            Quiz.builder().category("DSA").difficulty("MEDIUM").question("Which data structure uses LIFO?")
                .options(List.of("Queue","Stack","Array","Linked List")).correctAnswerIndex(1)
                .explanation("Stack uses LIFO.").points(10).timeLimit(45)
                .active(true).isDailyChallenge(false).createdAt(LocalDateTime.now()).build(),
            Quiz.builder().category("CORE_CS").difficulty("EASY").question("What does CPU stand for?")
                .options(List.of("Central Processing Unit","Core Processing Unit","Central Program Unit","Computer Processing Unit")).correctAnswerIndex(0)
                .explanation("CPU = Central Processing Unit.").points(5).timeLimit(30)
                .active(true).isDailyChallenge(false).createdAt(LocalDateTime.now()).build(),
            Quiz.builder().category("DATABASE").difficulty("MEDIUM").question("Which SQL clause filters grouped records?")
                .options(List.of("WHERE","HAVING","GROUP BY","FILTER")).correctAnswerIndex(1)
                .explanation("HAVING filters after GROUP BY.").points(10).timeLimit(45)
                .active(true).isDailyChallenge(false).createdAt(LocalDateTime.now()).build(),
            Quiz.builder().category("SOFTWARE_DEV").difficulty("HARD").question("D in SOLID stands for?")
                .options(List.of("Data Abstraction","Dependency Inversion","Dynamic Binding","Design Pattern")).correctAnswerIndex(1)
                .explanation("D = Dependency Inversion Principle.").points(20).timeLimit(60)
                .active(true).isDailyChallenge(false).createdAt(LocalDateTime.now()).build(),
            Quiz.builder().category("DEVELOPMENT").difficulty("EASY").question("What does HTML stand for?")
                .options(List.of("HyperText Markup Language","High-level Text Machine Language","HyperText Machine Learning","HyperText Modeling Language")).correctAnswerIndex(0)
                .explanation("HTML = HyperText Markup Language.").points(5).timeLimit(30)
                .active(true).isDailyChallenge(false).createdAt(LocalDateTime.now()).build(),
            Quiz.builder().category("CLERK_AUTH").difficulty("MEDIUM").question("What does JWT stand for?")
                .options(List.of("Java Web Token","JSON Web Token","JavaScript Web Token","JSON Web Transfer")).correctAnswerIndex(1)
                .explanation("JWT = JSON Web Token.").points(10).timeLimit(45)
                .active(true).isDailyChallenge(false).createdAt(LocalDateTime.now()).build(),
            Quiz.builder().category("DSA").difficulty("HARD").question("Worst-case of QuickSort?")
                .options(List.of("O(n log n)","O(n)","O(n^2)","O(log n)")).correctAnswerIndex(2)
                .explanation("QuickSort worst case is O(n^2).").points(20).timeLimit(60)
                .active(true).isDailyChallenge(false).createdAt(LocalDateTime.now()).build()
        ));
    }

    private void seedPuzzles() {
        puzzleRepository.saveAll(List.of(
            Puzzle.builder().levelNumber(1).title("Hello World")
                .description("Output of: System.out.println(\"Hello\" + \" \" + \"World\");")
                .type("OUTPUT_PREDICT").unlockRequirement(0).solution("Hello World")
                .points(15).difficulty("EASY").hints(List.of("+ concatenates strings"))
                .active(true).createdAt(LocalDateTime.now()).build(),
            Puzzle.builder().levelNumber(2).title("Loop Logic")
                .description("What prints?\nfor(int i=0; i<3; i++) System.out.print(i + \" \");")
                .type("OUTPUT_PREDICT").unlockRequirement(10).solution("0 1 2")
                .points(20).difficulty("EASY").hints(List.of("i starts at 0, runs while i<3"))
                .active(true).createdAt(LocalDateTime.now()).build(),
            Puzzle.builder().levelNumber(3).title("Fibonacci")
                .description("6th Fibonacci number? (0,1,1,2,3,5,...)")
                .type("LOGIC").unlockRequirement(25).solution("8")
                .points(30).difficulty("MEDIUM").hints(List.of("Sum of two preceding numbers"))
                .active(true).createdAt(LocalDateTime.now()).build(),
            Puzzle.builder().levelNumber(4).title("Array Reverse")
                .description("Reverse [1,2,3,4,5]. Enter comma-separated, no spaces.")
                .type("LOGIC").unlockRequirement(50).solution("5,4,3,2,1")
                .points(40).difficulty("MEDIUM").hints(List.of("Read last index to first"))
                .active(true).createdAt(LocalDateTime.now()).build(),
            Puzzle.builder().levelNumber(5).title("Big O Space")
                .description("Space complexity of merge sort? (e.g. O(1))")
                .type("LOGIC").unlockRequirement(80).solution("O(n)")
                .points(50).difficulty("HARD").hints(List.of("Needs temp arrays for merging"))
                .active(true).createdAt(LocalDateTime.now()).build()
        ));
    }
}