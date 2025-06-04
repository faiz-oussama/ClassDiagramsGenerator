package com.models_generator.main.service;

import com.models_generator.main.config.SystemPromptConfig;
import com.models_generator.main.model.Prompt;
import com.models_generator.main.model.User;
import com.models_generator.main.repository.PromptRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PromptService {

    private final SystemPromptConfig systemPromptConfig;

    private final PromptRepository promptRepository;

    public PromptService(SystemPromptConfig systemPromptConfig, PromptRepository promptRepository) {
        this.systemPromptConfig = systemPromptConfig;
        this.promptRepository = promptRepository;
    }

    public void SavePrompt(String promptText, User user){
        Prompt prompt = new Prompt(null ,promptText, user, null, null);
        promptRepository.save(prompt);
    }

    public List<Map<String, Object>> prepareFinalPrompt(String userPrompt) {
        List<Map<String, Object>> messages = new ArrayList<>();

        // System message
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("parts", List.of(Map.of("text", systemPromptConfig.getPrompt())));

        // User message
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("parts", List.of(Map.of("text", userPrompt)));

        messages.add(systemMessage);
        messages.add(userMessage);

        return messages;
    }

}
