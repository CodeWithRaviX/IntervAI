package com.aimock.interview;

import com.aimock.interview.ai.GeminiResponseParser;
import com.aimock.interview.dto.ai.GeminiEvaluationResponse;
import com.aimock.interview.dto.ai.GeminiQuestionGenerationResponse;
import com.aimock.interview.dto.ai.GeminiReportGenerationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeminiServiceTest {

    private GeminiResponseParser parser;

    @BeforeEach
    void setUp() {
        parser = new GeminiResponseParser();
    }

    @Test
    @DisplayName("Should parse structured JSON question response correctly")
    void testParseQuestionJson() {
        String json = "```json\n{\n  \"question\": \"Explain JPA vs Hibernate\",\n  \"questionType\": \"TECHNICAL\",\n  \"technology\": \"JPA\"\n}\n```";
        GeminiQuestionGenerationResponse res = parser.parseQuestion(json, "Java");

        assertNotNull(res);
        assertEquals("Explain JPA vs Hibernate", res.getQuestion());
        assertEquals("TECHNICAL", res.getQuestionType());
        assertEquals("JPA", res.getTechnology());
    }

    @Test
    @DisplayName("Should handle malformed JSON with reliable heuristic fallback")
    void testParseQuestionFallbackOnInvalidJson() {
        String invalid = "Not a json response";
        GeminiQuestionGenerationResponse res = parser.parseQuestion(invalid, "Java");

        assertNotNull(res);
        assertNotNull(res.getQuestion());
        assertFalse(res.getQuestion().isEmpty());
    }

    @Test
    @DisplayName("Should parse answer evaluation and adaptive follow-up")
    void testParseEvaluation() {
        String json = "{\n" +
                "  \"technicalScore\": 9,\n" +
                "  \"relevanceScore\": 8,\n" +
                "  \"clarityScore\": 8,\n" +
                "  \"depthScore\": 9,\n" +
                "  \"overallScore\": 9,\n" +
                "  \"feedback\": \"Excellent response with solid depth.\",\n" +
                "  \"strengths\": [\"Deep knowledge of Spring Bean lifecycle\"],\n" +
                "  \"weaknesses\": [\"None observed\"],\n" +
                "  \"followUpQuestion\": \"How do circular dependencies occur?\",\n" +
                "  \"followUpTechnology\": \"Spring Core\",\n" +
                "  \"followUpType\": \"TECHNICAL\"\n" +
                "}";

        GeminiEvaluationResponse eval = parser.parseEvaluation(json, false);

        assertNotNull(eval);
        assertEquals(9, eval.getTechnicalScore());
        assertEquals(9, eval.getOverallScore());
        assertEquals("How do circular dependencies occur?", eval.getFollowUpQuestion());
    }

    @Test
    @DisplayName("Should parse complete interview report")
    void testParseReport() {
        String json = "{\n" +
                "  \"overallScore\": 85.5,\n" +
                "  \"technicalScore\": 88.0,\n" +
                "  \"communicationScore\": 82.0,\n" +
                "  \"relevanceScore\": 86.0,\n" +
                "  \"averageAnswerScore\": 8.5,\n" +
                "  \"strengths\": [\"Strong backend fundamentals\"],\n" +
                "  \"weaknesses\": [\"Can expand on query optimization\"],\n" +
                "  \"recommendedTopics\": [\"Database indexes\"],\n" +
                "  \"improvementSuggestions\": [\"Practice system architecture trade-offs\"],\n" +
                "  \"summary\": \"Solid candidate overall.\"\n" +
                "}";

        GeminiReportGenerationResponse report = parser.parseReport(json);

        assertNotNull(report);
        assertEquals(85.5, report.getOverallScore());
        assertEquals(88.0, report.getTechnicalScore());
        assertEquals(1, report.getStrengths().size());
    }
}
