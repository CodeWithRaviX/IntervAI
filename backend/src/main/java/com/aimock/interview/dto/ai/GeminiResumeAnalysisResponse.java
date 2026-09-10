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
public class GeminiResumeAnalysisResponse {
    private String candidateName;
    private List<String> detectedSkills;
    private List<String> detectedProjects;
    private String suggestedRole;
    private String summary;
}
