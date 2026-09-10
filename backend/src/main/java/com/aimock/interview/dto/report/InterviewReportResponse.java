package com.aimock.interview.dto.report;

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
public class InterviewReportResponse {
    private UUID id;
    private UUID interviewId;
    private BigDecimal overallScore;
    private BigDecimal technicalScore;
    private BigDecimal communicationScore;
    private BigDecimal relevanceScore;
    private BigDecimal averageAnswerScore;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> recommendedTopics;
    private List<String> improvementSuggestions;
    private String summary;
    private LocalDateTime createdAt;
}
