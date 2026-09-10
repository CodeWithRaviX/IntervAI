package com.aimock.interview.dto.interview;

import com.aimock.interview.entity.Difficulty;
import com.aimock.interview.entity.ExperienceLevel;
import com.aimock.interview.entity.InterviewStatus;
import com.aimock.interview.entity.InterviewType;
import com.aimock.interview.entity.JobRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewSummaryResponse {
    private UUID id;
    private JobRole jobRole;
    private ExperienceLevel experienceLevel;
    private Difficulty difficulty;
    private InterviewType interviewType;
    private InterviewStatus status;
    private Integer totalQuestions;
    private Integer completedQuestions;
    private BigDecimal overallScore;
    private LocalDateTime createdAt;
}
