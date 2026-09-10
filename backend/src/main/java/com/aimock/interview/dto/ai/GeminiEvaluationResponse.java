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
public class GeminiEvaluationResponse {
    private int technicalScore;
    private int relevanceScore;
    private int clarityScore;
    private int depthScore;
    private int overallScore;
    private String feedback;
    private List<String> strengths;
    private List<String> weaknesses;
    private String followUpQuestion;
    private String followUpTechnology;
    private String followUpType;
}
