package com.codeplay.controller;

import com.codeplay.dto.ApiResponse;
import com.codeplay.model.Puzzle;
import com.codeplay.service.PuzzleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/puzzle")
@RequiredArgsConstructor
public class PuzzleController {

    private final PuzzleService puzzleService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Puzzle>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(puzzleService.getAllPuzzles()));
    }

    @GetMapping("/unlocked")
    public ResponseEntity<ApiResponse<List<Puzzle>>> getUnlocked(Authentication auth) {
        String clerkId = (String) auth.getPrincipal();
        return ResponseEntity.ok(ApiResponse.success(puzzleService.getUnlockedPuzzles(clerkId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Puzzle>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(puzzleService.getPuzzleById(id)));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> submit(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        String clerkId = (String) auth.getPrincipal();
        boolean correct = puzzleService.submitSolution(clerkId, id, body.get("solution"));
        return ResponseEntity.ok(ApiResponse.success(Map.of("correct", correct)));
    }
}
