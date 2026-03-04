package com.codeplay.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QuizSubmitResponse {
    private boolean correct;
    private String explanation;
    private int pointsEarned;
    private int totalPoints;
    private int newRank;
    private boolean levelUnlocked;
    private String unlockedPuzzleId;
    private int currentStreak;
}
