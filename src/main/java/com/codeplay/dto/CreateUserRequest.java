package com.codeplay.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class CreateUserRequest {
    @NotBlank
    private String clerkId;
    @NotBlank
    private String name;
    @Email @NotBlank
    private String email;
    private String avatarUrl;
}
