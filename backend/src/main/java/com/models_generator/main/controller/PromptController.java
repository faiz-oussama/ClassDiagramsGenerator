package com.models_generator.main.controller;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.models_generator.main.service.DiagramCachingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.models_generator.main.DTO.ApiResponse;
import com.models_generator.main.service.AIService;
import com.models_generator.main.service.ResponseProcessorService;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api")
public class PromptController {

    private final AIService aiService;
    private final ResponseProcessorService processorService;
    private final ObjectMapper objectMapper;
    private final DiagramCachingService diagramCachingService;

    public PromptController(AIService aiService, ResponseProcessorService processorService, ObjectMapper objectMapper, DiagramCachingService diagramCachingService) {
        this.aiService = aiService;
        this.processorService = processorService;
        this.objectMapper = objectMapper;
        this.diagramCachingService = diagramCachingService;
    }

    @PostMapping("/ask")
    public ResponseEntity<ApiResponse> askAI(@RequestParam String prompt,
                                             @RequestParam String title,
                                             HttpServletRequest request) {
        ApiResponse response = new ApiResponse();

        //  Get session ID
        String sessionId = request.getSession().getId();

        // Check if there is a previous diagram
        Optional<String> previousDiagram = diagramCachingService.getLatestDiagram(sessionId);
        String fullPrompt = previousDiagram
                .map(prev -> "Given this class diagram:\n" + prev + "\n\nNow: " + prompt)
                .orElse(prompt);

        String aiRawResponse = aiService.getAiResponse(fullPrompt);
        
        String cleanedJson = processorService.cleanJson(aiRawResponse);

        diagramCachingService.saveOrUpdateDiagram(sessionId, cleanedJson);
        // Parse diagram data and link it to the response
        try {
            JsonNode diagramData = objectMapper.readTree(cleanedJson);
            response.setDiagramData(diagramData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // mora madazt data l frontend ankhli l process d parsing w saving f database asynchrone bach tkon reponse rapid
        // w makatsnach 7ta data t sava l database 3ad t rediregik l frontend
        CompletableFuture.runAsync(() -> {
        try {
            processorService.processResponse(cleanedJson, title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
        
        return ResponseEntity.ok(response);
    }
    
}

