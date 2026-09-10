package com.aimock.interview.dto.resume;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeUploadResponse {
    private String extractedText;
    private List<String> detectedSkills;
    private List<String> detectedProjects;
    private String suggestedRole;
}
