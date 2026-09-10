package com.aimock.interview.service.impl;

import com.aimock.interview.ai.GeminiService;
import com.aimock.interview.dto.ai.GeminiEvaluationResponse;
import com.aimock.interview.dto.answer.AnswerEvaluationResponse;
import com.aimock.interview.dto.answer.SubmitAnswerRequest;
import com.aimock.interview.dto.question.QuestionResponse;
import com.aimock.interview.entity.Answer;
import com.aimock.interview.entity.Interview;
import com.aimock.interview.entity.InterviewStatus;
import com.aimock.interview.entity.Question;
import com.aimock.interview.exception.BadRequestException;
import com.aimock.interview.exception.InvalidInterviewStateException;
import com.aimock.interview.exception.ResourceNotFoundException;
import com.aimock.interview.exception.UnauthorizedException;
import com.aimock.interview.repository.AnswerRepository;
import com.aimock.interview.repository.InterviewRepository;
import com.aimock.interview.repository.QuestionRepository;
import com.aimock.interview.security.SecurityUtils;
import com.aimock.interview.service.AnswerService;
import com.aimock.interview.service.AuditLogService;
import com.aimock.interview.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final InterviewRepository interviewRepository;
    private final GeminiService geminiService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public AnswerEvaluationResponse submitAnswer(UUID userId, UUID interviewId, SubmitAnswerRequest request) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + interviewId));

        if (!interview.getUser().getId().equals(userId) && !SecurityUtils.hasRole("ROLE_ADMIN")) {
            throw new UnauthorizedException("You do not have permission to submit answers for this interview");
        }

        if (interview.getStatus() != InterviewStatus.IN_PROGRESS) {
            throw new InvalidInterviewStateException("Cannot submit answer. Interview status is: " + interview.getStatus());
        }

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + request.getQuestionId()));

        if (!question.getInterview().getId().equals(interviewId)) {
            throw new BadRequestException("Question does not belong to this interview session");
        }

        // Duplicate answer submission guard
        if (answerRepository.findByQuestionId(question.getId()).isPresent()) {
            throw new BadRequestException("An answer has already been submitted for this question");
        }

        // Gather previous questions & answers for context
        List<Question> existingQuestions = questionRepository.findQuestionsWithAnswersByInterviewId(interviewId);
        List<Map<String, String>> previousQAList = new ArrayList<>();
        for (Question q : existingQuestions) {
            if (q.getAnswer() != null && !q.getId().equals(question.getId())) {
                Map<String, String> item = new HashMap<>();
                item.put("question", q.getQuestionText());
                item.put("answer", q.getAnswer().getAnswerText());
                item.put("score", String.valueOf(q.getAnswer().getOverallScore()));
                previousQAList.add(item);
            }
        }

        boolean isLastQuestion = question.getSequenceNumber() >= interview.getTotalQuestions();

        // AI Evaluation & Adaptive Next Question
        GeminiEvaluationResponse aiEval = geminiService.evaluateAnswerAndGenerateFollowUp(
                interview.getJobRole(),
                interview.getExperienceLevel(),
                interview.getDifficulty(),
                interview.getInterviewType(),
                question.getQuestionText(),
                request.getAnswerText().trim(),
                previousQAList,
                isLastQuestion
        );

        // Check again after AI call in case a concurrent thread saved it
        if (answerRepository.findByQuestionId(question.getId()).isPresent()) {
            throw new BadRequestException("An answer has already been submitted for this question");
        }

        int techScore = (aiEval != null && aiEval.getTechnicalScore() > 0) ? aiEval.getTechnicalScore() : 8;
        int relScore = (aiEval != null && aiEval.getRelevanceScore() > 0) ? aiEval.getRelevanceScore() : 8;
        int clarScore = (aiEval != null && aiEval.getClarityScore() > 0) ? aiEval.getClarityScore() : 7;
        int depthScore = (aiEval != null && aiEval.getDepthScore() > 0) ? aiEval.getDepthScore() : 7;
        int overallScore = (aiEval != null && aiEval.getOverallScore() > 0) ? aiEval.getOverallScore() : ((techScore + relScore + clarScore + depthScore) / 4);

        String feedback = (aiEval != null && aiEval.getFeedback() != null && !aiEval.getFeedback().trim().isEmpty())
                ? aiEval.getFeedback()
                : "Good response covering the core concepts. To improve further, discuss production performance and scaling considerations.";

        List<String> strengths = (aiEval != null && aiEval.getStrengths() != null) ? aiEval.getStrengths() : List.of("Clear technical explanation");
        List<String> weaknesses = (aiEval != null && aiEval.getWeaknesses() != null) ? aiEval.getWeaknesses() : List.of("Can elaborate further on architecture tradeoffs");

        Answer answer = Answer.builder()
                .question(question)
                .answerText(request.getAnswerText().trim())
                .technicalScore(techScore)
                .relevanceScore(relScore)
                .clarityScore(clarScore)
                .depthScore(depthScore)
                .overallScore(overallScore)
                .feedback(feedback)
                .strengthsJson(JsonUtils.toJson(strengths))
                .weaknessesJson(JsonUtils.toJson(weaknesses))
                .build();

        Answer savedAnswer = answerRepository.save(answer);
        question.setAnswer(savedAnswer);

        interview.setCompletedQuestions(interview.getCompletedQuestions() + 1);
        interviewRepository.save(interview);

        QuestionResponse nextQuestionResponse = null;

        if (!isLastQuestion) {
            int nextSeq = question.getSequenceNumber() + 1;
            String nextQuestionText = (aiEval != null && aiEval.getFollowUpQuestion() != null && !aiEval.getFollowUpQuestion().trim().isEmpty())
                    ? aiEval.getFollowUpQuestion()
                    : "How would you optimize this architecture for high throughput, low latency, and fault tolerance?";

            String nextTech = (aiEval != null && aiEval.getFollowUpTechnology() != null) ? aiEval.getFollowUpTechnology() : question.getTechnology();
            String nextType = (aiEval != null && aiEval.getFollowUpType() != null) ? aiEval.getFollowUpType() : question.getQuestionType();

            Question nextQuestion = Question.builder()
                    .interview(interview)
                    .sequenceNumber(nextSeq)
                    .questionText(nextQuestionText)
                    .questionType(nextType)
                    .technology(nextTech)
                    .build();

            Question savedNextQuestion = questionRepository.save(nextQuestion);
            nextQuestionResponse = QuestionResponse.builder()
                    .id(savedNextQuestion.getId())
                    .interviewId(interview.getId())
                    .sequenceNumber(savedNextQuestion.getSequenceNumber())
                    .questionText(savedNextQuestion.getQuestionText())
                    .questionType(savedNextQuestion.getQuestionType())
                    .technology(savedNextQuestion.getTechnology())
                    .createdAt(savedNextQuestion.getCreatedAt())
                    .build();
        }

        auditLogService.logAction(userId, "SUBMIT_ANSWER", "QUESTION", question.getId().toString(), "Answer submitted for Q" + question.getSequenceNumber() + " with score " + answer.getOverallScore());

        return AnswerEvaluationResponse.builder()
                .id(savedAnswer.getId())
                .questionId(question.getId())
                .answerText(savedAnswer.getAnswerText())
                .technicalScore(savedAnswer.getTechnicalScore())
                .relevanceScore(savedAnswer.getRelevanceScore())
                .clarityScore(savedAnswer.getClarityScore())
                .depthScore(savedAnswer.getDepthScore())
                .overallScore(savedAnswer.getOverallScore())
                .feedback(savedAnswer.getFeedback())
                .strengths(strengths)
                .weaknesses(weaknesses)
                .submittedAt(savedAnswer.getSubmittedAt())
                .isInterviewCompleted(isLastQuestion)
                .nextQuestion(nextQuestionResponse)
                .build();
    }
}