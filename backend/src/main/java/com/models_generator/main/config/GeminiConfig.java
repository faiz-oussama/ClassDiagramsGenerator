package com.models_generator.main.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "gemini")
public class GeminiConfig {

    // Getter and Setter
    private String apiKey;

}
