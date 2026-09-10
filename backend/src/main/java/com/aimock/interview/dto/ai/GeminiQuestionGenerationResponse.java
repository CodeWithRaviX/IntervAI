package com.aimock.interview.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiQuestionGenerationResponse {
    private String question;
    private String questionType;
    private String technology;
}
