package com.codeplay.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "daily_challenges")
public class DailyChallenge {
    @Id
    private String id;
    private LocalDate challengeDate;
    private String quizId;
    private int bonusMultiplier;  // 2x points
    private int timeLimit;        // shorter time limit
    private List<String> completedByUsers;
    private boolean active;
}
