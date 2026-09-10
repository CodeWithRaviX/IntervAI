package com.aimock.interview.dto.question;

import com.aimock.interview.dto.answer.AnswerEvaluationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {
    private UUID id;
    private UUID interviewId;
    private Integer sequenceNumber;
    private String questionText;
    private String questionType;
    private String technology;
    private AnswerEvaluationResponse answer;
    private LocalDateTime createdAt;
}
