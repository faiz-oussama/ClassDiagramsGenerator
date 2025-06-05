package com.models_generator.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.models_generator.main.config.GeminiConfig;
import com.models_generator.main.config.SystemPromptConfig;

@SpringBootApplication
@EnableConfigurationProperties({SystemPromptConfig.class , GeminiConfig.class})
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

}
