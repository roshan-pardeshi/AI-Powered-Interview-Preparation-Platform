package com.interview.platform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.platform.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    @Value("${gemini.api-key}")
    private String geminiApiKey;

    @Value("${gemini.api-url:https://api.openai.com/v1/chat/completions}")
    private String geminiApiUrl;

    @Value("${gemini.model:gpt-4o-mini}")
    private String geminiModel;

    private String callModel(String prompt) {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            throw new BadRequestException("Gemini API key is not configured");
        }

        try {
            Map<String, Object> requestBody = Map.of(
                    "model", geminiModel,
                    "messages", List.of(Map.of("role", "user", "content", prompt)),
                    "temperature", 0.7,
                    "max_tokens", 800
            );

            String body = objectMapper.writeValueAsString(requestBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(geminiApiUrl))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + geminiApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.error("AI API returned HTTP {}: {}", response.statusCode(), response.body());
                throw new BadRequestException("AI service returned status " + response.statusCode());
            }

            JsonNode json = objectMapper.readTree(response.body());
            if (json.has("choices") && json.get("choices").isArray() && json.get("choices").size() > 0) {
                JsonNode choice = json.get("choices").get(0);
                if (choice.has("message") && choice.get("message").has("content")) {
                    return choice.get("message").get("content").asText();
                }
                if (choice.has("text")) {
                    return choice.get("text").asText();
                }
            }
            throw new BadRequestException("AI service returned an unexpected response shape");
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error calling AI service", e);
            throw new BadRequestException("Error calling AI service: " + e.getMessage());
        }
    }

    public String analyzeResume(String resumeText) {
        String prompt = "Analyze this resume and provide a JSON response with the following fields: " +
                "score (0-100), strongSkills (array), weakSkills (array), missingSkills (array), suggestions (array). " +
                "Resume text: " + resumeText;
        return callModel(prompt);
    }

    public String analyzeJobMatch(String resumeText, String jobDescription) {
        String prompt = "Compare this resume with the job description and provide a JSON response with: " +
                "matchScore (0-100), matchedSkills (array), missingSkills (array), suggestions (array). " +
                "Resume: " + resumeText + "\n\nJob Description: " + jobDescription;
        return callModel(prompt);
    }

    public String generateInterviewQuestions(String company, String technology, String difficulty) {
        String prompt = "Generate 10 interview questions for a " + difficulty + " level " + technology +
                " position at " + company + ". Provide a JSON response with an array of questions. " +
                "Each question should have a 'text' field and a 'category' field (HR, TECHNICAL, or CODING).";
        return callModel(prompt);
    }

    public String generateFollowUpQuestion(String previousAnswer, String context) {
        String prompt = "Based on this answer to an interview question, generate a relevant follow-up question. " +
                "Context: " + context + "\n\nAnswer: " + previousAnswer + "\n\nProvide only the follow-up question text.";
        return callModel(prompt);
    }

    public String evaluateAnswer(String question, String answer, String category) {
        String prompt = "Evaluate this interview answer and provide JSON feedback with: " +
                "score (0-100), feedback (string), strengths (array), improvements (array). " +
                "Question: " + question + "\n\nAnswer: " + answer + "\n\nCategory: " + category;
        return callModel(prompt);
    }
}
