package com.aimock.interview.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiReportGenerationResponse {
    private double overallScore;
    private double technicalScore;
    private double communicationScore;
    private double relevanceScore;
    private double averageAnswerScore;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> recommendedTopics;
    private List<String> improvementSuggestions;
    private String summary;
}
