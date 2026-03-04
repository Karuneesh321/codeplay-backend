package com.codeplay.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "leaderboard")
public class LeaderboardEntry {

    @Id
    private String id;

    private String userId;
    private String clerkId;
    private String name;
    private String email;

    private int totalPoints;
    private int rank;
    private int streak;
    private int completedLevelsCount;

    private String topCategory; // Best performing category

    private LocalDateTime updatedAt;
}
