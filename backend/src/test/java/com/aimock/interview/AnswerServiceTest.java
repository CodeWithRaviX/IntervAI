package com.aimock.interview;

import com.aimock.interview.ai.GeminiService;
import com.aimock.interview.dto.ai.GeminiEvaluationResponse;
import com.aimock.interview.dto.answer.AnswerEvaluationResponse;
import com.aimock.interview.dto.answer.SubmitAnswerRequest;
import com.aimock.interview.entity.*;
import com.aimock.interview.exception.BadRequestException;
import com.aimock.interview.repository.AnswerRepository;
import com.aimock.interview.repository.InterviewRepository;
import com.aimock.interview.repository.QuestionRepository;
import com.aimock.interview.service.AuditLogService;
import com.aimock.interview.service.impl.AnswerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private GeminiService geminiService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AnswerServiceImpl answerService;

    private User sampleUser;
    private Interview sampleInterview;
    private Question sampleQuestion;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .fullName("Test User")
                .build();

        sampleInterview = Interview.builder()
                .id(UUID.randomUUID())
                .user(sampleUser)
                .jobRole(JobRole.JAVA_BACKEND_DEVELOPER)
                .experienceLevel(ExperienceLevel.MID_LEVEL)
                .difficulty(Difficulty.MEDIUM)
                .interviewType(InterviewType.TECHNICAL)
                .totalQuestions(3)
                .completedQuestions(0)
                .status(InterviewStatus.IN_PROGRESS)
                .build();

        sampleQuestion = Question.builder()
                .id(UUID.randomUUID())
                .interview(sampleInterview)
                .sequenceNumber(1)
                .questionText("What is Spring Boot?")
                .questionType("TECHNICAL")
                .technology("Spring Boot")
                .build();
    }

    @Test
    @DisplayName("Should evaluate submitted answer and generate next adaptive question")
    void testSubmitAnswerSuccess() {
        SubmitAnswerRequest request = SubmitAnswerRequest.builder()
                .questionId(sampleQuestion.getId())
                .answerText("Spring Boot is an opinionated framework that simplifies Spring development by providing auto-configuration and embedded servers.")
                .build();

        when(interviewRepository.findById(sampleInterview.getId())).thenReturn(Optional.of(sampleInterview));
        when(questionRepository.findById(sampleQuestion.getId())).thenReturn(Optional.of(sampleQuestion));
        when(answerRepository.findByQuestionId(sampleQuestion.getId())).thenReturn(Optional.empty());
        when(questionRepository.findQuestionsWithAnswersByInterviewId(sampleInterview.getId())).thenReturn(List.of(sampleQuestion));

        GeminiEvaluationResponse aiEval = GeminiEvaluationResponse.builder()
                .technicalScore(9)
                .relevanceScore(9)
                .clarityScore(8)
                .depthScore(8)
                .overallScore(9)
                .feedback("Clear and concise explanation of Spring Boot.")
                .strengths(List.of("Mentions auto-configuration and embedded servers"))
                .weaknesses(List.of())
                .followUpQuestion("How does Spring Boot conditional configuration work?")
                .followUpTechnology("Spring Boot")
                .followUpType("TECHNICAL")
                .build();

        when(geminiService.evaluateAnswerAndGenerateFollowUp(any(), any(), any(), any(), any(), any(), any(), eq(false)))
                .thenReturn(aiEval);

        Answer savedAnswer = Answer.builder()
                .id(UUID.randomUUID())
                .question(sampleQuestion)
                .answerText(request.getAnswerText())
                .technicalScore(9)
                .relevanceScore(9)
                .clarityScore(8)
                .depthScore(8)
                .overallScore(9)
                .feedback("Clear and concise explanation of Spring Boot.")
                .build();

        when(answerRepository.save(any(Answer.class))).thenReturn(savedAnswer);

        Question nextQ = Question.builder()
                .id(UUID.randomUUID())
                .interview(sampleInterview)
                .sequenceNumber(2)
                .questionText("How does Spring Boot conditional configuration work?")
                .questionType("TECHNICAL")
                .technology("Spring Boot")
                .build();

        when(questionRepository.save(any(Question.class))).thenReturn(nextQ);

        AnswerEvaluationResponse response = answerService.submitAnswer(sampleUser.getId(), sampleInterview.getId(), request);

        assertNotNull(response);
        assertEquals(9, response.getOverallScore());
        assertEquals(9, response.getTechnicalScore());
        assertFalse(response.isInterviewCompleted());
        assertNotNull(response.getNextQuestion());
        assertEquals(2, response.getNextQuestion().getSequenceNumber());
        assertEquals("How does Spring Boot conditional configuration work?", response.getNextQuestion().getQuestionText());
    }

    @Test
    @DisplayName("Should reject duplicate answer submissions for the same question")
    void testSubmitDuplicateAnswerRejection() {
        SubmitAnswerRequest request = SubmitAnswerRequest.builder()
                .questionId(sampleQuestion.getId())
                .answerText("Sample answer text here for duplicate test...")
                .build();

        when(interviewRepository.findById(sampleInterview.getId())).thenReturn(Optional.of(sampleInterview));
        when(questionRepository.findById(sampleQuestion.getId())).thenReturn(Optional.of(sampleQuestion));
        when(answerRepository.findByQuestionId(sampleQuestion.getId())).thenReturn(Optional.of(mock(Answer.class)));

        assertThrows(BadRequestException.class, () ->
                answerService.submitAnswer(sampleUser.getId(), sampleInterview.getId(), request));
    }
}
