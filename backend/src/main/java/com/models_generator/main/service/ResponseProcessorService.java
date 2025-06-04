
package com.models_generator.main.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.models_generator.main.enums.RelationshipType;
import com.models_generator.main.enums.Visibility;
import com.models_generator.main.model.*;
import com.models_generator.main.repository.ClassDiagramRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

            List<Attribute> attributes = new ArrayList<>();
            for (JsonNode attrNode : entityNode.get("attributes")) {
                Attribute attribute = new Attribute();
                attribute.setName(attrNode.asText());
                attribute.setType("String"); // Default type
                attribute.setVisibility(Visibility.PRIVATE);
                attribute.setClassEntity(classEntity);
                attributes.add(attribute);
            }

            classEntity.setAttributes(attributes);
            classEntity.setMethods(new ArrayList<>());

            diagram.getClasses().add(classEntity);
            entityMap.put(entityName, classEntity);
        }

        // Parse relationships and infer methods
        for (JsonNode relNode : root.get("relationships")) {
            String subject = relNode.get("subject").asText();
            String object = relNode.get("object").asText();
            String verb = relNode.get("verb").asText();

            // Create relationship
            Relationship relationship = new Relationship();
            relationship.setFromClassName(subject);
            relationship.setToClassName(object);
            relationship.setType(mapVerbToRelationType(verb));
            relationship.setClassDiagram(diagram);
            diagram.getRelationships().add(relationship);

            // Infer and add method
            Method method = inferMethodFromVerb(verb, object);
            if (method != null) {
                ClassEntity subjectEntity = entityMap.get(subject);
                if (subjectEntity != null) {
                    method.setClassEntity(subjectEntity);
                    subjectEntity.getMethods().add(method);
                }
            }
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

    /**
     * Infers a method from a relationship verb and the related object name.
     */
    private Method inferMethodFromVerb(String verb, String object) {
        Method method = new Method();
        method.setVisibility(Visibility.PUBLIC);
        method.setParameters(new ArrayList<>());

        String camelObject = Character.toLowerCase(object.charAt(0)) + object.substring(1);
        String capitalObject = Character.toUpperCase(object.charAt(0)) + object.substring(1);

        return switch (verb.toLowerCase()) {
            case "creates" -> {
                method.setName("create" + capitalObject);
                method.setReturnType(capitalObject);
                yield method;
            }
            case "owns" -> {
                method.setName("getOwned" + capitalObject + "s");
                method.setReturnType("List<" + capitalObject + ">");
                yield method;
            }
            case "is_assigned_to", "assigned_to" -> {
                method.setName("assign" + capitalObject);
                method.setReturnType("void");
                method.getParameters().add(camelObject);
                yield method;
            }
            case "has_many" -> {
                method.setName("get" + capitalObject + "List");
                method.setReturnType("List<" + capitalObject + ">");
                yield method;
            }
            case "uploads" -> {
                method.setName("upload" + capitalObject);
                method.setReturnType("void");
                method.getParameters().add(camelObject);
                yield method;
            }
            default -> null;
        };
    }

    private String cleanJson(String aiResponse) {
        if (aiResponse.startsWith("```json") || aiResponse.startsWith("```")) {
            aiResponse = aiResponse.replaceFirst("(?s)^```(?:json)?", "");
            aiResponse = aiResponse.replaceFirst("```\\s*$", "");
        }
        return aiResponse.trim();
    }
}
