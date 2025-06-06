package com.models_generator.main.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.models_generator.main.enums.RelationshipType;
import com.models_generator.main.enums.Visibility;
import com.models_generator.main.model.Attribute;
import com.models_generator.main.model.ClassDiagram;
import com.models_generator.main.model.ClassEntity;
import com.models_generator.main.model.Method;
import com.models_generator.main.model.Relationship;
import com.models_generator.main.repository.ClassDiagramRepository;

@Service
public class ResponseProcessorService {

    private final ClassDiagramRepository classDiagramRepository;

    public ResponseProcessorService(ClassDiagramRepository classDiagramRepository) {
        this.classDiagramRepository = classDiagramRepository;
    }



    /**
     * Processes the Gemini JSON response and saves the diagram to the database.
     */
    @Transactional
    public ClassDiagram processResponse(String json, String diagramTitle) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        // Clean JSON first 
        json = cleanJson(json);

        JsonNode root = mapper.readTree(json);

        ClassDiagram diagram = new ClassDiagram();
        diagram.setTitle(diagramTitle);
        diagram.setClasses(new ArrayList<>());
        diagram.setRelationships(new ArrayList<>());

        // Save diagram first to handle foreign key constraints
        diagram = classDiagramRepository.save(diagram);

        Map<String, ClassEntity> entityMap = new HashMap<>();

        // Parse entities
        for (JsonNode entityNode : root.get("entities")) {
            String entityName = entityNode.get("name").asText();

            ClassEntity classEntity = new ClassEntity();
            classEntity.setName(entityName);
            classEntity.setClassDiagram(diagram);

            // Parse attributes
            List<Attribute> attributes = new ArrayList<>();
            for (JsonNode attrNode : entityNode.get("attributes")) {
                Attribute attribute = new Attribute();
                String fullAttr = attrNode.asText();

                String[] parts = fullAttr.split(":");
                String attrName = parts[0].trim();
                String attrType = parts.length > 1 ? parts[1].trim() : "String";

                attribute.setName(attrName);
                attribute.setType(attrType);
                attribute.setVisibility(Visibility.PRIVATE);
                attribute.setClassEntity(classEntity);
                attributes.add(attribute);
            }
            classEntity.setAttributes(attributes);

            // Parse methods
            List<Method> methods = new ArrayList<>();
            if (entityNode.has("methods")) {
                for (JsonNode methodNode : entityNode.get("methods")) {
                    Method method = new Method();
                    method.setName(methodNode.get("name").asText());
                    method.setReturnType(methodNode.has("returnType") ? methodNode.get("returnType").asText() : "void");
                    method.setVisibility(Visibility.PUBLIC); // Default or configurable

                    // Parse parameters as list of strings
                    List<String> parameters = new ArrayList<>();
                    if (methodNode.has("parameters")) {
                        for (JsonNode paramNode : methodNode.get("parameters")) {
                            parameters.add(paramNode.asText());
                        }
                    }
                    method.setParameters(parameters);
                    method.setClassEntity(classEntity);

                    methods.add(method);
                }
            }
            classEntity.setMethods(methods);

            diagram.getClasses().add(classEntity);
            entityMap.put(entityName, classEntity);
        }

        // Parse relationships
        for (JsonNode relNode : root.get("relationships")) {
            String subject = relNode.get("subject").asText();
            String object = relNode.get("object").asText();
            String verb = relNode.get("verb").asText();

            Relationship relationship = new Relationship();
            relationship.setFromClassName(subject);
            relationship.setToClassName(object);
            relationship.setType(mapVerbToRelationType(verb));
            relationship.setClassDiagram(diagram);

            diagram.getRelationships().add(relationship);
        }

        return classDiagramRepository.save(diagram);
    }


    /**
     * Maps common relationship verbs to a RelationshipType enum.
     */
    private RelationshipType mapVerbToRelationType(String verb) {
        return switch (verb.toLowerCase()) {
            case "owns" -> RelationshipType.ASSOCIATION;
            case "creates" -> RelationshipType.DEPENDENCY;
            case "assigned_to", "is_assigned_to" -> RelationshipType.ASSOCIATION;
            case "has_many" -> RelationshipType.AGGREGATION;
            case "belongs_to" -> RelationshipType.ASSOCIATION;
            case "uploads" -> RelationshipType.DEPENDENCY;
            default -> RelationshipType.ASSOCIATION;
        };
    }


    // Clean JSON response
    public String cleanJson(String aiResponse) {
        if (aiResponse.startsWith("```json") || aiResponse.startsWith("```")) {
            aiResponse = aiResponse.replaceFirst("(?s)^```(?:json)?", "");
            aiResponse = aiResponse.replaceFirst("```\\s*$", "");
        }
        return aiResponse.trim();
    }
}
