package com.codeplay.controller;

import com.codeplay.dto.*;
import com.codeplay.model.User;
import com.codeplay.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<UserDTO>> syncUser(@Valid @RequestBody CreateUserRequest req) {
        User user = userService.createOrUpdateUser(req);
        return ResponseEntity.ok(ApiResponse.success("User synced", toDTO(user)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getMe(Authentication auth) {
        String clerkId = (String) auth.getPrincipal();
        UserDTO user = userService.getUserProfile(clerkId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
            .id(user.getId())
            .clerkId(user.getClerkId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole())
            .totalPoints(user.getTotalPoints())
            .rank(user.getRank())
            .streak(user.getStreak())
            .build();
    }
}
