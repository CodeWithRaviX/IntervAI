package com.aimock.interview.service.impl;

import com.aimock.interview.dto.report.InterviewReportResponse;
import com.aimock.interview.entity.InterviewReport;
import com.aimock.interview.exception.ResourceNotFoundException;
import com.aimock.interview.exception.UnauthorizedException;
import com.aimock.interview.repository.InterviewReportRepository;
import com.aimock.interview.security.SecurityUtils;
import com.aimock.interview.service.ReportService;
import com.aimock.interview.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final InterviewReportRepository reportRepository;

    @Override
    @Transactional(readOnly = true)
    public InterviewReportResponse getReportByInterviewId(UUID userId, UUID interviewId) {
        InterviewReport report = reportRepository.findByInterviewId(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found for interview: " + interviewId));

        if (!report.getInterview().getUser().getId().equals(userId) && !SecurityUtils.hasRole("ROLE_ADMIN")) {
            throw new UnauthorizedException("You are not authorized to view this interview report");
        }

        return InterviewReportResponse.builder()
                .id(report.getId())
                .interviewId(report.getInterview().getId())
                .overallScore(report.getOverallScore())
                .technicalScore(report.getTechnicalScore())
                .communicationScore(report.getCommunicationScore())
                .relevanceScore(report.getRelevanceScore())
                .averageAnswerScore(report.getAverageAnswerScore())
                .strengths(JsonUtils.fromJsonStringList(report.getStrengthsJson()))
                .weaknesses(JsonUtils.fromJsonStringList(report.getWeaknessesJson()))
                .recommendedTopics(JsonUtils.fromJsonStringList(report.getRecommendedTopicsJson()))
                .improvementSuggestions(JsonUtils.fromJsonStringList(report.getImprovementSuggestionsJson()))
                .summary(report.getSummary())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
