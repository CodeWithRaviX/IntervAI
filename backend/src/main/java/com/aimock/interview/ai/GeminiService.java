package com.aimock.interview.ai;

import com.aimock.interview.dto.ai.GeminiEvaluationResponse;
import com.aimock.interview.dto.ai.GeminiQuestionGenerationResponse;
import com.aimock.interview.dto.ai.GeminiReportGenerationResponse;
import com.aimock.interview.dto.ai.GeminiResumeAnalysisResponse;
import com.aimock.interview.entity.Difficulty;
import com.aimock.interview.entity.ExperienceLevel;
import com.aimock.interview.entity.InterviewType;
import com.aimock.interview.entity.JobRole;

import java.util.List;
import java.util.Map;

public interface GeminiService {

    GeminiQuestionGenerationResponse generateFirstQuestion(
            JobRole jobRole,
            ExperienceLevel experienceLevel,
            Difficulty difficulty,
            InterviewType interviewType,
            String resumeContext
    );

    GeminiEvaluationResponse evaluateAnswerAndGenerateFollowUp(
            JobRole jobRole,
            ExperienceLevel experienceLevel,
            Difficulty difficulty,
            InterviewType interviewType,
            String currentQuestion,
            String candidateAnswer,
            List<Map<String, String>> previousQuestionsAndAnswers,
            boolean isLastQuestion
    );

    GeminiReportGenerationResponse generateInterviewReport(
            JobRole jobRole,
            ExperienceLevel experienceLevel,
            Difficulty difficulty,
            List<Map<String, Object>> qaHistory
    );

    GeminiResumeAnalysisResponse analyzeResume(String resumeText);
}
