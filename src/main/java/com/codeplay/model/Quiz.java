package com.codeplay.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "quizzes")
public class Quiz {
    @Id
    private String id;

    private String category;       // CORE_CS, DSA, DATABASE, SOFTWARE_DEV, DEVELOPMENT, CLERK_AUTH
    private String difficulty;     // EASY, MEDIUM, HARD
    private String question;
    private List<String> options;
    private int correctAnswerIndex;
    private String explanation;
    private int points;
    private int timeLimit;         // seconds
    private boolean isDailyChallenge;
    private LocalDateTime dailyChallengeDate;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
