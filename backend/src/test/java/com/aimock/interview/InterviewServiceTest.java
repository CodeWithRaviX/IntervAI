package com.aimock.interview;

import com.aimock.interview.ai.GeminiService;
import com.aimock.interview.dto.ai.GeminiQuestionGenerationResponse;
import com.aimock.interview.dto.interview.CreateInterviewRequest;
import com.aimock.interview.dto.interview.InterviewResponse;
import com.aimock.interview.entity.*;
import com.aimock.interview.exception.InvalidInterviewStateException;
import com.aimock.interview.repository.InterviewReportRepository;
import com.aimock.interview.repository.InterviewRepository;
import com.aimock.interview.repository.QuestionRepository;
import com.aimock.interview.repository.UserRepository;
import com.aimock.interview.service.AuditLogService;
import com.aimock.interview.service.impl.InterviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private InterviewReportRepository reportRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GeminiService geminiService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private InterviewServiceImpl interviewService;

    private User sampleUser;
    private Interview sampleInterview;

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
                .totalQuestions(5)
                .completedQuestions(0)
                .status(InterviewStatus.CREATED)
                .build();
    }

    @Test
    @DisplayName("Should create interview in CREATED status")
    void testCreateInterviewSuccess() {
        CreateInterviewRequest request = CreateInterviewRequest.builder()
                .jobRole(JobRole.JAVA_BACKEND_DEVELOPER)
                .experienceLevel(ExperienceLevel.MID_LEVEL)
                .difficulty(Difficulty.MEDIUM)
                .interviewType(InterviewType.TECHNICAL)
                .totalQuestions(5)
                .build();

        when(userRepository.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));
        when(interviewRepository.save(any(Interview.class))).thenReturn(sampleInterview);

        InterviewResponse response = interviewService.createInterview(sampleUser.getId(), request);

        assertNotNull(response);
        assertEquals(InterviewStatus.CREATED, response.getStatus());
        assertEquals(JobRole.JAVA_BACKEND_DEVELOPER, response.getJobRole());
    }

    @Test
    @DisplayName("Should start interview and generate 1st question via Gemini")
    void testStartInterviewSuccess() {
        UUID interviewId = sampleInterview.getId();
        when(interviewRepository.findById(interviewId)).thenReturn(Optional.of(sampleInterview));

        GeminiQuestionGenerationResponse geminiQ = GeminiQuestionGenerationResponse.builder()
                .question("What is Spring Boot Auto-Configuration?")
                .questionType("TECHNICAL")
                .technology("Spring Boot")
                .build();

        when(geminiService.generateFirstQuestion(any(), any(), any(), any(), any())).thenReturn(geminiQ);

        Question mockSavedQ = Question.builder()
                .id(UUID.randomUUID())
                .interview(sampleInterview)
                .sequenceNumber(1)
                .questionText(geminiQ.getQuestion())
                .questionType("TECHNICAL")
                .technology("Spring Boot")
                .build();

        when(questionRepository.save(any(Question.class))).thenReturn(mockSavedQ);
        when(interviewRepository.save(any(Interview.class))).thenReturn(sampleInterview);

        InterviewResponse response = interviewService.startInterview(sampleUser.getId(), interviewId);

        assertNotNull(response);
        assertEquals(InterviewStatus.IN_PROGRESS, sampleInterview.getStatus());
        assertNotNull(response.getCurrentQuestion());
        assertEquals("What is Spring Boot Auto-Configuration?", response.getCurrentQuestion().getQuestionText());
    }

    @Test
    @DisplayName("Should prevent starting already completed interview")
    void testStartCompletedInterviewThrows() {
        sampleInterview.setStatus(InterviewStatus.COMPLETED);
        when(interviewRepository.findById(sampleInterview.getId())).thenReturn(Optional.of(sampleInterview));

        assertThrows(InvalidInterviewStateException.class, () ->
                interviewService.startInterview(sampleUser.getId(), sampleInterview.getId()));
    }
}
