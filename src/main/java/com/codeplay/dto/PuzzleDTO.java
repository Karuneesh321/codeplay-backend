package com.codeplay.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

public class PuzzleDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PuzzleRequest {
        private int levelNumber;
        private String title;
        private String description;
        private String category;
        private String difficulty;
        private int unlockRequirement;
        private String type;
        private List<String> hints;
        private String solution;
        private String explanation;
        private int points;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PuzzleResponse {
        private String id;
        private int levelNumber;
        private String title;
        private String description;
        private String category;
        private String difficulty;
        private int unlockRequirement;
        private String type;
        private List<String> hints;
        private int points;
        private boolean unlocked;
        private boolean completed;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PuzzleSolutionSubmit {
        private String puzzleId;
        private String solution;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PuzzleSolutionResult {
        private boolean correct;
        private int pointsEarned;
        private String explanation;
        private String message;
        private int newTotalPoints;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MazeProgressResponse {
        private List<PuzzleResponse> puzzles;
        private int userPoints;
        private int currentLevel;
        private int nextUnlockRequirement;
    }
}
