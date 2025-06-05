package com.models_generator.main.controller;

import com.models_generator.main.repository.ClassDiagramRepository;
import com.models_generator.main.mapper.DiagramMapper;
import com.models_generator.main.DTO.DiagramDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/diagrams")
@RequiredArgsConstructor
public class DiagramController {
    private final ClassDiagramRepository diagramRepository;
    private final DiagramMapper diagramMapper;

    @GetMapping("/{id}")
    public ResponseEntity<DiagramDTO> getDiagram(@PathVariable Long id) {
        return diagramRepository.findById(id)
                .map(diagramMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}