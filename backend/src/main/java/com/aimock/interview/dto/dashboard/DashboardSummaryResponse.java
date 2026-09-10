package com.aimock.interview.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private long totalInterviews;
    private long completedInterviews;
    private Double averageScore;
    private BigDecimal bestScore;
    private List<RecentInterviewDto> recentInterviews;
    private List<TechnologyScoreDto> technologyBreakdown;
    private List<String> topWeakAreas;
    private List<String> recommendedTopics;
}
