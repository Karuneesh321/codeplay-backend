package com.codeplay.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

public class LeaderboardDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LeaderboardResponse {
        private List<LeaderboardEntryResponse> entries;
        private int totalUsers;
        private int currentPage;
        private int totalPages;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LeaderboardEntryResponse {
        private int rank;
        private String userId;
        private String name;
        private int totalPoints;
        private int streak;
        private int completedLevelsCount;
        private String topCategory;
        private boolean isCurrentUser;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserRankResponse {
        private int globalRank;
        private int totalPoints;
        private int totalUsers;
        private double percentile;
    }
}
