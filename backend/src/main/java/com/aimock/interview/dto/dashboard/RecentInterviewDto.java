package com.aimock.interview.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentInterviewDto {
    private UUID id;
    private String jobRole;
    private String difficulty;
    private String status;
    private Integer totalQuestions;
    private Integer completedQuestions;
    private Double overallScore;
    private LocalDateTime createdAt;
}
