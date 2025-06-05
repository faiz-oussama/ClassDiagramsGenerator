package com.models_generator.main.controller;

import com.models_generator.main.service.AIService;
import com.models_generator.main.service.ResponseProcessorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.models_generator.main.DTO.ApiResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api")
public class PromptController {

    private final AIService aiService;
    private final ResponseProcessorService processorService;
    private final ObjectMapper objectMapper;

    public PromptController(AIService aiService, ResponseProcessorService processorService, ObjectMapper objectMapper) {
        this.aiService = aiService;
        this.processorService = processorService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/ask")
    public ResponseEntity<ApiResponse> askAI(@RequestParam String prompt, @RequestParam String title) {
        ApiResponse response = new ApiResponse();

        String aiRawResponse = aiService.getAiResponse(prompt);
        
        String cleanedJson = processorService.cleanJson(aiRawResponse);
        try {
            JsonNode diagramData = objectMapper.readTree(cleanedJson);
            response.setDiagramData(diagramData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


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

