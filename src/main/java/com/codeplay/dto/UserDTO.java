package com.codeplay.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserDTO {
    private String id;
    private String clerkId;
    private String name;
    private String email;
    private String avatarUrl;
    private String role;
    private int totalPoints;
    private int rank;
    private int streak;
    private List<String> completedLevels;
    private List<String> unlockedPuzzles;
    private int quizAttempts;
}
