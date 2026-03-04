package com.codeplay.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
public class QuizSubmitRequest {
    @NotNull
    private String quizId;
    @NotNull @Min(0)
    private int selectedAnswer;
    @Min(0)
    private int timeTaken;
    private boolean isDailyChallenge;
}
