package com.aimock.interview.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private String headline;
    private String targetRole;
    private Integer yearsOfExperience;
    private boolean enabled;
    private boolean accountNonLocked;
    private Set<String> roles;
    private long totalInterviews;
    private LocalDateTime createdAt;
}
