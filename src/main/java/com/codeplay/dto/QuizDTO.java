package com.codeplay.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QuizDTO {
    private String id;
    private String category;
    private String difficulty;
    private String question;
    private List<String> options;
    private int points;
    private int timeLimit;
    private boolean isDailyChallenge;
}
