package com.models_generator.main.controller;

import java.util.concurrent.CompletableFuture;

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

        // Parse diagram data and link it to the response
        try {
            JsonNode diagramData = objectMapper.readTree(cleanedJson);
            response.setDiagramData(diagramData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // mora madazt data l frontend ankhli l process and parsing w saving f database asyncrone bach tkon reponse rapid 
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

