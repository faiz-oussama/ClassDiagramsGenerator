package com.models_generator.main.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    private final RestTemplate restTemplate;
    private final PromptService promptService;

    @Value("${gemini.api.key}")
    private String apiKey;

    public AIService(RestTemplate restTemplate, PromptService promptService) {
        this.restTemplate = restTemplate;
        this.promptService = promptService;
    }

    public String getAiResponse(String userPrompt) {
        List<Map<String, Object>> contents = promptService.prepareFinalPrompt(userPrompt);

        // Separate system instructions from user contents
        List<Map<String, Object>> userContents = new ArrayList<>();
        Map<String, Object> systemInstruction = null;

        for (Map<String, Object> content : contents) {
            String role = (String) content.get("role");
            if ("system".equals(role)) {
                // Convert system message to systemInstruction format
                systemInstruction = new HashMap<>();
                systemInstruction.put("parts", content.get("parts"));
            } else {
                // Keep user/model messages in contents
                userContents.add(content);
            }
        }

        // Create request body with proper structure
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", userContents);

        // Add system instruction if present
        if (systemInstruction != null) {
            requestBody.put("systemInstruction", systemInstruction);
        }

        // Add generation config for better responses
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("topK", 40);
        generationConfig.put("topP", 0.95);
        generationConfig.put("maxOutputTokens", 8192);
        requestBody.put("generationConfig", generationConfig);

        // Build HTTP request with proper headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Don't use Bearer auth for Gemini - the API key goes in the URL

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // Use the API key in the URL parameter, not in headers
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent?key=" + apiKey;

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            // Parse response with better error handling
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                // Check for error in response
                if (body.containsKey("error")) {
                    Map<String, Object> error = (Map<String, Object>) body.get("error");
                    System.err.println("Gemini API Error: " + error.get("message"));
                    return "Error from Gemini: " + error.get("message");
                }

                List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> firstCandidate = candidates.get(0);

                    // Check for finish reason
                    String finishReason = (String) firstCandidate.get("finishReason");
                    if (finishReason != null && !finishReason.equals("STOP")) {
                        System.err.println("Generation finished with reason: " + finishReason);
                    }

                    Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
                    if (content != null) {
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                        if (parts != null && !parts.isEmpty()) {
                            return (String) parts.get(0).get("text");
                        }
                    }
                }
            } else {
                System.err.println("HTTP Error: " + response.getStatusCode());
                return "HTTP Error: " + response.getStatusCode();
            }
        } catch (Exception e) {
            System.err.println("Exception calling Gemini API: " + e.getMessage());
            e.printStackTrace();
            return "Error calling Gemini API: " + e.getMessage();
        }

        return "No response from Gemini.";
    }
}