package com.aimock.interview.ai;

import com.aimock.interview.entity.Difficulty;
import com.aimock.interview.entity.ExperienceLevel;
import com.aimock.interview.entity.InterviewType;
import com.aimock.interview.entity.JobRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class GeminiPromptBuilder {

    public String buildFirstQuestionPrompt(
            JobRole jobRole,
            ExperienceLevel experienceLevel,
            Difficulty difficulty,
            InterviewType interviewType,
            String resumeContext) {

        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert technical interviewer conducting a mock interview for the role of '")
                .append(jobRole.name().replace('_', ' ')).append("'.\n")
                .append("Experience Level: ").append(experienceLevel.name()).append("\n")
                .append("Difficulty: ").append(difficulty.name()).append("\n")
                .append("Interview Type: ").append(interviewType.name()).append("\n\n");

        if (resumeContext != null && !resumeContext.trim().isEmpty()) {
            sb.append("Candidate Resume Highlights:\n")
                    .append(resumeContext.length() > 2000 ? resumeContext.substring(0, 2000) : resumeContext)
                    .append("\n\nTailor the question to the candidate's stated background where appropriate.\n\n");
        }

        sb.append("Generate the FIRST interview question.\n")
                .append("The question should test core foundational concepts appropriate for the candidate's experience level.\n\n")
                .append("Output STRICTLY valid JSON with the following schema:\n")
                .append("{\n")
                .append("  \"question\": \"The exact interview question text\",\n")
                .append("  \"questionType\": \"TECHNICAL | BEHAVIORAL | SYSTEM_DESIGN | SCENARIO\",\n")
                .append("  \"technology\": \"Core technology or domain topic\"\n")
                .append("}");

        return sb.toString();
    }

    public String buildEvaluationAndAdaptiveFollowUpPrompt(
            JobRole jobRole,
            ExperienceLevel experienceLevel,
            Difficulty difficulty,
            InterviewType interviewType,
            String currentQuestion,
            String candidateAnswer,
            List<Map<String, String>> previousQuestionsAndAnswers,
            boolean isLastQuestion) {

        StringBuilder sb = new StringBuilder();
        sb.append("You are an objective senior interviewer evaluating a candidate for the role of '")
                .append(jobRole.name().replace('_', ' ')).append("'.\n")
                .append("Experience Level: ").append(experienceLevel.name()).append("\n")
                .append("Difficulty: ").append(difficulty.name()).append("\n")
                .append("Interview Type: ").append(interviewType.name()).append("\n\n");

        if (previousQuestionsAndAnswers != null && !previousQuestionsAndAnswers.isEmpty()) {
            sb.append("Previous Questions & Candidate Answers Summary:\n");
            for (Map<String, String> qa : previousQuestionsAndAnswers) {
                sb.append("Q: ").append(qa.get("question")).append("\n")
                  .append("A: ").append(qa.get("answer")).append("\n")
                  .append("Score: ").append(qa.get("score")).append("/10\n\n");
            }
        }

        sb.append("CURRENT QUESTION:\n").append(currentQuestion).append("\n\n")
                .append("CANDIDATE ANSWER:\n").append(candidateAnswer).append("\n\n")
                .append("TASK:\n")
                .append("1. Score the answer from 1 to 10 across 4 dimensions: technicalScore, relevanceScore, clarityScore, depthScore.\n")
                .append("2. Calculate overallScore as the rounded average of the 4 dimension scores (1-10 integer).\n")
                .append("3. Provide constructivist, educational feedback.\n")
                .append("4. List key strengths (bullet points) and areas for improvement/weaknesses.\n");

        if (!isLastQuestion) {
            sb.append("5. Generate an ADAPTIVE follow-up question. If the candidate scored high (>=8), probe deeper or increase difficulty. If the candidate struggled (<=5), ask a clarifying question or shift to an adjacent core concept. Avoid repetitive questions.\n");
        } else {
            sb.append("5. Since this was the final question, set followUpQuestion to null.\n");
        }

        sb.append("\nOutput STRICTLY valid JSON with the following schema:\n")
                .append("{\n")
                .append("  \"technicalScore\": 8,\n")
                .append("  \"relevanceScore\": 9,\n")
                .append("  \"clarityScore\": 7,\n")
                .append("  \"depthScore\": 8,\n")
                .append("  \"overallScore\": 8,\n")
                .append("  \"feedback\": \"Detailed constructive critique...\",\n")
                .append("  \"strengths\": [\"Strength 1\", \"Strength 2\"],\n")
                .append("  \"weaknesses\": [\"Area for improvement 1\"],\n")
                .append("  \"followUpQuestion\": ").append(isLastQuestion ? "null" : "\"Next question text...\"").append(",\n")
                .append("  \"followUpTechnology\": ").append(isLastQuestion ? "null" : "\"Technology/Topic\"").append(",\n")
                .append("  \"followUpType\": ").append(isLastQuestion ? "null" : "\"TECHNICAL | BEHAVIORAL\"").append("\n")
                .append("}");

        return sb.toString();
    }

    public String buildReportPrompt(
            JobRole jobRole,
            ExperienceLevel experienceLevel,
            Difficulty difficulty,
            List<Map<String, Object>> qaHistory) {

        StringBuilder sb = new StringBuilder();
        sb.append("You are the chief evaluator synthesizing a complete mock interview session for role: '")
                .append(jobRole.name().replace('_', ' ')).append("'.\n")
                .append("Experience Level: ").append(experienceLevel.name()).append("\n")
                .append("Difficulty: ").append(difficulty.name()).append("\n\n")
                .append("Full Session History:\n");

        for (Map<String, Object> item : qaHistory) {
            sb.append("Question: ").append(item.get("question")).append("\n")
              .append("Answer: ").append(item.get("answer")).append("\n")
              .append("Scores: Tech=").append(item.get("technicalScore"))
              .append(", Relevance=").append(item.get("relevanceScore"))
              .append(", Clarity=").append(item.get("clarityScore"))
              .append(", Depth=").append(item.get("depthScore")).append("\n\n");
        }

        sb.append("TASK:\n")
                .append("Synthesize a comprehensive, executive-level final evaluation report.\n")
                .append("Calculate aggregate normalized 0-100 scores for overallScore, technicalScore, communicationScore, relevanceScore, and averageAnswerScore.\n")
                .append("Identify overall strengths, critical weaknesses, recommended preparation topics, actionable improvement suggestions, and an executive summary.\n\n")
                .append("Output STRICTLY valid JSON with the following schema:\n")
                .append("{\n")
                .append("  \"overallScore\": 82.5,\n")
                .append("  \"technicalScore\": 85.0,\n")
                .append("  \"communicationScore\": 78.0,\n")
                .append("  \"relevanceScore\": 84.0,\n")
                .append("  \"averageAnswerScore\": 8.2,\n")
                .append("  \"strengths\": [\"Deep understanding of Spring dependency injection\", \"Clear explanations of transactions\"],\n")
                .append("  \"weaknesses\": [\"Shallow explanation of database indexing under heavy load\"],\n")
                .append("  \"recommendedTopics\": [\"B-Tree Index Internals\", \"JVM Garbage Collection tuning\"],\n")
                .append("  \"improvementSuggestions\": [\"Practice articulating time complexity tradeoffs for backend queries\"],\n")
                .append("  \"summary\": \"The candidate demonstrated strong proficiency in core backend architectures with minor gaps in distributed transaction handling.\"\n")
                .append("}");

        return sb.toString();
    }

    public String buildResumeAnalysisPrompt(String resumeText) {
        return "You are an AI technical recruiter parsing an engineering resume.\n" +
                "Resume Content:\n" +
                (resumeText.length() > 4000 ? resumeText.substring(0, 4000) : resumeText) + "\n\n" +
                "TASK:\n" +
                "Extract detected skills, detected key projects, and suggest the most matching target job role.\n\n" +
                "Output STRICTLY valid JSON with schema:\n" +
                "{\n" +
                "  \"candidateName\": \"Candidate name if found or 'Candidate'\",\n" +
                "  \"detectedSkills\": [\"Java\", \"Spring Boot\", \"Docker\", \"SQL Server\"],\n" +
                "  \"detectedProjects\": [\"E-commerce Microservices\", \"Real-time Chat App\"],\n" +
                "  \"suggestedRole\": \"JAVA_BACKEND_DEVELOPER\",\n" +
                "  \"summary\": \"Brief overview of the candidate's profile\"\n" +
                "}";
    }
}
