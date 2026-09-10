package com.aimock.interview.service;

import com.aimock.interview.dto.answer.AnswerEvaluationResponse;
import com.aimock.interview.dto.answer.SubmitAnswerRequest;

import java.util.UUID;

public interface AnswerService {
    AnswerEvaluationResponse submitAnswer(UUID userId, UUID interviewId, SubmitAnswerRequest request);
}
