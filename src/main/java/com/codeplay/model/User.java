package com.codeplay.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String clerkId;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String avatarUrl;
    private String role; // "USER" or "ADMIN"

    @Builder.Default
    private int totalPoints = 0;

    @Builder.Default
    private int rank = 0;

    @Builder.Default
    private int streak = 0;

    private LocalDateTime lastActiveDate;

    @Builder.Default
    private List<String> completedLevels = new ArrayList<>();

    @Builder.Default
    private List<QuizAttempt> quizHistory = new ArrayList<>();

    @Builder.Default
    private List<String> unlockedPuzzles = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizAttempt {
        private String quizId;
        private String category;
        private int score;
        private int timeTaken;
        private LocalDateTime attemptedAt;
        private boolean passed;
    }
}
