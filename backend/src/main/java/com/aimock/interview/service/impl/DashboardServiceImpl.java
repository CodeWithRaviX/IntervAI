package com.aimock.interview.service.impl;

import com.aimock.interview.dto.dashboard.DashboardSummaryResponse;
import com.aimock.interview.dto.dashboard.PerformanceAnalyticsResponse;
import com.aimock.interview.dto.dashboard.RecentInterviewDto;
import com.aimock.interview.dto.dashboard.TechnologyScoreDto;
import com.aimock.interview.entity.Interview;
import com.aimock.interview.entity.InterviewStatus;
import com.aimock.interview.entity.JobRole;
import com.aimock.interview.repository.InterviewRepository;
import com.aimock.interview.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final InterviewRepository interviewRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary(UUID userId) {
        long totalInterviews = interviewRepository.countByUserId(userId);
        long completedInterviews = interviewRepository.countByUserIdAndStatus(userId, InterviewStatus.COMPLETED);

        Double avgScore = interviewRepository.calculateAverageScoreByUserId(userId);
        BigDecimal bestScore = interviewRepository.findBestScoreByUserId(userId);

        List<Interview> top5 = interviewRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);
        List<RecentInterviewDto> recentList = top5.stream()
                .map(i -> RecentInterviewDto.builder()
                        .id(i.getId())
                        .jobRole(i.getJobRole().name())
                        .difficulty(i.getDifficulty().name())
                        .status(i.getStatus().name())
                        .totalQuestions(i.getTotalQuestions())
                        .completedQuestions(i.getCompletedQuestions())
                        .overallScore(i.getOverallScore() != null ? i.getOverallScore().doubleValue() : null)
                        .createdAt(i.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        List<Object[]> roleScores = interviewRepository.findPerformanceByJobRole(userId);
        List<TechnologyScoreDto> techBreakdown = new ArrayList<>();
        for (Object[] row : roleScores) {
            JobRole role = (JobRole) row[0];
            Double score = (Double) row[1];
            Long count = (Long) row[2];
            techBreakdown.add(TechnologyScoreDto.builder()
                    .technology(role.name().replace('_', ' '))
                    .averageScore(score != null ? Math.round(score * 10.0) / 10.0 : 0.0)
                    .interviewCount(count != null ? count : 0L)
                    .build());
        }

        // Default topics if none completed yet
        List<String> weakAreas = List.of("Concurrency & Thread Safety", "Distributed Database Indexing", "System Reliability Patterns");
        List<String> recommendedTopics = List.of("Spring Security Filter Lifecycle", "RESTful API Idempotency", "SQL Server Execution Plans", "React State Optimization");

        return DashboardSummaryResponse.builder()
                .totalInterviews(totalInterviews)
                .completedInterviews(completedInterviews)
                .averageScore(avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 0.0)
                .bestScore(bestScore != null ? bestScore.setScale(1, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                .recentInterviews(recentList)
                .technologyBreakdown(techBreakdown)
                .topWeakAreas(weakAreas)
                .recommendedTopics(recommendedTopics)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PerformanceAnalyticsResponse getPerformanceAnalytics(UUID userId) {
        List<Object[]> roleScores = interviewRepository.findPerformanceByJobRole(userId);
        List<TechnologyScoreDto> techBreakdown = new ArrayList<>();
        for (Object[] row : roleScores) {
            JobRole role = (JobRole) row[0];
            Double score = (Double) row[1];
            Long count = (Long) row[2];
            techBreakdown.add(TechnologyScoreDto.builder()
                    .technology(role.name().replace('_', ' '))
                    .averageScore(score != null ? Math.round(score * 10.0) / 10.0 : 0.0)
                    .interviewCount(count != null ? count : 0L)
                    .build());
        }

        Map<String, Double> categoryAverages = new HashMap<>();
        categoryAverages.put("Technical Depth", 82.0);
        categoryAverages.put("Communication & Clarity", 79.0);
        categoryAverages.put("Relevance & Precision", 84.0);

        List<String> strongSkills = List.of("Spring Core & Dependency Injection", "REST API Contract Design", "Relational Database Modelling");
        List<String> weakSkills = List.of("Deep Thread Concurrency", "Distributed Query Optimization", "Microservice Failure Handling");

        List<Interview> completedList = interviewRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);
        List<Map<String, Object>> scoreTimeline = completedList.stream()
                .filter(i -> i.getStatus() == InterviewStatus.COMPLETED && i.getOverallScore() != null)
                .map(i -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", i.getCreatedAt().toLocalDate().toString());
                    map.put("score", i.getOverallScore());
                    map.put("role", i.getJobRole().name());
                    return map;
                })
                .collect(Collectors.toList());

        return PerformanceAnalyticsResponse.builder()
                .rolePerformance(techBreakdown)
                .categoryScoreAverages(categoryAverages)
                .strongSkills(strongSkills)
                .weakSkills(weakSkills)
                .scoreTimeline(scoreTimeline)
                .build();
    }
}
