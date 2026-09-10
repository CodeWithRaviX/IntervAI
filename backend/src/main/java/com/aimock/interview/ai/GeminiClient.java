package com.aimock.interview.ai;

import com.aimock.interview.exception.AiServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GeminiClient {

    private final RestClient geminiRestClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.model:gemini-3.6-flash}")
    private String model;

    @Value("${gemini.api.base-url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String baseUrl;

    private static final List<String> BACKUP_MODELS = List.of("gemini-3.6-flash", "gemini-3.5-flash", "gemini-3.7-flash");

    public String generateContent(String prompt) {
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.equals("YOUR_GEMINI_API_KEY") || apiKey.equals("test-gemini-key")) {
            log.info("Gemini API key not configured or in test mode. Falling back to structured heuristic generation.");
            return null;
        }

        List<String> modelsToTry = new ArrayList<>();
        if (model != null && !model.trim().isEmpty()) {
            modelsToTry.add(model.trim());
        }
        for (String backup : BACKUP_MODELS) {
            if (!modelsToTry.contains(backup)) {
                modelsToTry.add(backup);
            }
        }

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                ),
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "responseMimeType", "application/json"
                )
        );

        for (String targetModel : modelsToTry) {
            try {
                String url = String.format("%s/%s:generateContent?key=%s", baseUrl, targetModel, apiKey);
                log.debug("Calling Gemini API with model: {}", targetModel);

                byte[] responseBytes = geminiRestClient.post()
                        .uri(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON, MediaType.ALL)
                        .body(requestBody)
                        .retrieve()
                        .body(byte[].class);

                if (responseBytes != null && responseBytes.length > 0) {
                    String response = new String(responseBytes, java.nio.charset.StandardCharsets.UTF_8);
                    if (!response.trim().isEmpty()) {
                        log.info("Successfully received AI response from model: {}", targetModel);
                        return response;
                    }
                }
            } catch (Exception e) {
                log.warn("Error communicating with Gemini API model {}: {}. Trying next candidate model if available.", targetModel, e.getMessage());
            }
        }

        log.error("All Gemini API models failed for the request. Returning null for heuristic fallback.");
        return null;
    }
}
