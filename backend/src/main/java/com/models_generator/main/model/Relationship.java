package com.models_generator.main.model;

import com.models_generator.main.enums.RelationshipType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonBackReference;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Relationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromClassName;
    private String toClassName;

    @Enumerated(EnumType.STRING)
    private RelationshipType type;

    @JsonBackReference("diagram-relationships")
    @ManyToOne
    @JoinColumn(name = "diagram_id")
    private ClassDiagram classDiagram;
}
