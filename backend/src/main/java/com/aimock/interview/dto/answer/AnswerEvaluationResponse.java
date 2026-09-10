package com.aimock.interview.dto.answer;

import com.aimock.interview.dto.question.QuestionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerEvaluationResponse {
    private UUID id;
    private UUID questionId;
    private String answerText;
    private Integer technicalScore;
    private Integer relevanceScore;
    private Integer clarityScore;
    private Integer depthScore;
    private Integer overallScore;
    private String feedback;
    private List<String> strengths;
    private List<String> weaknesses;
    private LocalDateTime submittedAt;
    private boolean isInterviewCompleted;
    private QuestionResponse nextQuestion;
}
