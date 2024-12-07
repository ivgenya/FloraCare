package com.trmo.floracare.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private String username;
    private String email;
    private String googleId;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private UserStatsDTO stats;
}
