package com.models_generator.main.controller;

import com.models_generator.main.model.ClassDiagram;
import com.models_generator.main.service.AIService;
import com.models_generator.main.service.ResponseProcessorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PromptController {

    private final AIService aiService;
    private final ResponseProcessorService processorService;

    public PromptController(AIService aiService, ResponseProcessorService processorService) {
        this.aiService = aiService;
        this.processorService = processorService;
    }

    @PostMapping("/ask")
    public ResponseEntity<?> askAI(@RequestBody Map<String, String> request) {
        try {
            String userPrompt = request.get("prompt");
            String title = request.getOrDefault("title", "Untitled Diagram");

            String aiRawResponse = aiService.getAiResponse(userPrompt);

            ClassDiagram savedDiagram = processorService.processResponse(aiRawResponse, title);

            return ResponseEntity.ok(savedDiagram);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}

