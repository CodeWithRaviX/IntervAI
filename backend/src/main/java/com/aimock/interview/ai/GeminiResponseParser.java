package com.aimock.interview.ai;

import com.aimock.interview.dto.ai.GeminiEvaluationResponse;
import com.aimock.interview.dto.ai.GeminiQuestionGenerationResponse;
import com.aimock.interview.dto.ai.GeminiReportGenerationResponse;
import com.aimock.interview.dto.ai.GeminiResumeAnalysisResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class GeminiResponseParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String extractJsonText(String rawResponse) {
        if (rawResponse == null || rawResponse.trim().isEmpty()) {
            return "{}";
        }

        try {
            JsonNode rootNode = objectMapper.readTree(rawResponse);
            if (rootNode.has("candidates")) {
                JsonNode candidates = rootNode.get("candidates");
                if (candidates.isArray() && !candidates.isEmpty()) {
                    JsonNode firstCandidate = candidates.get(0);
                    JsonNode content = firstCandidate.get("content");
                    if (content != null && content.has("parts")) {
                        JsonNode parts = content.get("parts");
                        if (parts.isArray() && !parts.isEmpty()) {
                            StringBuilder sb = new StringBuilder();
                            for (JsonNode part : parts) {
                                if (part.has("text")) {
                                    sb.append(part.get("text").asText());
                                }
                            }
                            if (sb.length() > 0) {
                                rawResponse = sb.toString();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Not outer API envelope or already extracted
        }

        String cleaned = rawResponse.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        cleaned = cleaned.trim();

        // If wrapped in additional surrounding prose, extract between first { and last }
        int firstBrace = cleaned.indexOf('{');
        int lastBrace = cleaned.lastIndexOf('}');
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            cleaned = cleaned.substring(firstBrace, lastBrace + 1);
        }

        return cleaned;
    }

    public GeminiQuestionGenerationResponse parseQuestion(String rawResponse, String defaultTopic) {
        if (rawResponse == null || rawResponse.trim().isEmpty()) {
            return getDefaultQuestion(defaultTopic);
        }
        String json = extractJsonText(rawResponse);
        try {
            GeminiQuestionGenerationResponse response = objectMapper.readValue(json, GeminiQuestionGenerationResponse.class);
            if (response == null || response.getQuestion() == null || response.getQuestion().trim().isEmpty()) {
                return getDefaultQuestion(defaultTopic);
            }
            if (response.getQuestionType() == null || response.getQuestionType().trim().isEmpty()) {
                response.setQuestionType("TECHNICAL");
            }
            if (response.getTechnology() == null || response.getTechnology().trim().isEmpty()) {
                response.setTechnology(defaultTopic != null ? defaultTopic : "Core Engineering");
            }
            return response;
        } catch (Exception e) {
            log.warn("Failed to parse Gemini question response JSON: {}. Using heuristic fallback.", json, e);
            return getDefaultQuestion(defaultTopic);
        }
    }

    public GeminiEvaluationResponse parseEvaluation(String rawResponse, boolean isLastQuestion) {
        if (rawResponse == null || rawResponse.trim().isEmpty()) {
            return getDefaultEvaluation(isLastQuestion);
        }
        String json = extractJsonText(rawResponse);
        try {
            GeminiEvaluationResponse response = objectMapper.readValue(json, GeminiEvaluationResponse.class);
            if (response == null || response.getFeedback() == null || response.getFeedback().trim().isEmpty()) {
                return getDefaultEvaluation(isLastQuestion);
            }
            if (response.getOverallScore() <= 0) {
                int avg = (response.getTechnicalScore() + response.getRelevanceScore() +
                        response.getClarityScore() + response.getDepthScore()) / 4;
                response.setOverallScore(Math.max(6, avg));
            }
            if (response.getStrengths() == null || response.getStrengths().isEmpty()) {
                response.setStrengths(List.of("Clear articulation of core concepts", "Accurate technical terminology"));
            }
            if (response.getWeaknesses() == null || response.getWeaknesses().isEmpty()) {
                response.setWeaknesses(List.of("Could elaborate deeper on architectural tradeoffs and failure handling"));
            }
            if (!isLastQuestion && (response.getFollowUpQuestion() == null || response.getFollowUpQuestion().trim().isEmpty())) {
                response.setFollowUpQuestion("How would you optimize this implementation for high concurrency and resilience under heavy load?");
                response.setFollowUpTechnology("Performance & Optimization");
                response.setFollowUpType("TECHNICAL");
            }
            return response;
        } catch (Exception e) {
            log.warn("Failed to parse Gemini evaluation JSON: {}. Using heuristic evaluation fallback.", json, e);
            return getDefaultEvaluation(isLastQuestion);
        }
    }

    public GeminiReportGenerationResponse parseReport(String rawResponse) {
        if (rawResponse == null || rawResponse.trim().isEmpty()) {
            return getDefaultReport();
        }
        String json = extractJsonText(rawResponse);
        try {
            GeminiReportGenerationResponse response = objectMapper.readValue(json, GeminiReportGenerationResponse.class);
            if (response == null || response.getSummary() == null || response.getSummary().trim().isEmpty() || response.getOverallScore() <= 0) {
                return getDefaultReport();
            }
            return response;
        } catch (Exception e) {
            log.warn("Failed to parse Gemini report JSON: {}. Using heuristic report fallback.", json, e);
            return getDefaultReport();
        }
    }

    public GeminiResumeAnalysisResponse parseResumeAnalysis(String rawResponse) {
        if (rawResponse == null || rawResponse.trim().isEmpty()) {
            return getDefaultResumeAnalysis();
        }
        String json = extractJsonText(rawResponse);
        try {
            GeminiResumeAnalysisResponse response = objectMapper.readValue(json, GeminiResumeAnalysisResponse.class);
            if (response == null || response.getDetectedSkills() == null || response.getDetectedSkills().isEmpty()) {
                return getDefaultResumeAnalysis();
            }
            return response;
        } catch (Exception e) {
            log.warn("Failed to parse Gemini resume analysis JSON: {}", json, e);
            return getDefaultResumeAnalysis();
        }
    }

    private GeminiQuestionGenerationResponse getDefaultQuestion(String defaultTopic) {
        String topic = defaultTopic != null ? defaultTopic : "General Engineering";
        String question;
        if (topic.contains("JAVA") || topic.contains("Java")) {
            question = "Can you explain how Dependency Injection works in Spring Boot and compare Bean creation scopes (Singleton vs Prototype)?";
        } else if (topic.contains("REACT") || topic.contains("React")) {
            question = "How does React Virtual DOM reconciliation work, and when should you use useMemo versus useCallback for performance?";
        } else if (topic.contains("DATABASE") || topic.contains("SQL")) {
            question = "Explain the difference between Clustered and Non-Clustered Indexes in SQL Server and how they affect query execution plans.";
        } else if (topic.contains("SYSTEM") || topic.contains("Design")) {
            question = "How would you design a rate limiter for a distributed backend API handling 100k requests per second?";
        } else {
            question = "Could you walk through how you handle exception handling, logging, and transaction boundaries in your backend applications?";
        }

        return GeminiQuestionGenerationResponse.builder()
                .question(question)
                .questionType("TECHNICAL")
                .technology(topic)
                .build();
    }

    private GeminiEvaluationResponse getDefaultEvaluation(boolean isLastQuestion) {
        return GeminiEvaluationResponse.builder()
                .technicalScore(8)
                .relevanceScore(8)
                .clarityScore(7)
                .depthScore(7)
                .overallScore(8)
                .feedback("Solid answer demonstrating good foundational understanding of core concepts. To elevate your answer, discuss specific production tradeoffs and edge-case handling.")
                .strengths(List.of("Accurate articulation of primary mechanics", "Good technical vocabulary"))
                .weaknesses(List.of("Could elaborate on concurrency and scaling tradeoffs"))
                .followUpQuestion(isLastQuestion ? null : "How would you optimize this approach under high concurrency and load?")
                .followUpTechnology("Performance & Architecture")
                .followUpType("TECHNICAL")
                .build();
    }

    private GeminiReportGenerationResponse getDefaultReport() {
        return GeminiReportGenerationResponse.builder()
                .overallScore(80.0)
                .technicalScore(82.0)
                .communicationScore(78.0)
                .relevanceScore(80.0)
                .averageAnswerScore(8.0)
                .strengths(List.of("Strong foundational knowledge", "Structured communication style", "Clear technical terminology"))
                .weaknesses(List.of("Production edge-cases and distributed failure handling could be deeper"))
                .recommendedTopics(List.of("JVM Garbage Collection Tuning", "Database Index Internals", "Distributed Transaction Patterns (Saga)"))
                .improvementSuggestions(List.of("Practice structuring answers using Context-Problem-Solution-Tradeoff format"))
                .summary("The candidate demonstrated competent engineering depth and clear communication suitable for technical placement roles.")
                .build();
    }

    private GeminiResumeAnalysisResponse getDefaultResumeAnalysis() {
        return GeminiResumeAnalysisResponse.builder()
                .candidateName("Candidate")
                .detectedSkills(List.of("Java", "Spring Boot", "SQL Server", "REST APIs", "React", "Docker"))
                .detectedProjects(List.of("Full Stack Web Application", "RESTful Microservices Backend"))
                .suggestedRole("JAVA_BACKEND_DEVELOPER")
                .summary("Demonstrated profile with strong backend and full-stack software development projects.")
                .build();
    }
}