package com.aimock.interview.dto.interview;

import com.aimock.interview.dto.question.QuestionResponse;
import com.aimock.interview.dto.report.InterviewReportResponse;
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
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewDetailResponse {
    private UUID id;
    private JobRole jobRole;
    private ExperienceLevel experienceLevel;
    private Difficulty difficulty;
    private InterviewType interviewType;
    private InterviewStatus status;
    private Integer totalQuestions;
    private Integer completedQuestions;
    private BigDecimal overallScore;
    private List<QuestionResponse> questions;
    private InterviewReportResponse report;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
}
