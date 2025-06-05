// src/main/java/com/models_generator/main/mapper/DiagramMapper.java
package com.models_generator.main.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.models_generator.main.DTO.DiagramDTO;
import com.models_generator.main.model.ClassDiagram;
import com.models_generator.main.model.ClassEntity;
import com.models_generator.main.model.Relationship;

@Component
public class DiagramMapper {
    public DiagramDTO toDTO(ClassDiagram diagram) {
        DiagramDTO dto = new DiagramDTO();
        dto.setId(diagram.getId());
        dto.setTitle(diagram.getTitle());
        
        // Map classes
        dto.setClasses(diagram.getClasses().stream()
                .map(this::mapClass)
                .collect(Collectors.toList()));
        
        // Map relationships
        dto.setRelationships(diagram.getRelationships().stream()
                .map(this::mapRelationship)
                .collect(Collectors.toList()));
        
        return dto;
    }

    // Cette partie est dédiée à la conversion des entités en DTO au format specifique de la librairie de front
    
    private DiagramDTO.ClassElement mapClass(ClassEntity entity) {
        DiagramDTO.ClassElement element = new DiagramDTO.ClassElement();
        element.setId("class_" + entity.getId());
        element.setName(entity.getName());
        
        // Map attributes
        element.setAttributes(entity.getAttributes().stream()
                .map(attr -> {
                    DiagramDTO.AttributeElement attrDto = new DiagramDTO.AttributeElement();
                    attrDto.setName(attr.getName());
                    attrDto.setType(attr.getType());
                    attrDto.setVisibility(attr.getVisibility().name());
                    return attrDto;
                })
                .collect(Collectors.toList()));
        
        // Map methods
        element.setMethods(entity.getMethods().stream()
                .map(method -> {
                    DiagramDTO.MethodElement methodDto = new DiagramDTO.MethodElement();
                    methodDto.setName(method.getName());
                    methodDto.setReturnType(method.getReturnType());
                    methodDto.setParameters(method.getParameters());
                    return methodDto;
                })
                .collect(Collectors.toList()));
        
        // Default position car la librairie de front attends une position précise
        // pour chaque entite
        DiagramDTO.Position position = new DiagramDTO.Position();
        position.setX((int)(Math.random() * 500));
        position.setY((int)(Math.random() * 500)); 
        element.setPosition(position);
        
        return element;
    }
    
    private DiagramDTO.RelationshipElement mapRelationship(Relationship relationship) {
        DiagramDTO.RelationshipElement element = new DiagramDTO.RelationshipElement();
        element.setId("rel_" + relationship.getId());
        element.setSource("class_" + relationship.getFromClassName());
        element.setTarget("class_" + relationship.getToClassName());
        element.setType(relationship.getType().name());
        return element;
    }
}