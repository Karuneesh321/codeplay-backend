package com.codeplay.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LeaderboardEntry {
    private int rank;
    private String userId;
    private String name;
    private String avatarUrl;
    private int totalPoints;
    private int completedLevels;
    private int streak;
}
