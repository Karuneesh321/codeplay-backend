package com.codeplay.controller;

import com.codeplay.dto.*;
import com.codeplay.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/categories/{category}")
    public ResponseEntity<ApiResponse<List<QuizDTO>>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(ApiResponse.success(quizService.getQuizzesByCategory(category)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<QuizDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(quizService.getAllCategories()));
    }

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<QuizDTO>> getDailyChallenge() {
        return ResponseEntity.ok(ApiResponse.success(quizService.getDailyChallenge()));
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<QuizSubmitResponse>> submit(
            @Valid @RequestBody QuizSubmitRequest req, Authentication auth) {
        String clerkId = (String) auth.getPrincipal();
        QuizSubmitResponse response = quizService.submitAnswer(clerkId, req);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
