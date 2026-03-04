package com.codeplay.controller;

import com.codeplay.dto.ApiResponse;
import com.codeplay.model.*;
import com.codeplay.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final QuizService quizService;
    private final PuzzleService puzzleService;
    private final UserService userService;

    @PostMapping("/quiz")
    public ResponseEntity<ApiResponse<Quiz>> createQuiz(@RequestBody Quiz quiz) {
        return ResponseEntity.ok(ApiResponse.success(quizService.createQuiz(quiz)));
    }

    @DeleteMapping("/quiz/{id}")
    public ResponseEntity<ApiResponse<String>> deleteQuiz(@PathVariable String id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.ok(ApiResponse.success("Quiz deleted"));
    }

    @PostMapping("/puzzle")
    public ResponseEntity<ApiResponse<Puzzle>> createPuzzle(@RequestBody Puzzle puzzle) {
        return ResponseEntity.ok(ApiResponse.success(puzzleService.createPuzzle(puzzle)));
    }

    @DeleteMapping("/puzzle/{id}")
    public ResponseEntity<ApiResponse<String>> deletePuzzle(@PathVariable String id) {
        puzzleService.deletePuzzle(id);
        return ResponseEntity.ok(ApiResponse.success("Puzzle deleted"));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<ApiResponse<?>> getFullLeaderboard() {
        return ResponseEntity.ok(ApiResponse.success(userService.getLeaderboard(0, 100)));
    }
}