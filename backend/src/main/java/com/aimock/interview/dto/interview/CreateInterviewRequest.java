package com.aimock.interview.dto.interview;

import com.aimock.interview.entity.Difficulty;
import com.aimock.interview.entity.ExperienceLevel;
import com.aimock.interview.entity.InterviewType;
import com.aimock.interview.entity.JobRole;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInterviewRequest {

    @NotNull(message = "Job role is required")
    private JobRole jobRole;

    @NotNull(message = "Experience level is required")
    private ExperienceLevel experienceLevel;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    @NotNull(message = "Interview type is required")
    private InterviewType interviewType;

    @NotNull(message = "Number of questions is required")
    @Min(value = 3, message = "Minimum questions is 3")
    @Max(value = 15, message = "Maximum questions is 15")
    private Integer totalQuestions;
}
