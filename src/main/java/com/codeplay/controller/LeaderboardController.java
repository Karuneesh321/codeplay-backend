package com.codeplay.controller;

import com.codeplay.dto.LeaderboardDTO;
import com.codeplay.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<LeaderboardDTO.LeaderboardResponse> getLeaderboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        String clerkId = auth != null ? (String) auth.getPrincipal() : null;
        return ResponseEntity.ok(leaderboardService.getLeaderboard(page, size, clerkId));
    }

    @GetMapping("/top10")
    public ResponseEntity<List<LeaderboardDTO.LeaderboardEntryResponse>> getTop10() {
        return ResponseEntity.ok(leaderboardService.getTop10());
    }

    @GetMapping("/my-rank")
    public ResponseEntity<LeaderboardDTO.UserRankResponse> getMyRank(Authentication auth) {
        String clerkId = (String) auth.getPrincipal();
        return ResponseEntity.ok(leaderboardService.getUserRank(clerkId));
    }
}
