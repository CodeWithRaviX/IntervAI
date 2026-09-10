package com.aimock.interview.service.impl;

import com.aimock.interview.ai.GeminiService;
import com.aimock.interview.dto.ai.GeminiQuestionGenerationResponse;
import com.aimock.interview.dto.ai.GeminiReportGenerationResponse;
import com.aimock.interview.dto.common.PagedResponse;
import com.aimock.interview.dto.interview.CreateInterviewRequest;
import com.aimock.interview.dto.interview.InterviewDetailResponse;
import com.aimock.interview.dto.interview.InterviewResponse;
import com.aimock.interview.dto.interview.InterviewSummaryResponse;
import com.aimock.interview.dto.question.QuestionResponse;
import com.aimock.interview.dto.report.InterviewReportResponse;
import com.aimock.interview.dto.answer.AnswerEvaluationResponse;
import com.aimock.interview.entity.*;
import com.aimock.interview.exception.BadRequestException;
import com.aimock.interview.exception.InvalidInterviewStateException;
import com.aimock.interview.exception.ResourceNotFoundException;
import com.aimock.interview.exception.UnauthorizedException;
import com.aimock.interview.repository.InterviewReportRepository;
import com.aimock.interview.repository.InterviewRepository;
import com.aimock.interview.repository.QuestionRepository;
import com.aimock.interview.repository.UserRepository;
import com.aimock.interview.security.SecurityUtils;
import com.aimock.interview.service.AuditLogService;
import com.aimock.interview.service.InterviewService;
import com.aimock.interview.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final QuestionRepository questionRepository;
    private final InterviewReportRepository reportRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public InterviewResponse createInterview(UUID userId, CreateInterviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Interview interview = Interview.builder()
                .user(user)
                .jobRole(request.getJobRole())
                .experienceLevel(request.getExperienceLevel())
                .difficulty(request.getDifficulty())
                .interviewType(request.getInterviewType())
                .totalQuestions(request.getTotalQuestions() != null ? request.getTotalQuestions() : 5)
                .completedQuestions(0)
                .status(InterviewStatus.CREATED)
                .build();

        Interview saved = interviewRepository.save(interview);
        auditLogService.logAction(userId, "CREATE_INTERVIEW", "INTERVIEW", saved.getId().toString(), "Interview created");

        return mapToInterviewResponse(saved, null);
    }

    @Override
    @Transactional
    public InterviewResponse startInterview(UUID userId, UUID interviewId) {
        Interview interview = getInterviewAndValidateOwnership(interviewId, userId);

        if (interview.getStatus() != InterviewStatus.CREATED) {
            throw new InvalidInterviewStateException("Cannot start interview in status: " + interview.getStatus());
        }

        interview.setStatus(InterviewStatus.IN_PROGRESS);
        interview.setStartTime(LocalDateTime.now());

        // Generate 1st Question via Gemini AI
        GeminiQuestionGenerationResponse aiQ = geminiService.generateFirstQuestion(
                interview.getJobRole(),
                interview.getExperienceLevel(),
                interview.getDifficulty(),
                interview.getInterviewType(),
                interview.getResumeExtractedText()
        );

        String questionText = (aiQ != null && aiQ.getQuestion() != null && !aiQ.getQuestion().trim().isEmpty())
                ? aiQ.getQuestion().trim()
                : "Can you explain how Dependency Injection works in Spring Boot and compare Bean creation scopes?";

        String questionType = (aiQ != null && aiQ.getQuestionType() != null) ? aiQ.getQuestionType() : "TECHNICAL";
        String technology = (aiQ != null && aiQ.getTechnology() != null) ? aiQ.getTechnology() : interview.getJobRole().name().replace('_', ' ');

        Question question = Question.builder()
                .interview(interview)
                .sequenceNumber(1)
                .questionText(questionText)
                .questionType(questionType)
                .technology(technology)
                .build();

        Question savedQuestion = questionRepository.save(question);
        interview.addQuestion(savedQuestion);
        Interview savedInterview = interviewRepository.save(interview);

        auditLogService.logAction(userId, "START_INTERVIEW", "INTERVIEW", savedInterview.getId().toString(), "Interview started");

        QuestionResponse questionResponse = mapToQuestionResponse(savedQuestion);
        return mapToInterviewResponse(savedInterview, questionResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewDetailResponse getInterviewDetails(UUID userId, UUID interviewId) {
        Interview interview = getInterviewAndValidateOwnership(interviewId, userId);

        List<Question> questions = questionRepository.findQuestionsWithAnswersByInterviewId(interviewId);
        List<QuestionResponse> questionResponses = questions.stream()
                .map(this::mapToQuestionResponse)
                .collect(Collectors.toList());

        InterviewReportResponse reportResponse = null;
        if (interview.getReport() != null) {
            reportResponse = mapToReportResponse(interview.getReport());
        } else {
            Optional<InterviewReport> reportOpt = reportRepository.findByInterviewId(interviewId);
            if (reportOpt.isPresent()) {
                reportResponse = mapToReportResponse(reportOpt.get());
            }
        }

        return InterviewDetailResponse.builder()
                .id(interview.getId())
                .jobRole(interview.getJobRole())
                .experienceLevel(interview.getExperienceLevel())
                .difficulty(interview.getDifficulty())
                .interviewType(interview.getInterviewType())
                .status(interview.getStatus())
                .totalQuestions(interview.getTotalQuestions())
                .completedQuestions(interview.getCompletedQuestions())
                .overallScore(interview.getOverallScore())
                .questions(questionResponses)
                .report(reportResponse)
                .startTime(interview.getStartTime())
                .endTime(interview.getEndTime())
                .createdAt(interview.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<InterviewSummaryResponse> getUserInterviews(UUID userId, int page, int size) {
        if (userId == null) {
            throw new UnauthorizedException("User session is not authenticated");
        }
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Interview> interviewPage = interviewRepository.findByUserId(userId, pageRequest);

        List<InterviewSummaryResponse> content = interviewPage.getContent().stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());

        return PagedResponse.<InterviewSummaryResponse>builder()
                .content(content)
                .pageNumber(interviewPage.getNumber())
                .pageSize(interviewPage.getSize())
                .totalElements(interviewPage.getTotalElements())
                .totalPages(interviewPage.getTotalPages())
                .last(interviewPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public InterviewDetailResponse completeInterview(UUID userId, UUID interviewId) {
        Interview interview = getInterviewAndValidateOwnership(interviewId, userId);

        if (interview.getStatus() == InterviewStatus.COMPLETED) {
            return getInterviewDetails(userId, interviewId);
        }

        if (interview.getStatus() == InterviewStatus.CANCELLED) {
            throw new InvalidInterviewStateException("Cannot complete a cancelled interview");
        }

        List<Question> questions = questionRepository.findQuestionsWithAnswersByInterviewId(interviewId);

        List<Map<String, Object>> qaHistory = new ArrayList<>();
        double totalScores = 0;
        int answerCount = 0;

        for (Question q : questions) {
            if (q.getAnswer() != null) {
                Answer a = q.getAnswer();
                Map<String, Object> map = new HashMap<>();
                map.put("question", q.getQuestionText());
                map.put("answer", a.getAnswerText());
                map.put("technicalScore", a.getTechnicalScore());
                map.put("relevanceScore", a.getRelevanceScore());
                map.put("clarityScore", a.getClarityScore());
                map.put("depthScore", a.getDepthScore());
                map.put("overallScore", a.getOverallScore());
                qaHistory.add(map);

                totalScores += a.getOverallScore();
                answerCount++;
            }
        }

        double averageAnswerScore = answerCount > 0 ? (totalScores / answerCount) : 0.0;
        double overallScorePercent = averageAnswerScore * 10.0;

        GeminiReportGenerationResponse aiReport = geminiService.generateInterviewReport(
                interview.getJobRole(),
                interview.getExperienceLevel(),
                interview.getDifficulty(),
                qaHistory
        );

        String summary = (aiReport != null && aiReport.getSummary() != null && !aiReport.getSummary().trim().isEmpty())
                ? aiReport.getSummary()
                : "The candidate completed the mock interview round demonstrating consistent knowledge in core technical concepts.";

        double finalOverall = (aiReport != null && aiReport.getOverallScore() > 0) ? aiReport.getOverallScore() : (overallScorePercent > 0 ? overallScorePercent : 75.0);
        double finalTech = (aiReport != null && aiReport.getTechnicalScore() > 0) ? aiReport.getTechnicalScore() : 75.0;
        double finalComm = (aiReport != null && aiReport.getCommunicationScore() > 0) ? aiReport.getCommunicationScore() : 75.0;
        double finalRel = (aiReport != null && aiReport.getRelevanceScore() > 0) ? aiReport.getRelevanceScore() : 75.0;
        double finalAvg = (aiReport != null && aiReport.getAverageAnswerScore() > 0) ? aiReport.getAverageAnswerScore() : (averageAnswerScore > 0 ? averageAnswerScore : 7.5);

        InterviewReport report = InterviewReport.builder()
                .interview(interview)
                .overallScore(BigDecimal.valueOf(finalOverall).setScale(2, RoundingMode.HALF_UP))
                .technicalScore(BigDecimal.valueOf(finalTech).setScale(2, RoundingMode.HALF_UP))
                .communicationScore(BigDecimal.valueOf(finalComm).setScale(2, RoundingMode.HALF_UP))
                .relevanceScore(BigDecimal.valueOf(finalRel).setScale(2, RoundingMode.HALF_UP))
                .averageAnswerScore(BigDecimal.valueOf(finalAvg).setScale(2, RoundingMode.HALF_UP))
                .strengthsJson(JsonUtils.toJson(aiReport != null ? aiReport.getStrengths() : List.of("Good core fundamentals")))
                .weaknessesJson(JsonUtils.toJson(aiReport != null ? aiReport.getWeaknesses() : List.of("Further practice with edge cases")))
                .recommendedTopicsJson(JsonUtils.toJson(aiReport != null ? aiReport.getRecommendedTopics() : List.of("Concurrency Internals", "Database Indexing")))
                .improvementSuggestionsJson(JsonUtils.toJson(aiReport != null ? aiReport.getImprovementSuggestions() : List.of("Elaborate on production tradeoffs")))
                .summary(summary)
                .build();

        reportRepository.save(report);

        interview.setStatus(InterviewStatus.COMPLETED);
        interview.setEndTime(LocalDateTime.now());
        interview.setOverallScore(report.getOverallScore());
        interviewRepository.save(interview);

        auditLogService.logAction(userId, "COMPLETE_INTERVIEW", "INTERVIEW", interview.getId().toString(), "Interview completed with score " + report.getOverallScore());

        return getInterviewDetails(userId, interviewId);
    }

    @Override
    @Transactional
    public void cancelInterview(UUID userId, UUID interviewId) {
        Interview interview = getInterviewAndValidateOwnership(interviewId, userId);

        if (interview.getStatus() == InterviewStatus.COMPLETED) {
            throw new InvalidInterviewStateException("Cannot cancel a completed interview");
        }

        interview.setStatus(InterviewStatus.CANCELLED);
        interview.setEndTime(LocalDateTime.now());
        interviewRepository.save(interview);

        auditLogService.logAction(userId, "CANCEL_INTERVIEW", "INTERVIEW", interviewId.toString(), "Interview cancelled");
    }

    private Interview getInterviewAndValidateOwnership(UUID interviewId, UUID userId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + interviewId));

        if (!interview.getUser().getId().equals(userId) && !SecurityUtils.hasRole("ROLE_ADMIN")) {
            throw new UnauthorizedException("You do not have permission to access this interview");
        }

        return interview;
    }

    private InterviewResponse mapToInterviewResponse(Interview interview, QuestionResponse currentQuestion) {
        return InterviewResponse.builder()
                .id(interview.getId())
                .jobRole(interview.getJobRole())
                .experienceLevel(interview.getExperienceLevel())
                .difficulty(interview.getDifficulty())
                .interviewType(interview.getInterviewType())
                .status(interview.getStatus())
                .totalQuestions(interview.getTotalQuestions())
                .completedQuestions(interview.getCompletedQuestions())
                .overallScore(interview.getOverallScore())
                .currentQuestion(currentQuestion)
                .startTime(interview.getStartTime())
                .endTime(interview.getEndTime())
                .createdAt(interview.getCreatedAt())
                .build();
    }

    private InterviewSummaryResponse mapToSummaryResponse(Interview interview) {
        return InterviewSummaryResponse.builder()
                .id(interview.getId())
                .jobRole(interview.getJobRole())
                .experienceLevel(interview.getExperienceLevel())
                .difficulty(interview.getDifficulty())
                .interviewType(interview.getInterviewType())
                .status(interview.getStatus())
                .totalQuestions(interview.getTotalQuestions())
                .completedQuestions(interview.getCompletedQuestions())
                .overallScore(interview.getOverallScore())
                .createdAt(interview.getCreatedAt())
                .build();
    }

    private QuestionResponse mapToQuestionResponse(Question question) {
        AnswerEvaluationResponse answerResponse = null;
        if (question.getAnswer() != null) {
            Answer a = question.getAnswer();
            answerResponse = AnswerEvaluationResponse.builder()
                    .id(a.getId())
                    .questionId(question.getId())
                    .answerText(a.getAnswerText())
                    .technicalScore(a.getTechnicalScore())
                    .relevanceScore(a.getRelevanceScore())
                    .clarityScore(a.getClarityScore())
                    .depthScore(a.getDepthScore())
                    .overallScore(a.getOverallScore())
                    .feedback(a.getFeedback())
                    .strengths(JsonUtils.fromJsonStringList(a.getStrengthsJson()))
                    .weaknesses(JsonUtils.fromJsonStringList(a.getWeaknessesJson()))
                    .submittedAt(a.getSubmittedAt())
                    .build();
        }

        return QuestionResponse.builder()
                .id(question.getId())
                .interviewId(question.getInterview().getId())
                .sequenceNumber(question.getSequenceNumber())
                .questionText(question.getQuestionText())
                .questionType(question.getQuestionType())
                .technology(question.getTechnology())
                .answer(answerResponse)
                .createdAt(question.getCreatedAt())
                .build();
    }

    private InterviewReportResponse mapToReportResponse(InterviewReport report) {
        return InterviewReportResponse.builder()
                .id(report.getId())
                .interviewId(report.getInterview().getId())
                .overallScore(report.getOverallScore())
                .technicalScore(report.getTechnicalScore())
                .communicationScore(report.getCommunicationScore())
                .relevanceScore(report.getRelevanceScore())
                .averageAnswerScore(report.getAverageAnswerScore())
                .strengths(JsonUtils.fromJsonStringList(report.getStrengthsJson()))
                .weaknesses(JsonUtils.fromJsonStringList(report.getWeaknessesJson()))
                .recommendedTopics(JsonUtils.fromJsonStringList(report.getRecommendedTopicsJson()))
                .improvementSuggestions(JsonUtils.fromJsonStringList(report.getImprovementSuggestionsJson()))
                .summary(report.getSummary())
                .createdAt(report.getCreatedAt())
                .build();
    }
}