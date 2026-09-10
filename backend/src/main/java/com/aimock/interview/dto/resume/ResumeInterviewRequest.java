package com.aimock.interview.dto.resume;

import com.aimock.interview.entity.Difficulty;
import com.aimock.interview.entity.ExperienceLevel;
import com.aimock.interview.entity.InterviewType;
import com.aimock.interview.entity.JobRole;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeInterviewRequest {

    @NotNull(message = "Job role is required")
    private JobRole jobRole;

    @NotNull(message = "Experience level is required")
    private ExperienceLevel experienceLevel;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    @NotNull(message = "Interview type is required")
    private InterviewType interviewType;

    @NotNull(message = "Number of questions is required")
    @Min(3)
    @Max(15)
    private Integer totalQuestions;

    @NotBlank(message = "Extracted resume content is required")
    private String resumeText;
}
