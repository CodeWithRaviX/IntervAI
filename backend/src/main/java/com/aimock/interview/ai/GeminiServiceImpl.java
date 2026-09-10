package com.aimock.interview.ai;

import com.aimock.interview.dto.ai.GeminiEvaluationResponse;
import com.aimock.interview.dto.ai.GeminiQuestionGenerationResponse;
import com.aimock.interview.dto.ai.GeminiReportGenerationResponse;
import com.aimock.interview.dto.ai.GeminiResumeAnalysisResponse;
import com.aimock.interview.entity.Difficulty;
import com.aimock.interview.entity.ExperienceLevel;
import com.aimock.interview.entity.InterviewType;
import com.aimock.interview.entity.JobRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiServiceImpl implements GeminiService {

    private final GeminiClient geminiClient;
    private final GeminiPromptBuilder promptBuilder;
    private final GeminiResponseParser responseParser;

    @Override
    public GeminiQuestionGenerationResponse generateFirstQuestion(
            JobRole jobRole,
            ExperienceLevel experienceLevel,
            Difficulty difficulty,
            InterviewType interviewType,
            String resumeContext) {

        String prompt = promptBuilder.buildFirstQuestionPrompt(jobRole, experienceLevel, difficulty, interviewType, resumeContext);
        String rawResponse = geminiClient.generateContent(prompt);
        return responseParser.parseQuestion(rawResponse, jobRole.name().replace('_', ' '));
    }

    @Override
    public GeminiEvaluationResponse evaluateAnswerAndGenerateFollowUp(
            JobRole jobRole,
            ExperienceLevel experienceLevel,
            Difficulty difficulty,
            InterviewType interviewType,
            String currentQuestion,
            String candidateAnswer,
            List<Map<String, String>> previousQuestionsAndAnswers,
            boolean isLastQuestion) {

        String prompt = promptBuilder.buildEvaluationAndAdaptiveFollowUpPrompt(
                jobRole, experienceLevel, difficulty, interviewType, currentQuestion, candidateAnswer, previousQuestionsAndAnswers, isLastQuestion);
        String rawResponse = geminiClient.generateContent(prompt);
        return responseParser.parseEvaluation(rawResponse, isLastQuestion);
    }

    @Override
    public GeminiReportGenerationResponse generateInterviewReport(
            JobRole jobRole,
            ExperienceLevel experienceLevel,
            Difficulty difficulty,
            List<Map<String, Object>> qaHistory) {

        String prompt = promptBuilder.buildReportPrompt(jobRole, experienceLevel, difficulty, qaHistory);
        String rawResponse = geminiClient.generateContent(prompt);
        return responseParser.parseReport(rawResponse);
    }

    @Override
    public GeminiResumeAnalysisResponse analyzeResume(String resumeText) {
        String prompt = promptBuilder.buildResumeAnalysisPrompt(resumeText);
        String rawResponse = geminiClient.generateContent(prompt);
        return responseParser.parseResumeAnalysis(rawResponse);
    }
}
