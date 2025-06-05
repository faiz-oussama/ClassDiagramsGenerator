package com.models_generator.main.DTO;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class ApiResponse {
    private JsonNode diagramData; 
    private DiagramDTO diagram;   
}